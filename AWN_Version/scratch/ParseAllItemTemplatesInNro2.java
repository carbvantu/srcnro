package scratch;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class ParseAllItemTemplatesInNro2 {
    public static void main(String[] args) throws Exception {
        Path p2 = Paths.get("C:\\Users\\vtson\\Downloads\\@remnro\\nro2\\nro.sql");
        if (!Files.exists(p2)) {
            System.out.println("Not found");
            return;
        }

        int totalItems = 0;
        int maxId = 0;
        TreeMap<Integer, String> items = new TreeMap<>();
        Map<Integer, Integer> itemType = new HashMap<>();
        Map<Integer, Integer> itemIcon = new HashMap<>();
        Map<Integer, Integer> itemPart = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(p2)) {
            String line;
            boolean inItemTemplate = false;
            // Pattern for: (id, TYPE, gender, NAME, description, icon_id, part, is_up_to_up, power_require, gold, gem, head, body, leg)
            Pattern rowPat = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*(\\d+),\\s*'([^']*)',\\s*'([^']*)',\\s*(\\d+),\\s*(-?\\d+)");

            while ((line = br.readLine()) != null) {
                if (line.contains("INSERT INTO `item_template`") || line.contains("INSERT INTO item_template")) {
                    inItemTemplate = true;
                    continue;
                }
                if (inItemTemplate) {
                    Matcher m = rowPat.matcher(line);
                    while (m.find()) {
                        int id = Integer.parseInt(m.group(1));
                        int type = Integer.parseInt(m.group(2));
                        String name = m.group(4);
                        int icon = Integer.parseInt(m.group(6));
                        int part = Integer.parseInt(m.group(7));
                        totalItems++;
                        if (id > maxId) maxId = id;
                        items.put(id, name);
                        itemType.put(id, type);
                        itemIcon.put(id, icon);
                        itemPart.put(id, part);
                    }
                    if (line.endsWith(";")) {
                        inItemTemplate = false;
                    }
                }
            }
        }

        System.out.println("Parsed items in nro2/nro.sql: " + totalItems + ", Max ID: " + maxId);

        // Check items with id >= 2000
        System.out.println("\nSample items with ID >= 2000 in nro2/nro.sql:");
        int count = 0;
        for (var entry : items.entrySet()) {
            if (entry.getKey() >= 2000) {
                int id = entry.getKey();
                System.out.println(String.format("  ID %4d | Type %2d | Icon %5d | Part %4d | Name: %s",
                    id, itemType.get(id), itemIcon.get(id), itemPart.get(id), entry.getValue()));
                count++;
                if (count > 40) {
                    System.out.println("  ... and " + (items.tailMap(2000).size() - 40) + " more!");
                    break;
                }
            }
        }
    }
}
