package Ex4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientSMTP {

    private static final String SERVEUR = "localhost";
    private static final int PORT = 25;
    private static final String EXPEDITEUR = "georges@test.local";

    private static BufferedReader entree;
    private static PrintWriter sortie;

    public static void main(String[] args) {
        // Liste des noms associés à leurs adresses
        Map<String, String> destinataires = new HashMap<>();
        destinataires.put("Amel", "amel.zannouh@etu.univ-lyon1.fr");
        destinataires.put("David", "david-stefan.voicu@etu.univ-lyon1.fr");

        try {
            Socket socket = new Socket(SERVEUR, PORT);
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sortie = new PrintWriter(socket.getOutputStream());

            System.out.println(entree.readLine()); // 220 : message d'accueil du serveur
            envoyer("HELO localhost");

            for (String nom : destinataires.keySet()) {
                String adresse = destinataires.get(nom);

                envoyer("MAIL FROM:<" + EXPEDITEUR + ">");
                envoyer("RCPT TO:<" + adresse + ">");
                envoyer("DATA");

                sortie.print("From: " + EXPEDITEUR + "\r\n");
                sortie.print("To: " + adresse + "\r\n");
                sortie.print("Subject: Bonjour\r\n");
                sortie.print("\r\n"); // ligne vide entre les en-têtes et le corps
                sortie.print("Bonjour " + nom + " !\r\n");
                envoyer("."); // un point seul termine le message
            }

            envoyer("QUIT");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Envoie une commande au serveur et affiche sa réponse
    private static void envoyer(String commande) throws Exception {
        sortie.print(commande + "\r\n"); // SMTP impose \r\n en fin de ligne
        sortie.flush();
        System.out.println("> " + commande);
        System.out.println(entree.readLine());
    }
}