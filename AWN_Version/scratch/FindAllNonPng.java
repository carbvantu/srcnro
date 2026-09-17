package scratch;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FindAllNonPng {
    public static void main(String[] args) throws IOException {
        Path p1 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1");
        Path p2 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2");

        System.out.println("Non-png in nro1:");
        findNonPng(p1);

        System.out.println("\nNon-png in nro2:");
        findNonPng(p2);
    }

    private static void findNonPng(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (var s = Files.walk(dir)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString().toLowerCase();
                if (!name.endsWith(".png")) {
                    System.out.println("  Found: " + p);
                }
            });
        }
    }
}
