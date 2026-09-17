package scratch;

import jbcd.ConnectDB;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class FindUnmappedIcons {
    public static void main(String[] args) throws Exception {
        Set<Integer> dbIcons = new HashSet<>();
        Map<Integer, String> iconToItemName = new HashMap<>();
        try (Connection con = ConnectDB.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id, name, icon_id FROM item_template WHERE icon_id > 0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int icon = rs.getInt("icon_id");
                dbIcons.add(icon);
                iconToItemName.put(icon, rs.getString("name"));
            }
        }
        System.out.println("Total icons used in item_template: " + dbIcons.size());

        // Now find icons in nro1
        Path nro1Icon = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1\\data\\girlkun\\icon\\x4");
        Set<Integer> nro1Icons = loadIcons(nro1Icon);
        System.out.println("nro1 x4 icons: " + nro1Icons.size());

        Set<Integer> nro1Unmapped = new TreeSet<>(nro1Icons);
        nro1Unmapped.removeAll(dbIcons);
        System.out.println("nro1 unmapped icons: " + nro1Unmapped.size());

        // Group into ranges
        printRanges("nro1 Unmapped", nro1Unmapped);

        // Now find icons in nro2
        Path nro2Icon = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2\\NROZ_BETA\\data\\ảnh\\x4");
        Set<Integer> nro2Icons = loadIcons(nro2Icon);
        System.out.println("\nnro2 x4 icons: " + nro2Icons.size());

        Set<Integer> nro2Unmapped = new TreeSet<>(nro2Icons);
        nro2Unmapped.removeAll(dbIcons);
        System.out.println("nro2 unmapped icons: " + nro2Unmapped.size());

        printRanges("nro2 Unmapped", nro2Unmapped);
    }

    private static Set<Integer> loadIcons(Path dir) throws IOException {
        Set<Integer> set = new TreeSet<>();
        if (!Files.exists(dir)) return set;
        try (var s = Files.list(dir)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString();
                if (name.endsWith(".png")) {
                    try {
                        set.add(Integer.parseInt(name.substring(0, name.length() - 4)));
                    } catch (Exception ignored) {}
                }
            });
        }
        return set;
    }

    private static void printRanges(String label, Set<Integer> unmapped) {
        System.out.println("--- " + label + " Ranges ---");
        List<Integer> list = new ArrayList<>(unmapped);
        if (list.isEmpty()) return;
        int start = list.get(0);
        int prev = start;
        for (int i = 1; i < list.size(); i++) {
            int cur = list.get(i);
            if (cur == prev + 1) {
                prev = cur;
            } else {
                if (start == prev) {
                    System.out.println("  " + start);
                } else {
                    System.out.println("  " + start + " -> " + prev + " (" + (prev - start + 1) + " icons)");
                }
                start = cur;
                prev = cur;
            }
        }
        if (start == prev) {
            System.out.println("  " + start);
        } else {
            System.out.println("  " + start + " -> " + prev + " (" + (prev - start + 1) + " icons)");
        }
    }
}
