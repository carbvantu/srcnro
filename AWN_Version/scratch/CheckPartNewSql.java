package scratch;

import jbcd.ConnectDB;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class CheckPartNewSql {
    public static void main(String[] args) throws Exception {
        Path partNew = Paths.get("part/part new.sql");
        if (!Files.exists(partNew)) {
            System.out.println("part new.sql not found at " + partNew.toAbsolutePath());
            return;
        }

        Set<Integer> dbParts = new HashSet<>();
        try (Connection con = ConnectDB.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT id FROM part");
            while (rs.next()) {
                dbParts.add(rs.getInt(1));
            }
        }
        System.out.println("Current DB parts count: " + dbParts.size());

        List<Integer> fileParts = new ArrayList<>();
        Map<Integer, Integer> partTypes = new LinkedHashMap<>();
        Pattern pat = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*'([^']*)'\\)");

        try (BufferedReader br = Files.newBufferedReader(partNew)) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = pat.matcher(line);
                while (m.find()) {
                    int id = Integer.parseInt(m.group(1));
                    int type = Integer.parseInt(m.group(2));
                    fileParts.add(id);
                    partTypes.put(id, type);
                }
            }
        }

        System.out.println("Total parts in part new.sql: " + fileParts.size() + 
            " (Range: " + Collections.min(fileParts) + " -> " + Collections.max(fileParts) + ")");

        List<Integer> missingFromDb = new ArrayList<>();
        for (int id : fileParts) {
            if (!dbParts.contains(id)) {
                missingFromDb.add(id);
            }
        }

        System.out.println("Parts in part new.sql missing from DB: " + missingFromDb.size() + 
            (missingFromDb.isEmpty() ? "" : " (Range: " + Collections.min(missingFromDb) + " -> " + Collections.max(missingFromDb) + ")"));
    }
}
