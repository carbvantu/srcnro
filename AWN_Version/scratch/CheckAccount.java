import jbcd.ConnectDB;
import jbcd.CrisResultSet;

public class CheckAccount {
    public static void main(String[] args) throws Exception {
        CrisResultSet rs = ConnectDB.executeQuery("select id, username, password from account where username = 'atrai'");
        if (rs.first()) {
            System.out.println("User: " + rs.getString("username") + ", Pass: " + rs.getString("password"));
        }
        rs.dispose();
    }
}
