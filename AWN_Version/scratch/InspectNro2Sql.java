package scratch;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InspectNro2Sql {
    public static void main(String[] args) throws Exception {
        Path p2 = Paths.get("C:\\Users\\vtson\\Downloads\\@remnro\\nro2\\nro.sql");
        Path partNew = Paths.get("C:\\Users\\vtson\\Downloads\\AWN_Version\\part\\part new.sql");

        System.out.println("=== PART NEW.SQL ===");
        if (Files.exists(partNew)) {
            try (BufferedReader br = Files.newBufferedReader(partNew)) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null && count < 15) {
                    System.out.println(line);
                    count++;
                }
            }
        }

        System.out.println("\n=== NRO2/NRO.SQL ITEM_TEMPLATES ===");
        if (Files.exists(p2)) {
            try (BufferedReader br = Files.newBufferedReader(p2)) {
                String line;
                boolean inItemTemplate = false;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (line.contains("INSERT INTO `item_template`") || line.contains("INSERT INTO item_template")) {
                        inItemTemplate = true;
                    }
                    if (inItemTemplate) {
                        if (line.length() > 200) {
                            System.out.println(line.substring(0, 200) + "...");
                        } else {
                            System.out.println(line);
                        }
                        count++;
                        if (count > 20) break;
                    }
                }
            }
        }
    }
}
