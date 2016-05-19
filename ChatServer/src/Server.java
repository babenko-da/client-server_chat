import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public class Server extends JFrame {

    private ArrayList<ClientHandler> clients;
    ServerSocket server = null;
    JTable jTab;

    public Server() {
        setSize(800, 600);
        setLocation(700, 300);
        setTitle("Server");
        SQLTransport.connect();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JButton jbExit = new JButton("CLOSE SERVER & EXIT");
        add(jbExit, BorderLayout.SOUTH);
        jbExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    stop();
                } finally {
                    System.exit(0);
                }
            }
        });
        String[] mHeader = {"ID", "Name"};
        Object[][] mMain = {{0, "Empty"}};
        jTab = new JTable(new DefaultTableModel(mMain, mHeader));
        JScrollPane sp = new JScrollPane(jTab);
        sp.setViewportView(jTab);
        add(sp, BorderLayout.CENTER);


        setVisible(true);

        try {
            int x = 0;
            server = new ServerSocket(8190);
            clients = new ArrayList<ClientHandler>();
            System.out.println("Port 8189 Listening. Waiting for Connection...");
            while (true) {
                Socket s = server.accept();
                ClientHandler ch = new ClientHandler(s, this);
                registerHandler(ch);
                new Thread(ch).start();
                System.out.println("Client connected");
                updateData();
                if (x > 10000) break;
            }
            System.out.println("Connection closed");
            stop();
        } catch (SocketException e) {
            System.out.println("Server closed");
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    public void updateData() {
        ((DefaultTableModel)jTab.getModel()).setRowCount(0);
        int i = 0;
        for (ClientHandler o : clients) {
            i++;
            ((DefaultTableModel) jTab.getModel()).addRow(new Object[]{i, o.getName()});
        }
    }

    public void registerHandler(ClientHandler ch) {
        clients.add(ch);
        updateData();
    }

    public void unregisterHandler(ClientHandler ch) {
        clients.remove(ch);
        updateData();
    }

    public void broadcastMsg(String str) {
        // ArrayList<ClientHandler> clients;
        for (ClientHandler o : clients)
            o.sendMsg(str);
    }

    public void stop() {
        try {
            for (ClientHandler o : clients)
                o.stop();
            server.close();
            SQLTransport.closeConnection();
            System.out.println("Server closed");
        } catch (IOException e) {
            System.out.println("E");
        }
    }

}
