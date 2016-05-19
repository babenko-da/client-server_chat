import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public class ChatView extends JFrame {
    private JTextArea jtfMsg;
    private JTextField jtf;
    private ChatViewEventListener _listener;

    public ChatView(ChatViewEventListener listener) throws HeadlessException {
        _listener = listener;

        setSize(400, 500);
        setTitle("JClient");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jtfMsg = new JTextArea();
        JScrollPane jsp = new JScrollPane(jtfMsg);
        jsp.setAutoscrolls(true);
        jtfMsg.setAutoscrolls(true);
        add(jsp, BorderLayout.CENTER);
        jtfMsg.setBackground(Color.white);
        jtfMsg.setForeground(Color.black);
        jtfMsg.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        JPanel jpBottom = new JPanel();
        JButton jbSEND = new JButton("SEND");
        jtf = new JTextField();
        jpBottom.setLayout(new BorderLayout());
        jpBottom.add(jbSEND, BorderLayout.EAST);
        jpBottom.add(jtf, BorderLayout.CENTER);
        add(jpBottom, BorderLayout.SOUTH);

        jbSEND.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsgFromJTF();
            }
        });

        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsgFromJTF();
            }
        });

        setVisible(false);
    }

    private void sendMsgFromJTF() {
        if (_listener != null) {
            _listener.sendMessage(jtf.getText());
            jtf.setText("");
        }
    }

    public void showMsg(String msg) {
        jtfMsg.append(msg);
    }

    @Override
    public void dispose() {
        super.dispose();
        _listener = null;
    }
}
