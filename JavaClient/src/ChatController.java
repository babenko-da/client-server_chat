import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public class ChatController extends Thread {
    private Socket serverSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isLogging;

    private ChatView chatView;
    private LoginView loginWindow;

    public ChatController() {
        chatView = new ChatView(new ChatViewEventListener() {
            @Override
            public void sendMessage(String msg) {
                try {
                    out.writeUTF(msg);
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Message sending error...");
                    closeConnection();
                }
            }
        });
        setIsLogging(false);
    }

    @Override
    public void run() {
        super.run();
        while(true) {
            if (!isLogging) {
                if (loginWindow == null) {
                    login();
                }
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                readServerStream();
            }
        }
    }

    private void readServerStream() {
        while (isLogging && in != null) {
            try {
                showMsg(in.readUTF() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                closeConnection();
            }
        }
    }

    private void showMsg(String msg) {
        chatView.showMsg(msg);
    }

    private void setIsLogging(boolean value) {
        isLogging = value;
        if (chatView.isVisible() != value) {
            chatView.setVisible(value);
        }
    }

    private boolean closeConnection() {
        setIsLogging(false);
        try {
            in.close();
            out.close();
            serverSocket.close();
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
        return true;
    }

    private void login() {
        if (!isLogging) {
            loginWindow = new LoginView();
            loginWindow.setListener(new LoginEventListener() {
                @Override
                public void onLoginSuccess(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
                    setIsLogging(true);
                    serverSocket = socket;
                    in = inputStream;
                    out = outputStream;
                    loginWindow.dispose();
                    loginWindow = null;
                }

                @Override
                public void onLoginError(String msg) {
                    System.out.println("Login / Register error: "+msg);
                }
            });
        }
    }
}
