package scratch;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FindFilesOver100MB {
    public static void main(String[] args) throws IOException {
        Path root = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version");
        long maxBytes = 95L * 1024 * 1024; // 95 MB threshold for GitHub safe limit

        System.out.println("Scanning for files > 95MB in " + root);
        List<Path> largeFiles = new ArrayList<>();

        try (var s = Files.walk(root)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                try {
                    long size = Files.size(p);
                    if (size > maxBytes) {
                        largeFiles.add(p);
                        System.out.println(String.format("LARGE FILE: %-80s | Size: %.2f MB", 
                            root.relativize(p), (double) size / (1024 * 1024)));
                    }
                } catch (IOException ignored) {}
            });
        }

        System.out.println("Total files exceeding GitHub 100MB limit: " + largeFiles.size());
    }
}
