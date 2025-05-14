import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Esperant connexio...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        socket.close();
        serverSocket.close();
        System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
    }

    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        String nomFitxer = (String) in.readObject();
        System.out.println("Nomfitxer rebut: " + nomFitxer);

        if (nomFitxer == null || nomFitxer.isEmpty()) {
            System.out.println("Nom del fitxer buit o nul. Sortint...");
            return;
        }

        Fitxer fitxer = new Fitxer(nomFitxer);
        byte[] contingut = fitxer.getContingut();

        if (contingut != null) {
            System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
            out.writeObject(contingut);
            out.flush();
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        } else {
            System.out.println("Error llegint el fitxer.");
        }
    }

    public static void main(String[] args) {
        try {
            Servidor servidor = new Servidor();
            Socket socket = servidor.connectar();
            servidor.enviarFitxers(socket);
            servidor.tancarConnexio(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
