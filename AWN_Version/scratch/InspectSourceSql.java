package scratch;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class InspectSourceSql {
    public static void main(String[] args) throws Exception {
        Path p1 = Paths.get("C:\\Users\\vtson\\Downloads\\@remnro\\nro1\\sql\\nroz.sql");
        Path p2 = Paths.get("C:\\Users\\vtson\\Downloads\\@remnro\\nro2\\nro.sql");

        if (Files.exists(p1)) {
            System.out.println("=== INSPECTING nro1/sql/nroz.sql ===");
            inspectSql(p1);
        } else {
            System.out.println("p1 not found: " + p1);
        }

        if (Files.exists(p2)) {
            System.out.println("\n=== INSPECTING nro2/nro.sql ===");
            inspectSql(p2);
        } else {
            System.out.println("p2 not found: " + p2);
        }
    }

    private static void inspectSql(Path file) throws Exception {
        System.out.println("File size: " + Files.size(file) / 1024 / 1024 + " MB");
        // Check tables in this file
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            Set<String> tables = new TreeSet<>();
            int itemCount = 0;
            int partCount = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("CREATE TABLE") || line.startsWith("INSERT INTO")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 2) {
                        String tbl = parts[2].replace("`", "").replace("(", "");
                        tables.add(tbl);
                    }
                }
                if (line.contains("INSERT INTO `item_template`") || line.contains("INSERT INTO item_template")) {
                    itemCount++;
                }
                if (line.contains("INSERT INTO `part`") || line.contains("INSERT INTO part")) {
                    partCount++;
                }
            }
            System.out.println("Tables found: " + tables);
            System.out.println("item_template insert lines: " + itemCount);
            System.out.println("part insert lines: " + partCount);
        }
    }
}
