import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public abstract class LoginEventListener {
    public abstract void onLoginSuccess(Socket server, DataInputStream inputStream, DataOutputStream outputStream);
    public abstract void onLoginError(String msg);
}
