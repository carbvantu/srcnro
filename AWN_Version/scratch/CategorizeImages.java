package scratch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CategorizeImages {
    public static void main(String[] args) throws Exception {
        Path p1 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1");
        Path p2 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2");

        System.out.println("=== ANALYZING MOUNTS IN NRO1 & NRO2 ===");
        checkNamedMounts(p1, p2);

        System.out.println("\n=== ANALYZING KEY ICON RANGES IN NRO1 (x4) ===");
        checkIconRanges(p1.resolve("data\\girlkun\\icon\\x4"), 20);

        System.out.println("\n=== ANALYZING KEY ICON RANGES IN NRO2 (x4) ===");
        checkIconRanges(p2.resolve("NROZ_BETA\\data\\ảnh\\x4"), 20);
    }

    private static void checkNamedMounts(Path p1, Path p2) throws Exception {
        List<Path> mountFiles = new ArrayList<>();
        findMounts(p1, mountFiles);
        findMounts(p2, mountFiles);
        for (Path p : mountFiles) {
            BufferedImage img = ImageIO.read(p.toFile());
            int w = img != null ? img.getWidth() : -1;
            int h = img != null ? img.getHeight() : -1;
            System.out.println(String.format("Mount file: %-25s | Size: %3dx%-3d | Path: %s", 
                p.getFileName(), w, h, p));
        }
    }

    private static void findMounts(Path root, List<Path> list) throws IOException {
        if (!Files.exists(root)) return;
        try (var s = Files.walk(root)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString().toLowerCase();
                if (name.contains("mount") && name.endsWith(".png")) {
                    list.add(p);
                }
            });
        }
    }

    private static void checkIconRanges(Path dir, int samplePerRange) throws Exception {
        if (!Files.exists(dir)) return;
        List<Integer> ids = new ArrayList<>();
        try (var s = Files.list(dir)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString();
                if (name.endsWith(".png")) {
                    try {
                        ids.add(Integer.parseInt(name.substring(0, name.length() - 4)));
                    } catch (Exception ignored) {}
                }
            });
        }
        Collections.sort(ids);
        
        // Sample several distinct ranges to see what their image dimensions and styles are
        int[] checkPoints = {11059, 13070, 13543, 14048, 15032, 16000, 16347, 17961, 20094, 20615, 21020, 21556, 22775, 25000, 32225};
        for (int cp : checkPoints) {
            int closest = -1;
            for (int id : ids) {
                if (id >= cp) {
                    closest = id;
                    break;
                }
            }
            if (closest != -1 && closest - cp < 50) {
                Path f = dir.resolve(closest + ".png");
                BufferedImage img = ImageIO.read(f.toFile());
                int w = img != null ? img.getWidth() : -1;
                int h = img != null ? img.getHeight() : -1;
                System.out.println(String.format("Range around %5d -> Sample ID %5d: %dx%d (%s)", 
                    cp, closest, w, h, f.getFileName()));
            }
        }
    }
}
