import java.io.BufferedReader;
import java.io.IOException;

public class FilServidorXat implements Runnable {
    private BufferedReader in;
    private String nomClient;

    public FilServidorXat(BufferedReader in, String nomClient) {
        this.in = in;
        this.nomClient = nomClient;
    }

    @Override
    public void run() {
        try {
            System.out.println("Fil de " + nomClient + " iniciat.");
            String missatge;
            while ((missatge = in.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equalsIgnoreCase("sortir")) {
                    System.out.println("Fil de xat finalitzat.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al fil del servidor: " + e.getMessage());
        }
    }
}