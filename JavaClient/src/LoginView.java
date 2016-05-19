import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public class LoginView extends JFrame {
    private LoginEventListener _listener = null;

    private JTextField jtfIP;
    private JTextField jtfPort;
    private JTextField jtfLogin;
    private JTextField jtfPassword;
    private JLabel jlMsg;

    private Socket serverSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public LoginView() throws HeadlessException {
        setTitle("Login");
        setSize(250, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setLayout(new OrientableFlowLayout(OrientableFlowLayout.VERTICAL)); //error
        setLayout(new GridLayout(0,1));

        JPanel jPanel = new JPanel(new GridLayout(0,1));
        jtfIP = new JTextField();
        jtfIP.setText("localhost");
        jPanel.add(jtfIP);

        jtfPort = new JTextField();
        jtfPort.setText("8190");
        jPanel.add(jtfPort);
        add(jPanel);

        JPanel jPanelLogin = new JPanel(new GridLayout(0,1));
        jtfLogin = new JTextField();
        jPanelLogin.add(new JLabel("Login:"));
        jPanelLogin.add(jtfLogin);
        add(jPanelLogin);

        JPanel pPassword = new JPanel(new GridLayout(0,1));
        jtfPassword = new JTextField();
        pPassword.add(new JLabel("Password:"));
        pPassword.add(jtfPassword);
        add(pPassword);

        jlMsg = new JLabel();
        add(jlMsg);

        JPanel jButtonsPanel = new JPanel(new GridLayout(1,0));
        JButton jbRegister = new JButton("Register & Login");
        jbRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(_listener != null) {
                    startRegister();
                } else {
                    showMsg("Listener is not has been assigned!");
                }
            }
        });
        jButtonsPanel.add(jbRegister);

        JButton jbConnect = new JButton("Login");
        jbConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(_listener != null) {
                    startLogin();
                } else {
                    showMsg("Listener is not has been assigned!");
                }
            }
        });
        jButtonsPanel.add(jbConnect);
        add(jButtonsPanel);

        setVisible(true);
    }

    public void setListener(LoginEventListener listener) {
        _listener = listener;
    }

    private void showMsg(String msg) {
        jlMsg.setText(msg);
    }

    private boolean openConnection() {
        try {
            serverSocket = new Socket(jtfIP.getText(), Integer.parseInt(jtfPort.getText()));
            out = new DataOutputStream(serverSocket.getOutputStream());
            in = new DataInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            showMsg("Connection refused");
            return false;
        }
        return true;
    }

    private boolean closeConnection() {
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

    private void startRegister() {
        try {
            openConnection();
            out.writeUTF("r"+jtfLogin.getText() + " " + jtfPassword.getText());//r = register
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String answer;
                        while (true) {
                            answer = in.readUTF();
                            if (answer.equals(ServerAnswer.REGISTER_SUCCESS)) {
                                startLogin();
                                break;
                            } else
                            if (answer.equals(ServerAnswer.REGISTER_ERR_LOGIN_ALREADY_USED)) {
                                _listener.onLoginError("Login already used");
                                showMsg("Login already used");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Connection Lost");
                        closeConnection();
                    }
                }
            }).start();
        } catch (IOException e) {
            showMsg("Connection refused");
            e.printStackTrace();
        }
    }

    private void startLogin() {
        try {
            openConnection();
            out.writeUTF("l"+jtfLogin.getText() + " " + jtfPassword.getText());//l = login
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String answer;
                        while (true) { // если все хорошо, бесконечно читаем входящий поток
                            answer = in.readUTF();
                            if (answer.equals(ServerAnswer.LOGIN_SUCCESS)) {
                                _listener.onLoginSuccess(serverSocket, in, out);
                                break;
                            } else
                            if (answer.equals(ServerAnswer.LOGIN_ERR_INVALID_PASS)) {
                                _listener.onLoginError("Password error");
                                showMsg("Invalid pass");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Connection Lost");
                        closeConnection();
                    }
                }
            }).start();
        } catch (IOException e) {
            showMsg("Connection refused");
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        serverSocket = null;
        out = null;
        in = null;
        _listener = null;
    }
}
