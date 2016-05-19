import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by dmitrybabenko on 4/16/16.
 */
public class ChatWindow extends JFrame {
    private DataOutputStream out;

    private JTextArea jta;
    private JTextField jtf;

    private Socket server;
    private ServerHandler serverHandler;

    public ChatWindow() throws HeadlessException {
        //ui
        setTitle("Chat Client");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jta = new JTextArea();
        jta.setAutoscrolls(true);
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setAutoscrolls(true);

        DefaultCaret caret = (DefaultCaret)jta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(jsp);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        add(southPanel, BorderLayout.SOUTH);

        JButton jb = new JButton("SEND");
        jtf = new JTextField();
        southPanel.add(jb, BorderLayout.EAST);
        southPanel.add(jtf, BorderLayout.CENTER);

        //connections
        startClient();

        //events
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jtf.getText().isEmpty()) {
                    sendMessage(jtf.getText());
                    showMessage("<"+jtf.getText());
                    jtf.setText("");
                }
            }
        });
        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jtf.getText().isEmpty()) {
                    sendMessage(jtf.getText());
                    showMessage("<"+jtf.getText());
                    jtf.setText("");
                }
            }
        });

        setVisible(true);
    }

    public void startClient() {
        try {
            server = new Socket("localhost", 8189);
            serverHandler = new ServerHandler(server, new MessageListener() {
                @Override
                public void incomingMessage(String mess) {
                    jta.append(String.format(">%s\n",mess));
                }
            });
            serverHandler.start();
            out = new DataOutputStream(server.getOutputStream());

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);

                    try {
                        sendMessage("End");
                        out.close();
                        serverHandler.interrupt();
                        server.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch(IOException exception) {
            exception.printStackTrace();
            showMessage("Error connect to server");
        }
    }

    public void sendMessage(String mess) {
        try {
            out.writeUTF(mess);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error send msg!");
        }
    }

    public void showMessage(String msg) {
        jta.append(String.format("\n",msg));
    }
}
