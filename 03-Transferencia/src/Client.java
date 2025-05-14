import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp";
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
        String nomFitxer = scanner.nextLine();

        if (nomFitxer.equalsIgnoreCase("sortir")) {
            System.out.println("Sortint...");
            return;
        }

        out.writeObject(nomFitxer);
        out.flush();

        byte[] contingut = (byte[]) in.readObject();

        String nomGuardar = DIR_ARRIBADA + "/" + new File(nomFitxer).getName();
        FileOutputStream fos = new FileOutputStream(nomGuardar);
        fos.write(contingut);
        fos.close();

        System.out.println("Fitxer rebut i guardat com: " + nomGuardar);
    }

    public void tancarConnexio() throws IOException {
        socket.close();
        System.out.println("Connexio tancada.");
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.connectar();
            client.rebreFitxers();
            client.tancarConnexio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
