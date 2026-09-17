package scratch;

import jbcd.ConnectDB;
import java.sql.*;
import java.util.*;

public class CheckDbTemplates {
    public static void main(String[] args) throws Exception {
        try (Connection con = ConnectDB.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT count(*), max(id) FROM item_template");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Total item_template count: " + rs.getInt(1) + ", Max ID: " + rs.getInt(2));
            }

            // Check item types breakdown
            ps = con.prepareStatement("SELECT type, count(*) FROM item_template GROUP BY type ORDER BY count(*) DESC");
            rs = ps.executeQuery();
            System.out.println("\nBreakdown by Type:");
            while (rs.next()) {
                System.out.println("  Type " + rs.getInt(1) + ": " + rs.getInt(2) + " items");
            }

            // Check recent items (id >= 2400)
            ps = con.prepareStatement("SELECT id, type, name, icon_id, part FROM item_template WHERE id >= 2400 ORDER BY id");
            rs = ps.executeQuery();
            System.out.println("\nItems with ID >= 2400:");
            while (rs.next()) {
                System.out.println(String.format("  ID %4d | Type %2d | Icon %5d | Part %4d | Name: %s",
                    rs.getInt("id"), rs.getInt("type"), rs.getInt("icon_id"), rs.getInt("part"), rs.getString("name")));
            }
        }
    }
}
