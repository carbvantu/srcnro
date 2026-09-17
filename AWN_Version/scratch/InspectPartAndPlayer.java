import java.sql.*;

public class InspectPartAndPlayer {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/awnv3?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement st = conn.createStatement();
            
            // Check part 856
            ResultSet rs = st.executeQuery("SELECT * FROM part WHERE id = 856");
            if (rs.next()) {
                System.out.println("Part 856: type=" + rs.getInt("type") + ", data=" + rs.getString("data"));
            } else {
                System.out.println("Part 856 NOT FOUND in part table!");
            }
            
            // Check max part ID and count
            rs = st.executeQuery("SELECT count(*), max(id) FROM part");
            if (rs.next()) {
                System.out.println("Part count=" + rs.getInt(1) + ", max id=" + rs.getInt(2));
            }
            
            // Check player items in inventory and body
            rs = st.executeQuery("SELECT id, name, items_body, items_bag FROM player ORDER BY id DESC LIMIT 5");
            while (rs.next()) {
                System.out.println("Player: " + rs.getString("name") + " (id=" + rs.getInt("id") + ")");
                System.out.println("  Body: " + rs.getString("items_body"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
