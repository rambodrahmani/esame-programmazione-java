/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import messaggio.Messaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class SocketConversazione extends Thread {

    private final Socket so;
    private final ObjectInputStream ingresso;

    SocketConversazione(Socket s) throws IOException { // 0
        so = s;
        ingresso = new ObjectInputStream(so.getInputStream());
    }

    @Override
    public void run() { // 1
        Messaggio messaggioRicevuto;
        try {
            while (true) {
                messaggioRicevuto = (Messaggio) ingresso.readObject();

                switch (messaggioRicevuto.getTipo()) {
                    case MESSAGGIO_NORMALE:
                        FrameContatti.gestisciNuovoMessaggioRicevuto(messaggioRicevuto);
                        break;
                    case MESSAGGIO_HEARTBEAT:
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            terminaSocket();
        }
    }

    void terminaSocket() { // 3
        try {
            if (so != null) {
                ingresso.close();
                so.close();
            }
        } catch (IOException e) {
        }
    }
}

/*
Note:
(0) Costruttore SocketConversazione().
    Inizializza Socket, ObjectOutputStream e ObjectInputStream per il canale di
    comunicazione con il Client che si connette per iniziare una conversazione.

(1) Funzione run().
    Rimane in attesa dei messaggi di tipo MESSAGGIO_NORMALE che invia il client 
    con cui e' stata avviata la conversazione.

(2) Funzione inviaMessaggio().
    Riceve come argomenti il tipo e il testo del messaggio da inviare al Client
    con si e' stata instaurata la comunicazione.

(3) Funzione terminaSocket().
    Termina il canale di comunicazione eliminando Socket, ObjectOutputStream e
    ObjectInputStream per il canale di comunicazione con il Client che si 
    connette per iniziare una conversazione.
 */
