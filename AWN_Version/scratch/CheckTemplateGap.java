import java.sql.*;

public class CheckTemplateGap {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/awnv3?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT count(*), max(id) FROM item_template");
            if (rs.next()) {
                System.out.println("Item count: " + rs.getInt(1) + ", Max ID: " + rs.getInt(2));
            }
            
            // Check gaps between id and row count
            rs = st.executeQuery("SELECT id FROM item_template ORDER BY id ASC");
            int expected = 0;
            int gaps = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                if (id != expected) {
                    if (gaps < 10) {
                        System.out.println("Gap detected: expected " + expected + " but found id " + id);
                    }
                    gaps++;
                    expected = id;
                }
                expected++;
            }
            System.out.println("Total gaps: " + gaps);

            // Also check item 2436 in database: name, icon_id, type
            rs = st.executeQuery("SELECT * FROM item_template WHERE id = 2436");
            if (rs.next()) {
                System.out.println("Item 2436 in DB: name=" + rs.getString("name") + ", icon_id=" + rs.getInt("icon_id") + ", type=" + rs.getInt("type") + ", part=" + rs.getInt("part"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
