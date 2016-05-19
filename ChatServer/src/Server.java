import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by dmitrybabenko on 4/22/16.
 */
public class Server{
    public static int MAX_CLIENTS_COUNT = 5;
    private ArrayList<ClientHandler> clients;

    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            clients = new ArrayList<>();
            System.out.println("server started at port 8189");
            while (ClientHandler.CLIENTS_COUNT <= MAX_CLIENTS_COUNT) {
                Socket s = serverSocket.accept();
                ClientHandler ch = new ClientHandler(s, this);
                clients.add(ch);
                new Thread(ch).start();
                System.out.println("Client connected");
            }
            System.out.println("connection closed");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка запуска сервера, перезапустите приложение.");
        }
    }

    public void unregisterClient(ClientHandler client) {
        clients.remove(client);
    }
}
