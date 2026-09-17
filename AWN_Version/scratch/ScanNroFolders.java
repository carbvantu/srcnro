package scratch;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ScanNroFolders {
    public static void main(String[] args) throws IOException {
        Path p1 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1");
        Path p2 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2");

        System.out.println("=== ANALYZING NRO1 ===");
        analyzeFolder(p1);

        System.out.println("\n=== ANALYZING NRO2 ===");
        analyzeFolder(p2);
    }

    private static void analyzeFolder(Path root) throws IOException {
        if (!Files.exists(root)) {
            System.out.println("Path does not exist: " + root);
            return;
        }

        Map<String, Integer> fileCounts = new TreeMap<>();
        Map<String, Set<String>> extensions = new TreeMap<>();
        Map<String, List<String>> sampleFiles = new TreeMap<>();

        try (var stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile).forEach(p -> {
                Path rel = root.relativize(p);
                String parent = rel.getParent() != null ? rel.getParent().toString() : "<root>";
                fileCounts.put(parent, fileCounts.getOrDefault(parent, 0) + 1);

                String name = p.getFileName().toString();
                int dot = name.lastIndexOf('.');
                String ext = dot >= 0 ? name.substring(dot) : "<none>";
                extensions.computeIfAbsent(parent, k -> new HashSet<>()).add(ext);

                List<String> samples = sampleFiles.computeIfAbsent(parent, k -> new ArrayList<>());
                if (samples.size() < 5) {
                    samples.add(name);
                }
            });
        }

        for (var entry : fileCounts.entrySet()) {
            String dir = entry.getKey();
            int count = entry.getValue();
            Set<String> exts = extensions.get(dir);
            List<String> samples = sampleFiles.get(dir);
            System.out.println(String.format("Dir: %-40s | Count: %5d | Ext: %s | Samples: %s", 
                dir, count, exts, samples));
        }
    }
}
