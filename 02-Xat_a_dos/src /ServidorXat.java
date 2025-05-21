import java.io.*;
import java.net.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (clientSocket != null) clientSocket.close();
        if (serverSocket != null) serverSocket.close();
        System.out.println("Servidor aturat.");
    }

    public String getNom(BufferedReader in) throws IOException {
        System.out.println("Rebut: Escriu el teu nom:");
        return in.readLine();
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarServidor();
            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getRemoteSocketAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(servidor.clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(servidor.clientSocket.getOutputStream(), true);

            String nomClient = servidor.getNom(in);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat filServidor = new FilServidorXat(in, nomClient);
            Thread thread = new Thread(filServidor);
            thread.start();

            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = consola.readLine();
                out.println(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));

            thread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                servidor.pararServidor();
            } catch (IOException e) {
                System.err.println("Error en tancar el servidor: " + e.getMessage());
            }
        }
    }
}