package Ex7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientPOP {

    private static final String SERVEUR = "localhost";
    private static final int PORT = 110;
    private static final String UTILISATEUR = "alice@test.local"; // adresse complète sur hMailServer
    private static final String MOT_DE_PASSE = "motdepasse";

    private static BufferedReader entree;
    private static PrintWriter sortie;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVEUR, PORT);
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sortie = new PrintWriter(socket.getOutputStream());

            System.out.println(entree.readLine()); // +OK : message d'accueil du serveur
            envoyer("USER " + UTILISATEUR);
            if (!envoyer("PASS " + MOT_DE_PASSE).startsWith("+OK")) {
                System.out.println("Authentification refusée");
                socket.close();
                return;
            }

            // STAT répond "+OK <nombre de messages> <taille totale>"
            String stat = envoyer("STAT");
            int nbMessages = Integer.parseInt(stat.split(" ")[1]);

            for (int i = 1; i <= nbMessages; i++) {
                System.out.println("\n===== Message " + i + " =====");
                envoyer("RETR " + i);
                String ligne;
                while (!(ligne = entree.readLine()).equals(".")) { // un point seul termine le message
                    System.out.println(ligne);
                }
            }

            envoyer("QUIT");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Envoie une commande au serveur, affiche sa réponse et la renvoie
    private static String envoyer(String commande) throws Exception {
        sortie.print(commande + "\r\n"); // POP3 impose \r\n en fin de ligne
        sortie.flush();
        System.out.println("> " + commande);
        String reponse = entree.readLine();
        System.out.println(reponse);
        return reponse;
    }
}