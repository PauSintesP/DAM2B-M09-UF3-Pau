import java.io.BufferedReader;
import java.io.IOException;

public class FilLectorCX implements Runnable {
    private BufferedReader in;

    public FilLectorCX(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            System.out.println("Fil de lectura iniciat.");
            String missatge;
            while ((missatge = in.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (IOException e) {
            System.err.println("Error al fil del client: " + e.getMessage());
        }
    }
}