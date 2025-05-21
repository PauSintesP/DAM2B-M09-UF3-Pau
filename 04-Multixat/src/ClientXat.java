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
        if (oos == null) {
            System.out.println("oos null. Sortint...");
            sortir = true;
            return;
        }
        oos.writeObject(missatge);
        oos.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (ois != null) {
                System.out.println("Flux d'entrada tancat.");
                ois.close();
            }
            if (oos != null) {
                System.out.println("Flux de sortida tancat.");
                oos.close();
            }
            if (socket != null) socket.close();
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
                        String codi = Missatge.getCodiMissatge(missatgeCru);
                        String[] parts = Missatge.getPartsMissatge(missatgeCru);

                        if (codi == null || parts == null) {
                            System.out.println("Missatge mal format. Ignorant...");
                            continue;
                        }

                        switch (codi) {
                            case Missatge.CODI_SORTIR_TOTS:
                                sortir = true;
                                break;
                            case Missatge.CODI_MSG_PERSONAL:
                                if (parts.length >= 3) {
                                    String remitent = parts[1];
                                    String missatge = parts[2];
                                    System.out.println("Missatge personal de (" + remitent + "): " + missatge);
                                } else {
                                    System.out.println("Missatge personal mal format");
                                }
                                break;
                            case Missatge.CODI_MSG_GRUP:
                                if (parts.length >= 2) {
                                    System.out.println("Missatge al grup: " + parts[1]);
                                } else {
                                    System.out.println("Missatge grupal mal format");
                                }
                                break;
                            default:
                                System.out.println("ERROR: Codi desconegut: " + codi);
                        }
                    } catch (EOFException e) {
                        System.out.println("Connexió tancada pel servidor.");
                        sortir = true;
                        break;
                    } catch (Exception e) {
                        System.out.println("Error rebent missatge. Sortint...");
                        e.printStackTrace();
                        sortir = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error inicialitzant ObjectInputStream");
                e.printStackTrace();
            } finally {
                tancarClient();
            }
        }).start();
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- Sortir del client");
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

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);
        
        try {
            client.connecta();
            client.executarLectura();
            client.ajuda();

            while (!client.sortir) {
                String opcio = getLinea(sc, "> ", false);
                if (opcio.trim().isEmpty()) {
                    client.sortir = true;
                    break;
                }

                try {
                    switch (opcio) {
                        case "1":
                            String nom = getLinea(sc, "Nom: ", true);
                            client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                            break;
                        case "2":
                            String dest = getLinea(sc, "Destinatari: ", true);
                            String msg = getLinea(sc, "Missatge: ", true);
                            client.enviarMissatge(Missatge.getMissatgePersonal(dest, msg));
                            break;
                        case "3":
                            String msgGrup = getLinea(sc, "Missatge al grup: ", true);
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
                } catch (IOException e) {
                    System.out.println("Error enviant missatge: " + e.getMessage());
                    client.sortir = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error connectant al servidor: " + e.getMessage());
        } finally {
            client.tancarClient();
            sc.close();
        }
    }
}