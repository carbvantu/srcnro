package scratch;

import jbcd.ConnectDB;
import java.sql.*;
import java.util.*;

public class CheckImgByNameTable {
    public static void main(String[] args) throws Exception {
        try (Connection con = ConnectDB.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT name, n_frame FROM img_by_name ORDER BY name");
            Map<String, Integer> list = new TreeMap<>();
            while (rs.next()) {
                list.put(rs.getString("name"), rs.getInt("n_frame"));
            }
            System.out.println("Total img_by_name rows: " + list.size());
            System.out.println("Mount rows in img_by_name:");
            for (var e : list.entrySet()) {
                if (e.getKey().startsWith("mount_")) {
                    System.out.println("  " + e.getKey() + " -> " + e.getValue() + " frames");
                }
            }
        }
    }
}
