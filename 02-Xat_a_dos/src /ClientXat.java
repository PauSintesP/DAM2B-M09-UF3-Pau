import java.io.*;
import java.net.*;
public class ClientXat {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final String MSG_SORTIR = "sortir";
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Client connectat a " + HOST + ":" + PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) {
        out.println(missatge);
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();

            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriu el teu nom: ");
            String nom = consola.readLine();
            client.enviarMissatge(nom);

            FilLectorCX filLector = new FilLectorCX(client.in);
            Thread thread = new Thread(filLector);
            thread.start();

            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = consola.readLine();
                client.enviarMissatge(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));

            thread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                client.tancarClient();
            } catch (IOException e) {
                System.err.println("Error en tancar el client: " + e.getMessage());
            }
        }
    }
}