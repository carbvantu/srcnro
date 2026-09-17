import java.sql.*;

public class CheckDB {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/awnv3?useSSL=false", "root", "")) {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables("awnv3", null, "%", new String[]{"TABLE"});
            int tableCount = 0;
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                try (Statement stmt = conn.createStatement();
                     ResultSet cntRs = stmt.executeQuery("SELECT count(*) FROM `" + tableName + "`")) {
                    if (cntRs.next()) {
                        System.out.println(tableName + ": " + cntRs.getInt(1) + " rows");
                    }
                } catch (Exception e) {
                    System.out.println(tableName + ": error counting (" + e.getMessage() + ")");
                }
                tableCount++;
            }
            System.out.println("Total tables: " + tableCount);
        }
    }
}
