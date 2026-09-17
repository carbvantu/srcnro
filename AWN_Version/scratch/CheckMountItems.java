package scratch;

import jbcd.ConnectDB;
import java.sql.*;
import java.util.*;

public class CheckMountItems {
    public static void main(String[] args) throws Exception {
        Map<Integer, Integer> mountPartToItemId = new TreeMap<>();
        Map<Integer, String> mountPartToItemName = new TreeMap<>();
        try (Connection con = ConnectDB.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT id, name, part, icon_id FROM item_template WHERE type = 23 ORDER BY part");
            while (rs.next()) {
                int part = rs.getInt("part");
                mountPartToItemId.put(part, rs.getInt("id"));
                mountPartToItemName.put(part, rs.getString("name"));
            }
        }
        System.out.println("Current mounts in item_template: " + mountPartToItemId.size());
        for (var e : mountPartToItemId.entrySet()) {
            System.out.println(String.format("  Part %2d -> Item ID %4d: %s", 
                e.getKey(), e.getValue(), mountPartToItemName.get(e.getKey())));
        }

        // Check which parts from 1 to 80 are missing
        System.out.println("\nMissing Mount Parts from 1 to 80:");
        for (int p = 1; p <= 80; p++) {
            if (!mountPartToItemId.containsKey(p)) {
                System.out.print(p + " ");
            }
        }
        System.out.println();
    }
}
