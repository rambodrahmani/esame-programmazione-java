/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.ServerSocket;
import java.net.Socket;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import messaggio.Messaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class ThreadServerSocket extends Thread {

    private final ServerSocket serverSocket;
    private static LinkedHashMap<String, SocketClient> clientsConnessi;

    private boolean threadSospeso;

    ThreadServerSocket() throws IOException { // 0
        serverSocket = new ServerSocket(GestoreParametriConfigurazioneXML.parametri.portaServer);
        clientsConnessi = new LinkedHashMap<>();

        threadSospeso = false;
    }

    @Override
    public void run() { // 1
        try {
            while (true) {
                synchronized (this) {
                    while (threadSospeso) {
                        wait();
                    }
                }
                Socket nuovoSocket = serverSocket.accept();
                SocketClient nuovoClient = new SocketClient(nuovoSocket);
                nuovoClient.start();
            }
        } catch (InterruptedException | IOException ex) {
            if (!threadSospeso) {
                Server.VIDEO.print("Errore durante l'accettazione di un nuovo Client: " + ex.getMessage() + "\n: ");
                Server.VIDEO.flush();
            }
        }
    }

    public static boolean identificaNuovoClient(String email, SocketClient client) { // 2
        try {
            if (clientsConnessi.containsKey(email)) {
                return false;
            }
            clientsConnessi.put(email, client);
        } catch (Exception ex) {

        }

        return true;
    }

    public static void registraLogXML(Messaggio m) { // 3
        try {
            if (Server.gestoreLogsXML.registraLogXML(m)) {
                Server.VIDEO.print("Messaggio di Log da " + m.getMittente() + " registrato.\n: ");
                Server.VIDEO.flush();
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            Server.VIDEO.print("Errore durante la registrazione di un Log: " + e.getMessage() + "\n: ");
            Server.VIDEO.flush();
        }
    }

    public static void rimuoviClient(String email) { // 4
        if (clientsConnessi.containsKey(email)) {
            clientsConnessi.remove(email);
        }
        Server.VIDEO.print("Rimosso Client: " + email + "\n: ");
        Server.VIDEO.flush();
    }

    public void fermaServerSocket() throws IOException, InterruptedException { // 5
        sospendi();

        Iterator it = clientsConnessi.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SocketClient client = (SocketClient) pair.getValue();
            client.disconnetti();
            it.remove();
        }

        serverSocket.close();
    }

    public Map ottieniListaClientConnessi() { // 6
        return clientsConnessi;
    }

    public void sospendi() { // 7
        threadSospeso = true;
    }

    synchronized void riprendi() { // 8
        threadSospeso = false;
        notify();
    }
}

/*
Note:
(0) Costruttore ThreadServerSocket().
    Avvia un ServerSocket nella porta indicata nel file di configurazione XML
    locale. Inizializza la variabile clientsConnessi che contiene i Client
    connessi al Server di Log.

(1) Funzione run().
    Funzione run() del Thread esteso dalla classe.
    Rimane in attesa di connessioni da parte dei vari Client. Ad ogni Client
    che si connette associa un oggetto della classe SocketClient.

(2) Funzione identificaNuovoClient().
    Controlla che un altro Client non si sia gia' connesso al Server con lo
    stesso indirizzo email e inserisci il Client appena connesso nella 
    variabile clientsConnessi nella quale ad ogni instanza della classe 
    SocketClient corrisponde l'indirizzo email del Client a cui e' associato.

(3) Funzione registraLogXML().
    Passa l'oggetto Messaggio ricevuto dal Client alla classe GestoreLogsXML 
    che si occupa di validare e registrare il log ricevuto dal Client.

(4) Funzione rimuoviClient().
    Rimuove l'istanza della classe SocketClient associata al Client il quale 
    indirizzo email viene passato come argomento della funzione.

(5) Funzione fermaServerSocket().
    Arresta il ServerSocket in attesa di connessioni da parte dei vari 
    Client, comunica a tutti i Client connessi l'arresto del Server di Log e 
    li rimuove dalla variabile clientsConnessi chiudendo l'istanza dell'oggetto 
    SocketClient associato a ciascuno.

(6) Funzione ottieniListaClientConnessi().
    Restitusice la lista dei Client connessi al Server di Log.

(7) Funzione sospendi().
    Funzione di gestione del Thread. Sospende la ricezione dei messaggi per 
    il socket in ascolto connesso al Server di Log.

(8) Funzione riprendi().
    Funzione di gestione del Thread. Riprende la ricezione dei messaggi per
    il socket in ascolto connesso al Server di Log.
 */
