import jbcd.ConnectDB;
import jbcd.CrisResultSet;

public class TestLoginQuery {
    public static void main(String[] args) {
        try {
            System.out.println("Testing login query via ConnectDB...");
            CrisResultSet rs = ConnectDB.executeQuery("select * from account where username = ?", "atrai");
            if (rs.first()) {
                System.out.println("SUCCESS! Found account atrai! ID=" + rs.getInt("id") + ", username=" + rs.getString("username"));
            } else {
                System.out.println("Account atrai not found, but query succeeded!");
            }
            rs.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
