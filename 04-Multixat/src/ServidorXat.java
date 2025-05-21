import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;
    private final Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

        while (!sortir) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connectat: " + socket.getInetAddress());

            GestorClients client = new GestorClients(socket, this);
            new Thread(client).start();
        }
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null) serverSocket.close();
    }

    public synchronized void finalitzarXat() throws IOException {
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        clients.clear();
        sortir = true;
        pararServidor();
        System.out.println("Tancant tots els clients.");
    }

    public synchronized void afegirClient(GestorClients client) {
        clients.put(client.getNom(), client);
        enviarMissatgeGrup(Missatge.getMissatgeGrup("Entra: " + client.getNom()));
        System.out.println("DEBUG: multicast Entra: " + client.getNom());
    }

    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) clients.remove(nom);
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        for (GestorClients client : clients.values()) {
            client.enviarMissatge("Servidor", missatge);
        }
    }

    public synchronized void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        GestorClients client = clients.get(destinatari);
        if (client != null) {
            client.enviarMissatge(remitent, missatge);
        }
    }

    public static void main(String[] args) throws IOException {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        servidor.pararServidor();
    }
}
