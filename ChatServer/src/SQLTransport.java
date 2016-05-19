import java.sql.*;

/**
 * Created by dmitrybabenko on 5/1/16.
 */
public class SQLTransport {

    private static Connection c;
    private static PreparedStatement ps;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:chatdb.db");
            c.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void fillTable() {
        try {
            for (int i = 0; i < 20; i++) {
                ps = c.prepareStatement("INSERT INTO Main (Login, Password, Nickname) VALUES (?, ?, ?)");
                ps.setString(1, "login" + i);
                ps.setString(2, "pass" + i);
                ps.setString(3, "nick" + i);
                ps.execute();
            }
        } catch (SQLException e) {
        }
    }

    public static boolean getIsNicknameAlreadyUsed(String login) {
        ResultSet rs;
        try {
            ps = c.prepareStatement("SELECT Nickname FROM Main WHERE Login = ?");
            ps.setString(1, login);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getNicknameByLoginPassword(String login, String password) {
        ResultSet rs;
        String str = "";
        try {
            ps = c.prepareStatement("SELECT Nickname FROM Main WHERE Login = ? AND Password = ?");
            ps.setString(1, login);
            ps.setString(2, password);
            rs = ps.executeQuery();
            while (rs.next()) {
                str = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static boolean registerUser(String login, String pass) {
        try {
            ps = c.prepareStatement("INSERT INTO Main (Login, Password, Nickname) VALUES (?, ?, ?)");
            ps.setString(1, login);
            ps.setString(2, pass);
            ps.setString(3, login);
            ps.execute();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }
}
