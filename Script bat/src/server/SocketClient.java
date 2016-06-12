/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import messaggio.Messaggio;
import messaggio.TipoMessaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class SocketClient extends Thread {

    private String emailClient;
    private final Socket so;
    private final ObjectInputStream ingresso;
    private final ObjectOutputStream uscita;

    private boolean threadSospeso;

    SocketClient(Socket s) throws IOException { // 0
        so = s;
        ingresso = new ObjectInputStream(so.getInputStream());
        uscita = new ObjectOutputStream(so.getOutputStream());

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
                Messaggio messaggioRicevuto = (Messaggio) ingresso.readObject();
                if (null != messaggioRicevuto.getTipo()) {
                    switch (messaggioRicevuto.getTipo()) {
                        case IDENTIFICAZIONE_CLIENT:
                            emailClient = messaggioRicevuto.getTesto();
                            if (ThreadServerSocket.identificaNuovoClient(emailClient, this)) {
                                Server.VIDEO.print("Identificato nuovo Client: " + emailClient + "\n: ");
                                Server.VIDEO.flush();
                            } else {
                                Server.VIDEO.print("Identificazione Client " + emailClient + " fallita. Un altro Client con lo stesso indirizzo email e' attualmente connesso.\n: ");
                                Server.VIDEO.flush();
                                inviaMessaggio(TipoMessaggio.IDENTIFICAZIONE_CLIENT_FALLITA, "Identificazione fallita. Indirizzo Email in uso.");
                                terminaSocket();
                            }
                            break;
                        case CLIENT_DISCONNESSO:
                            emailClient = messaggioRicevuto.getTesto();
                            ThreadServerSocket.rimuoviClient(emailClient);
                            terminaSocket();
                            break;
                        case MESSAGGIO_LOG:
                            ThreadServerSocket.registraLogXML(messaggioRicevuto);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (InterruptedException | IOException | ClassNotFoundException ex) {
            if (!threadSospeso) {
                Server.VIDEO.print("Errore durante la comunicazione con il Client: " + ex.getMessage() + "\n: ");
                Server.VIDEO.flush();
            }
            ThreadServerSocket.rimuoviClient(emailClient);
            terminaSocket();
        }
    }

    private boolean inviaMessaggio(TipoMessaggio tipo, String testo) { // 2
        try {
            Messaggio messaggio = new Messaggio(tipo, "Server di Log", emailClient, testo);
            uscita.writeObject(messaggio);
        } catch (IOException e) {
            System.out.println("Errore durante l'invio del messaggio al Client: " + e.getMessage() + "\n: ");
            Server.VIDEO.flush();
            return false;
        }

        return true;
    }

    public void disconnetti() { // 3
        try {
            if (so != null) {
                if (inviaMessaggio(TipoMessaggio.SERVER_LOG_ARRESTATO, "Server Fermato")) {
                    sospendi();
                    ingresso.close();
                    uscita.close();
                    so.close();
                }
            }
        } catch (IOException e) {
            Server.VIDEO.print("Errore durante la disconnessione del Client: " + e.getMessage() + "\n: ");
            Server.VIDEO.flush();
        }
    }

    private void terminaSocket() { // 4
        try {
            if (so != null) {
                sospendi();
                ingresso.close();
                uscita.close();
                so.close();
            }
        } catch (IOException e) {
        }
    }

    public void sospendi() { // 5
        threadSospeso = true;
    }

    synchronized void riprendi() { // 6
        threadSospeso = false;
        notify();
    }
}

/*
Note:
(0) Costruttore SocketClient().
    Inizializza Socket, ObjectInputStream, e ObjectOutputStream per il Client 
    a cui e' associato.

(1) Funzione run().
    Rimane in attesa di ricezione dei messaggi da parte del Client a cui e'
    associato. Possono arrivare messaggi di tipo:
        IDENTIFICAZIONE_CLIENT: I Client si e' appena connesso al Server di 
                                Log e si identifica con l'indirizzo email con 
                                cui si e' connesso al Servizio di Messagistica.
        CLIENT_DISCONNESSO:     Il Client si e' disconnesso e va quindi rimosso 
                                dai Client connessi al Server di Log.
        MESSAGGIO_LOG:          Messaggio contenente come parametro "testo" il 
                                log XML che deve essere registrato nel file di 
                                log aperto locale.

(2) Funzione inviaMessaggio().
    Invia un oggetto della classe Messaggio al Client a cui l'instanza di 
    SocketClient e' associata. Bisogna indicare il tipo e il testo del 
    messaggio che si desidera inviare al Client.

(3) Funzione disconnetti().
    Utilizzato in caso di arresto del Server di Log per comunicare al Client
    l'evento. Viene inviato al Client un messaggio di tipo SERVER_LOG_ARRESTATO.

(4) Funzione terminaSocket().
    Utilizzato per terminare Socket, ObjectInputStream, e ObjectOutputStream 
    per il Client a cui e' associato. Viene utilizzata in caso di errori
    durante la comunicazione con il Client.

(5) Funzione sospendi().
    Funzione di gestione del Thread. Sospende la ricezione dei messaggi per 
    il socket in ascolto connesso al Server di Log.

(6) Funzione riprendi().
    Funzione di gestione del Thread. Riprende la ricezione dei messaggi per
    il socket in ascolto connesso al Server di Log.
 */
