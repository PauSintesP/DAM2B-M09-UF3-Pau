import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Fitxer {
    private String nom;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() throws IOException {
        File fitxer = new File(nom);
        if (fitxer.exists() && fitxer.isFile()) {
            return Files.readAllBytes(fitxer.toPath());
        } else {
            System.out.println("Fitxer no trobat o no vàlid.");
            return null;
        }
    }
}
