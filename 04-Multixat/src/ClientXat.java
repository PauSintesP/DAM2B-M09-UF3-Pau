import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    public boolean sortir = false;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        System.out.println("Client connectat a localhost:9999");
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        oos.writeObject(missatge);
        oos.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            System.out.println("Tancant client...");
        } catch (IOException ignored) {}
    }

    public void executarLectura() {
        new Thread(() -> {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                while (!sortir) {
                    try {
                        String missatgeCru = (String) ois.readObject();
                        if (missatgeCru == null) {
                            System.out.println("Missatge rebut nul. Sortint...");
                            sortir = true;
                            break;
                        }

                        System.out.println("Missatge rebut cru: " + missatgeCru);

                        String codi = Missatge.getCodiMissatge(missatgeCru);
                        String[] parts = Missatge.getPartsMissatge(missatgeCru);

                        if (codi == null || parts == null) {
                            System.out.println("Missatge mal format. Sortint...");
                            sortir = true;
                            break;
                        }

                        switch (codi) {
                            case Missatge.CODI_SORTIR_TOTS:
                                sortir = true;
                                break;
                            case Missatge.CODI_MSG_PERSONAL:
                                if (parts.length >= 3) {
                                    System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                                } else {
                                    System.out.println("Missatge personal mal format");
                                }
                                break;
                            case Missatge.CODI_MSG_GRUP:
                                if (parts.length >= 2) {
                                    System.out.println(parts[1]);
                                } else {
                                    System.out.println("Missatge grupal mal format");
                                }
                                break;
                            default:
                                System.out.println("ERROR: codi desconegut.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error rebent missatge. Sortint...");
                        sortir = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No s'ha pogut obrir l'ObjectInputStream");
            } finally {
                tancarClient();
            }
        }).start();
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc)-> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public static String getLinea(Scanner sc, String missatge, boolean obligatori) {
        System.out.print(missatge);
        String linia;
        do {
            linia = sc.nextLine();
        } while (obligatori && linia.trim().isEmpty());
        return linia;
    }

    public static void main(String[] args) throws IOException {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);
        client.connecta();
        client.executarLectura();
        client.ajuda();

        while (!client.sortir) {
            String opcio = getLinea(sc, "", false);
            if (opcio.trim().isEmpty()) {
                client.sortir = true;
                break;
            }

            switch (opcio) {
                case "1":
                    String nom = getLinea(sc, "Introdueix el nom: ", true);
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    String dest = getLinea(sc, "Destinatari:: ", true);
                    String msgPers = getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgePersonal(dest, msgPers));
                    break;
                case "3":
                    String msgGrup = getLinea(sc, "Missatge grupal: ", true);
                    client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                    break;
                case "4":
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    client.sortir = true;
                    break;
                case "5":
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    client.sortir = true;
                    break;
                default:
                    System.out.println("Opció no vàlida.");
            }
        }

        client.tancarClient();
        sc.close();
    }
}
