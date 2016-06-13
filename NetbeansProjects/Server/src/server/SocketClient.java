/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import messaggio.Messaggio;
import messaggio.TipoMessaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class SocketClient extends Thread {

    private static final PrintWriter VIDEO = new PrintWriter(System.out, true);
    private String emailContatto;
    private final ThreadServerSocket threadServerSocket;
    private final Socket so;
    private final ObjectInputStream ingresso;
    private final ObjectOutputStream uscita;

    SocketClient(ThreadServerSocket tServerSocket, Socket s) throws IOException { // 0
        threadServerSocket = tServerSocket;
        so = s;
        ingresso = new ObjectInputStream(so.getInputStream());
        uscita = new ObjectOutputStream(so.getOutputStream());

        start();
    }

    @Override
    public void run() { // 1
        try {
            boolean continua = true;
            while (continua) {
                Messaggio messaggioRicevuto = (Messaggio) ingresso.readObject();
                if (null != messaggioRicevuto.getTipo()) {
                    switch (messaggioRicevuto.getTipo()) {
                        case IDENTIFICAZIONE_CLIENT:
                            emailContatto = messaggioRicevuto.getTesto();
                            if (threadServerSocket.identificaNuovoClient(emailContatto, this)) {
                                VIDEO.print("Identificato nuovo Client: " + emailContatto + "\n: ");
                                VIDEO.flush();
                            } else {
                                VIDEO.print("Identificato Client: " + emailContatto + " fallita. Un altro Client con lo stesso indirizzo email e' attualmente connesso.\n: ");
                                VIDEO.flush();
                                inviaMessaggio(TipoMessaggio.IDENTIFICAZIONE_CLIENT_FALLITA, "Identificazione fallita. Indirizzo Email in uso.");
                                terminaSocket();
                            }
                            break;
                        case CLIENT_DISCONNESSO:
                            emailContatto = messaggioRicevuto.getTesto();
                            threadServerSocket.rimuoviClient(emailContatto);
                            continua = false;
                            break;
                        case MESSAGGIO_LOG:
                            threadServerSocket.registraLogXML(messaggioRicevuto);
                            break;
                        default:
                            break;
                    }
                }
            }

            so.close();
        } catch (IOException | ClassNotFoundException e) {
            VIDEO.print("Errore durante la comunicazione con il Client: " + e.getMessage() + "\n: ");
            VIDEO.flush();
            threadServerSocket.rimuoviClient(emailContatto);
            terminaSocket();
        }
    }

    private boolean inviaMessaggio(TipoMessaggio tipo, String testo) { // 2
        try {
            Messaggio messaggio = new Messaggio(tipo, "Server di Log", emailContatto, testo);
            uscita.writeObject(messaggio);
        } catch (IOException e) {
            VIDEO.print("Errore durante l'invio del messaggio al Client: " + e.getMessage() + "\n: ");
            VIDEO.flush();
            return false;
        }

        return true;
    }

    public void disconnetti() { // 3
        try {
            if (so != null) {
                stop();

                if (inviaMessaggio(TipoMessaggio.SERVER_LOG_ARRESTATO, "Server Fermato")) {
                    ingresso.close();
                    uscita.close();
                    so.close();
                }
            }
        } catch (IOException e) {
            VIDEO.print("Errore durante la disconnessione del Client: " + e.getMessage() + "\n: ");
            VIDEO.flush();
        }
    }

    private void terminaSocket() { // 4
        try {
            if (so != null) {
                stop();
                ingresso.close();
                uscita.close();
                so.close();
            }
        } catch (IOException e) {
        }
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
 */
