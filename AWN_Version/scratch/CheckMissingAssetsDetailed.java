package scratch;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CheckMissingAssetsDetailed {
    public static void main(String[] args) throws IOException {
        Path serverRoot = Paths.get("data");
        Path nro1 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1");
        Path nro2 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2");

        System.out.println("=== CHECKING NRO1 ASSETS MISSING FROM SERVER ===");
        checkDir(nro1, serverRoot);

        System.out.println("\n=== CHECKING NRO2 ASSETS MISSING FROM SERVER ===");
        checkDir(nro2, serverRoot);
    }

    private static void checkDir(Path sourceDir, Path targetRoot) throws IOException {
        if (!Files.exists(sourceDir)) return;
        List<Path> missing = new ArrayList<>();
        try (var s = Files.walk(sourceDir)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString();
                // Check where this file should be
                // If it's in icon, effect, or imgbyname
                String parent = p.getParent().getFileName().toString().toLowerCase();
                String grand = p.getParent().getParent() != null ? p.getParent().getParent().getFileName().toString().toLowerCase() : "";

                Path targetFile = null;
                if (grand.equals("icon") || parent.equals("icon") || parent.startsWith("x")) {
                    // Check in targetRoot/icon/x4/name, x3, x2, x1
                    if (parent.matches("x[1-4]")) {
                        targetFile = targetRoot.resolve("icon").resolve(parent).resolve(name);
                    } else {
                        targetFile = targetRoot.resolve("icon").resolve("x4").resolve(name);
                    }
                } else if (parent.equals("imgbyname") || name.startsWith("aura_") || name.startsWith("mount_") || name.startsWith("set_eff_") || name.startsWith("skills_")) {
                    targetFile = targetRoot.resolve("img_by_name").resolve("x4").resolve(name);
                } else if (parent.equals("effect") || name.startsWith("imgeffect_") || name.startsWith("imageeffect_")) {
                    targetFile = targetRoot.resolve("effect").resolve("x4").resolve(name);
                }

                if (targetFile != null && !Files.exists(targetFile)) {
                    missing.add(p);
                }
            });
        }
        System.out.println("Total missing files: " + missing.size());
        if (!missing.isEmpty()) {
            System.out.println("Samples missing (up to 20):");
            for (int i = 0; i < Math.min(20, missing.size()); i++) {
                System.out.println("  " + missing.get(i));
            }
        }
    }
}
