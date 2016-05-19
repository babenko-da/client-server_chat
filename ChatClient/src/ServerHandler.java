import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by dmitrybabenko on 4/16/16.
 */
public class ServerHandler extends Thread {
    private Socket s;
    private DataInputStream in;
    private MessageListener listener;

    public ServerHandler(Socket s, MessageListener listener) {
        this.s = s;
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();

        try {
            in = new DataInputStream(s.getInputStream());
            while (!this.isInterrupted()) {
                listener.incomingMessage(in.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
            listener.incomingMessage("Ошибка подключения к серверу.");
            interrupt();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        in = null;
        s = null;
        listener = null;
    }
}
