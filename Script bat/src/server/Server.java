/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public final class Server {

    public static final PrintWriter VIDEO = new PrintWriter(System.out, true);
    private static final Scanner TASTIERA = new Scanner(System.in);
    private ThreadServerSocket threadServerSocket;
    private static GestoreParametriConfigurazioneXML gestoreParametriConfig;
    public static GestoreLogsXML gestoreLogsXML;

    Server() { // 1
        caricaConfigurazioni();

        try {
            gestoreLogsXML = new GestoreLogsXML();
        } catch (IOException e) {
            VIDEO.println("Errore durante l'inizializzazione del file di Log: " + e.getMessage() + ".");
            VIDEO.println("Termino Server di Log.");
            System.exit(0);
        }
    }

    private void caricaConfigurazioni() { // 2
        try {
            gestoreParametriConfig = new GestoreParametriConfigurazioneXML("configurazioni.xml", "configurazioni.xsd");
            if (gestoreParametriConfig.leggiFileDiConfigurazione()) {
                VIDEO.println("Parametri di configurazione caricati correttamente.");
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            VIDEO.println("Errore durante la lettura del file di configurazione: " + e.getMessage() + ".");
            VIDEO.println("Termino Server di Log.");
            System.exit(0);
        }
    }

    private void avviaServer() { // 3
        try {
            threadServerSocket = new ThreadServerSocket();
            threadServerSocket.start();
            VIDEO.println("Server avviato. [" + ottieniIndirizzoIP() + " : " + GestoreParametriConfigurazioneXML.parametri.portaServer + "]");
        } catch (IOException e) {
            VIDEO.println("Errore durante l'avvio del Server di Log: " + e.getMessage());
            VIDEO.println("Termino Server di Log.");
            System.exit(0);
        }
    }

    private void fermaServer() { // 4
        String scelta_2 = "";

        VIDEO.println("\nSei sicuro di voler fermare il Server? [Y/n]");
        VIDEO.print(": ");
        VIDEO.flush();

        boolean inputValido = false;
        do {
            try {
                scelta_2 = TASTIERA.nextLine();

                if (!(scelta_2.charAt(0) == 'Y' || scelta_2.charAt(0) == 'n')) {
                    throw new InputMismatchException();
                }

                inputValido = true;
            } catch (StringIndexOutOfBoundsException siobe) {

            } catch (InputMismatchException ime) {
                VIDEO.println("Valore non valido.");
                VIDEO.print(": ");
                VIDEO.flush();
            }
        } while (!inputValido);

        if (scelta_2.charAt(0) == 'Y') {
            try {
                threadServerSocket.fermaServerSocket();
            } catch (IOException | InterruptedException e) {
                VIDEO.println("Errore durante l'arresto del Server di Log: " + e.getMessage());
                VIDEO.println("Termino Server di Log.");
            } finally {
                System.exit(0);
            }
        }
    }

    private void stampaLogs() { // 5
        try {
            String logs = gestoreLogsXML.leggiLogs();
            VIDEO.println(logs);
        } catch (IOException ex) {
            VIDEO.print("Errore lettura Logs: " + ex.getMessage() + "\n: ");
            VIDEO.flush();
        }
    }

    private void stampaListaClientConnessi() { // 6
        if (threadServerSocket != null) {
            VIDEO.println("\nClient attualmente connessi al Server di Log:");
            
            Map clientsConnessi = threadServerSocket.ottieniListaClientConnessi();

            Iterator it = clientsConnessi.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                VIDEO.println(pair.getKey());
            }
        } else {
            VIDEO.println("Server di Log non avviato.");
        }
    }

    private void stampaMenuServer() { // 7
        VIDEO.println("\n--------------");
        VIDEO.println("Server di Log");
        VIDEO.println("--------------");
        VIDEO.println("1) Avvia");
        VIDEO.println("2) Stampa logs");
        VIDEO.println("3) Stampa lista Client connessi");
        VIDEO.println("4) Ferma");
        VIDEO.println("--------------");
        VIDEO.print(": ");
        VIDEO.flush();
    }
    
    private String ottieniIndirizzoIP() throws SocketException { // 8
        String indirizzoIP = "";
        Enumeration e;
        e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                indirizzoIP = i.getHostAddress();
            }
            break;
        }

        return indirizzoIP;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { // 0
        // TODO code application logic here        
        Server server = new Server();

        while (true) {
            server.stampaMenuServer();

            int scelta = -1;
            boolean inputValido = false;
            do {
                try {
                    scelta = TASTIERA.nextInt();
                    inputValido = true;
                } catch (InputMismatchException ime) {
                    VIDEO.println("Valore non valido.");
                    VIDEO.print(": ");
                    VIDEO.flush();
                    TASTIERA.nextLine();
                }
            } while (!inputValido);

            switch (scelta) {
                case 1:
                    server.avviaServer();
                    break;
                case 2:
                    server.stampaLogs();
                    break;
                case 3:
                    server.stampaListaClientConnessi();
                    break;
                case 4:
                    server.fermaServer();
                    break;
                default:
                    VIDEO.println("Scelta non valida.");
                    break;
            }
        }
    }
}

/*
Note:
(0) Funzione main.
    Definisce un oggetto Server.
    Gestisce il menu testuale sul terminale chiamando le opportune funzione
    dell'oggetto Server.

(1) Costruttore Server().
    Carica le configurazioni presenti nel file .xml e inizializza il gestore di
    logs XML.

(2) Funzione caricaConfigurazioni().
    Legge il file di configurazione locale "configurazione.xml" e carica le
    informazioni presenti: la porta su cui avviare il Server di Log, la
    locazione del file di testo dei logs e la locazione del file .xsd da
    utilizzare per validare i singoli log ricevuti.

(3) Funzione avviaServer().
    Crea un nuovo oggetto del tipo ThreadServerSocket(), passando come argomento
    la porta su cui avviare il ServerSocket in ascolto.

(4) Funzione fermaServer().
    Ferma il ServerSocket in ascolto di connessioni da parte dei vari Clients.
    Utilizza la funzione fermaServerSocket() della classe ThreadServerSocket
    che disconnette i Clients connessi (chiude i socket ad essi associati)
    e infine chiude il ServerSocket in attesa di nuove connessioni.

(5) stampaLogs().
    Legge il file di log locale e stampare i Logs presenti su terminale.

(6) Funzione stampaListaClientConnessi().
    Stampa l'elenco dei Client connessi al Server di Log al momento.

(7) Funzione stampaMenuServer().
    Funzione utilizzata dal metodo main per stampare il menu sul terminale.

(8) Funzione ottieniIndirizzoIP().
    Ottiene l'indirizzo IP associato alla prima interfaccia di rete attiva. Si 
    puo' cosi' ottenere l'indirizzo IP associato al computer dal router che 
    gestisce la rete e permettere di eseguire l'applicativo su piu' computer
    della rete per lo scambio di messaggi.
 */
