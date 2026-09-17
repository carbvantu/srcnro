package scratch;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class AnalyzeCostumesInPartNew {
    public static void main(String[] args) throws Exception {
        Path p = Paths.get("part/part new.sql");
        // Each entry is: (id, TYPE, DATA)
        // TYPE: 0 = head, 1 = leg/body, 2 = body/leg
        Pattern pat = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*'([^']*)'\\)");
        Map<Integer, Integer> types = new LinkedHashMap<>();
        Map<Integer, String> data = new LinkedHashMap<>();

        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = pat.matcher(line);
                while (m.find()) {
                    int id = Integer.parseInt(m.group(1));
                    int type = Integer.parseInt(m.group(2));
                    types.put(id, type);
                    data.put(id, m.group(3));
                }
            }
        }

        // Group into sets of 3: type 0, type 1, type 2
        System.out.println("Total parts in file: " + types.size());
        List<Integer> keys = new ArrayList<>(types.keySet());

        int costumeIndex = 1;
        for (int i = 0; i < keys.size(); i += 3) {
            if (i + 2 >= keys.size()) break;
            int p1 = keys.get(i);
            int p2 = keys.get(i + 1);
            int p3 = keys.get(i + 2);
            int t1 = types.get(p1);
            int t2 = types.get(p2);
            int t3 = types.get(p3);

            // Extract sample icon ID from p1 (head)
            String d1 = data.get(p1);
            Matcher iconM = Pattern.compile("\\[(\\d+),").matcher(d1);
            int headIcon = iconM.find() ? Integer.parseInt(iconM.group(1)) : -1;

            if (p1 >= 1944) { // Newer parts!
                System.out.println(String.format("Costume #%2d: PartHead=%4d (t=%d), Part2=%4d (t=%d), Part3=%4d (t=%d) | HeadSprite=%d",
                    costumeIndex, p1, t1, p2, t2, p3, t3, headIcon));
            }
            costumeIndex++;
        }
    }
}
