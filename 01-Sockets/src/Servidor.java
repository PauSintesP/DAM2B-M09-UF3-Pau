import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() throws IOException {
        srvSocket = new ServerSocket(PORT);
        System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
        System.out.println("Esperant connexions a " + HOST + ":" + PORT);
        
        clientSocket = srvSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
    }

    public void repDades() throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );

        String linia;
        while((linia = in.readLine()) != null) {
            System.out.println("Rebut: " + linia);
        }
        
        in.close();
    }

    public void tanca() throws IOException {
        if(clientSocket != null) clientSocket.close();
        if(srvSocket != null) srvSocket.close();
        System.out.println("Servidor tancat.");
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            servidor.connecta();
            servidor.repDades();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                servidor.tanca();
            } catch (IOException e) {
                System.err.println("Error en tancar: " + e.getMessage());
            }
        }
    }
}