/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
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

    private static final PrintWriter VIDEO = new PrintWriter(System.out, true);
    private final ServerSocket serverSocket;
    private final Map clientsConnessi;

    ThreadServerSocket() throws IOException { // 0
        serverSocket = new ServerSocket(GestoreParametriConfigurazioneXML.parametri.portaServer);
        clientsConnessi = new LinkedHashMap();

        start();
    }

    @Override
    public void run() { // 1
        try {
            while (true) {
                Socket aus = serverSocket.accept();
                new SocketClient(this, aus);
            }
        } catch (IOException e) {
            VIDEO.print("Errore durante l'accettazione di un nuovo Client: " + e.getMessage() + "\n: ");
            VIDEO.flush();
        }
    }

    public boolean identificaNuovoClient(String email, SocketClient client) { // 2
        if (clientsConnessi.containsKey(email)) {
            return false;
        }
        clientsConnessi.put(email, client);

        return true;
    }

    public void registraLogXML(Messaggio m) { // 3
        try {
            if (Server.gestoreLogsXML.registraLogXML(m)) {
                VIDEO.print("Messaggio di Log da " + m.getMittente() + " registrato.\n: ");
                VIDEO.flush();
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            VIDEO.print("Errore durante la registrazione di un Log: " + e.getMessage() + "\n: ");
        }
    }

    public void rimuoviClient(String email) { // 4
        if (clientsConnessi.containsKey(email)) {
            clientsConnessi.remove(email);
        }
        VIDEO.print("Rimosso Client: " + email + "\n: ");
        VIDEO.flush();
    }

    public void fermaServerSocket() throws IOException, InterruptedException { // 5
        stop();

        Iterator it = clientsConnessi.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SocketClient client = (SocketClient) pair.getValue();
            client.disconnetti();
            it.remove();
        }

        serverSocket.close();
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
 */
