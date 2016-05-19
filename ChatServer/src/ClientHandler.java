import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dmitrybabenko on 4/22/16.
 */
public class ClientHandler implements Runnable{
    public static int CLIENTS_COUNT = 0;

    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private String name;
    private Server server;

    public ClientHandler(Socket s, Server server) {
        CLIENTS_COUNT ++;
        socket = s;
        name = "Client #"+CLIENTS_COUNT;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            sendMsg("Hello, User!");
            String inString = "";
            while (!inString.equals("end")) {
                if (in.hasNext()) {
                    inString = in.nextLine();
                    System.out.println(name + ": "+inString);
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка получения/отправки сообщения клиенту.");
        } finally {
            server.unregisterClient(this);
        }
    }

    public void sendMsg(String msg) throws IOException {
        if (out == null) {
            out.println(msg);
            out.flush();
        } else {
            throw new IOException("Out stream is not initialized");
        }
    }
}
