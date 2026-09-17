package scratch;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class FindAllItemTemplatesInAllSql {
    public static void main(String[] args) throws Exception {
        List<Path> sqlFiles = new ArrayList<>();
        sqlFiles.add(Paths.get("C:\\Users\\vtson\\Downloads\\ENZEEFXNROxBARCOLL V10(1)\\backup\\nro_goc.sql"));
        sqlFiles.add(Paths.get("C:\\Users\\vtson\\Downloads\\AWN_Version\\AWN_Version\\sql\\anwinvip.sql"));
        sqlFiles.add(Paths.get("C:\\Users\\vtson\\Downloads\\AWN_Version\\AWN_Version\\awnv3 (database16-08).sql"));
        sqlFiles.add(Paths.get("C:\\Users\\vtson\\Downloads\\@remnro\\tutien.space\\nro.sql"));
        sqlFiles.add(Paths.get("C:\\Users\\vtson\\Downloads\\@remnro\\tutien.space\\nro1.sql"));

        for (Path p : sqlFiles) {
            if (Files.exists(p)) {
                checkSql(p);
            }
        }
    }

    private static void checkSql(Path file) throws Exception {
        int count = 0;
        int maxId = 0;
        Pattern pat = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*(\\d+),\\s*'([^']*)',");
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            boolean inTemplate = false;
            while ((line = br.readLine()) != null) {
                if (line.contains("INSERT INTO `item_template`") || line.contains("INSERT INTO item_template")) {
                    inTemplate = true;
                    continue;
                }
                if (inTemplate) {
                    Matcher m = pat.matcher(line);
                    while (m.find()) {
                        count++;
                        int id = Integer.parseInt(m.group(1));
                        if (id > maxId) maxId = id;
                    }
                    if (line.endsWith(";")) inTemplate = false;
                }
            }
        }
        System.out.println(String.format("File: %-70s | Items: %5d | Max ID: %5d", file.getFileName(), count, maxId));
    }
}
