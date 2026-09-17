package scratch;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CheckExistingAssets {
    public static void main(String[] args) throws IOException {
        Path serverIconX4 = Paths.get("data\\icon\\x4");
        Path serverEffectX4 = Paths.get("data\\effect\\x4");
        Path serverImgByNameX4 = Paths.get("data\\img_by_name\\x4");

        System.out.println("Server icon x4 exists: " + Files.exists(serverIconX4));
        System.out.println("Server effect x4 exists: " + Files.exists(serverEffectX4));
        System.out.println("Server img_by_name x4 exists: " + Files.exists(serverImgByNameX4));

        Set<String> existingIcons = getFiles(serverIconX4);
        Set<String> existingEffects = getFiles(serverEffectX4);
        Set<String> existingImgByName = getFiles(serverImgByNameX4);

        System.out.println("Existing icons in server x4: " + existingIcons.size());
        System.out.println("Existing effects in server x4: " + existingEffects.size());
        System.out.println("Existing img_by_name in server x4: " + existingImgByName.size());

        // Check how many icons from nro1 and nro2 are NOT in server
        Path nro1IconX4 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro1\\data\\girlkun\\icon\\x4");
        Path nro2IconX4 = Paths.get("c:\\Users\\vtson\\Downloads\\AWN_Version\\file ảnh\\nro2\\NROZ_BETA\\data\\ảnh\\x4");

        Set<String> nro1Icons = getFiles(nro1IconX4);
        Set<String> nro2Icons = getFiles(nro2IconX4);

        System.out.println("nro1 icons in x4: " + nro1Icons.size());
        System.out.println("nro2 icons in x4: " + nro2Icons.size());

        Set<String> missingFromNro1 = new TreeSet<>(nro1Icons);
        missingFromNro1.removeAll(existingIcons);
        System.out.println("nro1 icons missing from server: " + missingFromNro1.size() + 
            (missingFromNro1.isEmpty() ? "" : " Samples: " + missingFromNro1.stream().limit(10).toList()));

        Set<String> missingFromNro2 = new TreeSet<>(nro2Icons);
        missingFromNro2.removeAll(existingIcons);
        System.out.println("nro2 icons missing from server: " + missingFromNro2.size() + 
            (missingFromNro2.isEmpty() ? "" : " Samples: " + missingFromNro2.stream().limit(10).toList()));
    }

    private static Set<String> getFiles(Path dir) throws IOException {
        Set<String> set = new TreeSet<>();
        if (Files.exists(dir)) {
            try (var s = Files.list(dir)) {
                s.filter(Files::isRegularFile).forEach(p -> set.add(p.getFileName().toString()));
            }
        }
        return set;
    }
}
