import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public class ClientHandler implements Runnable {

    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private Server owner;
    public static int CLIENTS_NUM = 0;

    public String getName() {
        return name;
    }

    public ClientHandler(Socket s, Server server) {
        CLIENTS_NUM++;
        this.s = s;
        this.name = "Client #" + CLIENTS_NUM;
        this.owner = server;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            sendMsg("Hello, User!");
            String str;
            str = in.readUTF();
            Character action = str.charAt(0);
            str = str.substring(1);
            String login = str.split(" ")[0];
            String pass = str.split(" ")[1];
            if (action == 'l') { //login
                String strNick = SQLTransport.getNicknameByLoginPassword(login, pass);
                if (!strNick.isEmpty()) {
                    //login successful
                    sendMsg(ServerAnswer.LOGIN_SUCCESS);
                    name = strNick;
                    while (true) {
                        str = in.readUTF();
                        if (str.equalsIgnoreCase("End"))
                            break;
                        owner.broadcastMsg(getName() + ": " + str);
                    }
                } else {
                    //invalid pass
                    sendMsg(ServerAnswer.LOGIN_ERR_INVALID_PASS);
                }
            } else if (action == 'r') { //register
                if (SQLTransport.getIsNicknameAlreadyUsed(login)) {
                    //nick name already used
                    sendMsg(ServerAnswer.REGISTER_ERR_LOGIN_ALREADY_USED);
                } else {
                    if(SQLTransport.registerUser(login, pass)) {
                        sendMsg(ServerAnswer.REGISTER_SUCCESS);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        } finally {
            stop();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
        }
    }

    public void stop() {
        try {
            owner.unregisterHandler(this);
            out.writeUTF("Server close connection...");
            out.flush();
            s.close();
            in.close();
            out.close();
        } catch (IOException e) {
        }
    }
}
