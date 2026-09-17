import java.sql.*;

public class VerifyDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/awnv3?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = ""; // default local root pass

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("=== KIEM TRA DATABASE AWNV3 ===");

            // 1. Kiem tra item_template 2434..2478
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, name, type, gender, head, body, leg, part FROM item_template WHERE id BETWEEN 2434 AND 2478 ORDER BY id ASC")) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.printf("Item [%d] %-30s | Type: %d | Gender: %d | Head/Body/Leg: %d/%d/%d | Part: %d%n",
                            rs.getInt("id"), rs.getString("name"), rs.getInt("type"), rs.getInt("gender"),
                            rs.getInt("head"), rs.getInt("body"), rs.getInt("leg"), rs.getInt("part"));
                }
                System.out.println("Tong so Item Template moi: " + count);
            }

            // 2. Kiem tra data_badges 19..28
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, name, idEffect, idItem, Options FROM data_badges WHERE id >= 19 ORDER BY id ASC")) {
                System.out.println("\n=== KIEM TRA DATA BADGES MOI ===");
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.printf("Badge [%d] %-25s | Effect: %d | Item: %d | Options: %s%n",
                            rs.getInt("id"), rs.getString("name"), rs.getInt("idEffect"), rs.getInt("idItem"), rs.getString("Options"));
                }
                System.out.println("Tong so Badge moi: " + count);
            }

            // 3. Kiem tra part 1944..1958
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, type, data FROM part WHERE id BETWEEN 1944 AND 1958 ORDER BY id ASC")) {
                System.out.println("\n=== KIEM TRA PART MOI ===");
                int count = 0;
                while (rs.next()) {
                    count++;
                    String d = rs.getString("data");
                    if (d != null && d.length() > 40) d = d.substring(0, 40) + "...";
                    System.out.printf("Part [%d] | Type: %d | Data: %s%n", rs.getInt("id"), rs.getInt("type"), d);
                }
                System.out.println("Tong so Part moi: " + count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
