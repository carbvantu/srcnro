import java.sql.*;

public class InspectExistingPets {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/awnv3?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement st = conn.createStatement();
            
            System.out.println("=== CAC PET CO SAN TRONG GAME (type = 21) ===");
            ResultSet rs = st.executeQuery("SELECT id, name, icon_id, part, head, body, leg FROM item_template WHERE type = 21 AND id < 2400 LIMIT 15");
            while (rs.next()) {
                System.out.printf("Pet [%d] %-30s | icon: %d | part: %d | H/B/L: %d/%d/%d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("icon_id"), rs.getInt("part"),
                        rs.getInt("head"), rs.getInt("body"), rs.getInt("leg"));
            }

            System.out.println("\n=== CAC THU CUOI CO SAN (type = 23) ===");
            rs = st.executeQuery("SELECT id, name, icon_id, part, head, body, leg FROM item_template WHERE type = 23 AND id < 2400 LIMIT 10");
            while (rs.next()) {
                System.out.printf("Mount [%d] %-30s | icon: %d | part: %d | H/B/L: %d/%d/%d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("icon_id"), rs.getInt("part"),
                        rs.getInt("head"), rs.getInt("body"), rs.getInt("leg"));
            }

            System.out.println("\n=== CAC LINH THU CO SAN (type = 70) ===");
            rs = st.executeQuery("SELECT id, name, icon_id, part, head, body, leg FROM item_template WHERE type = 70 AND id < 2400 LIMIT 10");
            while (rs.next()) {
                System.out.printf("LinhThu [%d] %-30s | icon: %d | part: %d | H/B/L: %d/%d/%d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("icon_id"), rs.getInt("part"),
                        rs.getInt("head"), rs.getInt("body"), rs.getInt("leg"));
            }

            System.out.println("\n=== CAC CAI TRANG CO SAN (type = 5) ===");
            rs = st.executeQuery("SELECT id, name, icon_id, part, head, body, leg FROM item_template WHERE type = 5 AND id BETWEEN 1000 AND 1010");
            while (rs.next()) {
                System.out.printf("CaiTrang [%d] %-30s | icon: %d | part: %d | H/B/L: %d/%d/%d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("icon_id"), rs.getInt("part"),
                        rs.getInt("head"), rs.getInt("body"), rs.getInt("leg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
