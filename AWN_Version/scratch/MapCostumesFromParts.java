package scratch;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class MapCostumesFromParts {
    public static void main(String[] args) throws Exception {
        Path partNew = Paths.get("part/part new.sql");
        // Each entry is: (id, TYPE, DATA)
        // TYPE 0 = head, TYPE 1 = leg/body, TYPE 2 = body/leg
        Pattern pat = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*'([^']*)'\\)");
        Map<Integer, Integer> types = new LinkedHashMap<>();
        Map<Integer, String> data = new LinkedHashMap<>();

        try (BufferedReader br = Files.newBufferedReader(partNew)) {
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

        // We want all parts with id >= 1959
        List<Integer> newParts = new ArrayList<>();
        for (int id : types.keySet()) {
            if (id >= 1959) {
                newParts.add(id);
            }
        }
        System.out.println("Total new parts (>= 1959): " + newParts.size());

        // Group into triplets
        // Check pattern of types: 0, 1, 2
        for (int i = 0; i < newParts.size(); i += 3) {
            if (i + 2 >= newParts.size()) break;
            int pA = newParts.get(i);
            int pB = newParts.get(i + 1);
            int pC = newParts.get(i + 2);
            int tA = types.get(pA);
            int tB = types.get(pB);
            int tC = types.get(pC);

            int head = -1, leg = -1, body = -1;
            int[] ps = {pA, pB, pC};
            int[] ts = {tA, tB, tC};
            for (int k = 0; k < 3; k++) {
                if (ts[k] == 0 && head == -1) head = ps[k];
                else if (ts[k] == 1 && leg == -1) leg = ps[k];
                else if (ts[k] == 2 && body == -1) body = ps[k];
                else if (head == -1) head = ps[k];
                else if (leg == -1) leg = ps[k];
                else body = ps[k];
            }

            // Find first icon in head
            Matcher m = Pattern.compile("\\[(\\d+),").matcher(data.get(head != -1 ? head : pA));
            int sprite = m.find() ? Integer.parseInt(m.group(1)) : -1;

            System.out.println(String.format("Costume Part Set: Head=%4d, Body=%4d, Leg=%4d | Raw: (%d:t%d, %d:t%d, %d:t%d) | SampleSprite=%d",
                head, body, leg, pA, tA, pB, tB, pC, tC, sprite));
        }
    }
}
