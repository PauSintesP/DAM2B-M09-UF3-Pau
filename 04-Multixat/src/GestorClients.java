import java.io.*;
import java.net.Socket;

public class GestorClients implements Runnable {
    private final Socket client;
    private final ServidorXat servidor;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) {
        this.client = client;
        this.servidor = servidor;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            sortir = true;
        }
    }

    public String getNom() {
        return nom;
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(Missatge.getMissatgePersonal(remitent, missatge));
            oos.flush();
        } catch (IOException e) {
            sortir = true;
        }
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (Exception e) {
            sortir = true;
        } finally {
            try {
                client.close();
            } catch (IOException e) {}
        }
    }

    private void processaMissatge(String missatgeRaw) throws IOException {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                servidor.eliminarClient(nom);
                sortir = true;
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                servidor.enviarMissatgePersonal(parts[1], nom, parts[2]);
                break;
            case Missatge.CODI_MSG_GRUP:
                servidor.enviarMissatgeGrup(parts[1]);
                break;
            default:
                System.out.println("Codi desconegut.");
        }
    }
}
