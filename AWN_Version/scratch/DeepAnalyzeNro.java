package scratch;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DeepAnalyzeNro {
    public static void main(String[] args) throws IOException {
        Path p1 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1");
        Path p2 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2");

        System.out.println("=== DEEP ANALYZING NRO1 ===");
        analyzeNro1(p1);

        System.out.println("\n=== DEEP ANALYZING NRO2 ===");
        analyzeNro2(p2);
    }

    private static void analyzeNro1(Path root) throws IOException {
        // Look at data/girlkun/effect, data/girlkun/icon, normal/image/...
        checkEffects(root.resolve("data\\girlkun\\effect"));
        checkIcons(root.resolve("data\\girlkun\\icon"), "girlkun/icon");
        checkNormal(root.resolve("normal\\image"));
    }

    private static void analyzeNro2(Path root) throws IOException {
        checkNro2Images(root.resolve("NROZ_BETA\\data"));
    }

    private static void checkEffects(Path effectDir) throws IOException {
        if (!Files.exists(effectDir)) return;
        System.out.println("--- Effect files in " + effectDir + " ---");
        Set<String> effectFiles = new TreeSet<>();
        try (var s = Files.walk(effectDir)) {
            s.filter(Files::isRegularFile).forEach(p -> effectFiles.add(p.getFileName().toString()));
        }
        System.out.println("Total distinct effect files: " + effectFiles.size());
        System.out.println("Sample effects: " + effectFiles.stream().limit(20).toList());
    }

    private static void checkIcons(Path iconDir, String label) throws IOException {
        if (!Files.exists(iconDir)) return;
        Set<Integer> iconIds = new TreeSet<>();
        Set<String> nonNumeric = new TreeSet<>();
        try (var s = Files.walk(iconDir)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString();
                if (name.endsWith(".png")) {
                    String stem = name.substring(0, name.length() - 4);
                    try {
                        iconIds.add(Integer.parseInt(stem));
                    } catch (NumberFormatException e) {
                        nonNumeric.add(name);
                    }
                }
            });
        }
        System.out.println("--- Icons in " + label + " ---");
        System.out.println("Numeric icons count: " + iconIds.size() + 
            (iconIds.isEmpty() ? "" : " (Range: " + Collections.min(iconIds) + " -> " + Collections.max(iconIds) + ")"));
        if (!nonNumeric.isEmpty()) {
            System.out.println("Non-numeric icon files (" + nonNumeric.size() + "): " + nonNumeric.stream().limit(10).toList());
        }
    }

    private static void checkNormal(Path normalDir) throws IOException {
        if (!Files.exists(normalDir)) return;
        System.out.println("--- Normal image files ---");
        Set<String> imgByName = new TreeSet<>();
        Set<Integer> icons = new TreeSet<>();
        Set<String> effects = new TreeSet<>();
        try (var s = Files.walk(normalDir)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String parent = p.getParent().getFileName().toString();
                String name = p.getFileName().toString();
                if (parent.equals("imgbyname")) {
                    imgByName.add(name);
                } else if (parent.equals("effect")) {
                    effects.add(name);
                } else if (parent.equals("icon")) {
                    if (name.endsWith(".png")) {
                        try {
                            icons.add(Integer.parseInt(name.substring(0, name.length() - 4)));
                        } catch (Exception ignored) {}
                    }
                }
            });
        }
        System.out.println("imgbyname (" + imgByName.size() + "): " + imgByName);
        System.out.println("effects (" + effects.size() + "): " + effects);
        System.out.println("icons (" + icons.size() + "): " + 
            (icons.isEmpty() ? "" : "Range " + Collections.min(icons) + " -> " + Collections.max(icons)));
    }

    private static void checkNro2Images(Path dataDir) throws IOException {
        if (!Files.exists(dataDir)) return;
        System.out.println("--- NRO2 Data ---");
        Set<String> img2Files = new TreeSet<>();
        Path anh2 = dataDir.resolve("ảnh 2");
        if (Files.exists(anh2)) {
            try (var s = Files.walk(anh2)) {
                s.filter(Files::isRegularFile).forEach(p -> img2Files.add(p.getFileName().toString()));
            }
        }
        System.out.println("Distinct files in 'ảnh 2' (" + img2Files.size() + "): " + img2Files);

        Path anh = dataDir.resolve("ảnh");
        if (Files.exists(anh)) {
            Set<Integer> icons = new TreeSet<>();
            try (var s = Files.walk(anh)) {
                s.filter(Files::isRegularFile).forEach(p -> {
                    String name = p.getFileName().toString();
                    if (name.endsWith(".png")) {
                        try {
                            icons.add(Integer.parseInt(name.substring(0, name.length() - 4)));
                        } catch (Exception ignored) {}
                    }
                });
            }
            System.out.println("Icons in 'ảnh' (" + icons.size() + "): " + 
                (icons.isEmpty() ? "" : "Range " + Collections.min(icons) + " -> " + Collections.max(icons)));
        }
    }
}
