/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import messaggio.Messaggio;
import messaggio.TipoMessaggio;
import messaggio.MessaggioDiLog;
import messaggio.TipoMessaggioDiLog;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class SocketServerDiLog extends Thread {

    private Socket so;
    private ObjectOutputStream uscita;
    private ObjectInputStream ingresso;
    private String emailConnessione;
    private boolean connessoAlServerDiLog;

    private boolean threadSospeso;

    SocketServerDiLog() { // 0
        connessoAlServerDiLog = false;
        threadSospeso = true;

        start();
    }

    public void connetti() throws IOException { // 1
        emailConnessione = FrameContatti.emailConnessione;
        so = new Socket(GestoreParametriConfigurazioneXML.parametri.indirizzoIPServerDiLog, GestoreParametriConfigurazioneXML.parametri.portaServerDiLog);
        uscita = new ObjectOutputStream(so.getOutputStream());
        ingresso = new ObjectInputStream(so.getInputStream());

        connessoAlServerDiLog = true;

        if (inviaMessaggio(TipoMessaggio.IDENTIFICAZIONE_CLIENT, emailConnessione)) {
            FrameContatti.impostaStatoConnessione(true, "Stato: connesso.");
            riprendi();
        }
    }

    public boolean connesso() { // 2
        return connessoAlServerDiLog;
    }

    @Override
    public void run() { // 3
        try {
            Messaggio messaggioRicevuto;

            while (true) {
                synchronized (this) {
                    while (threadSospeso) {
                        wait();
                    }
                }
                messaggioRicevuto = (Messaggio) ingresso.readObject();

                switch (messaggioRicevuto.getTipo()) {
                    case IDENTIFICAZIONE_CLIENT_FALLITA:
                        FrameContatti.mostraMessaggioErrore("Errore Identificazione", "Identificazione con il Server fallita. Indirizzo Email già in uso. Disconnesso.");
                        FrameContatti.impostaStatoConnessione(false, "Stato: disconnesso.");
                        sospendi();
                        break;
                    case SERVER_LOG_ARRESTATO:
                        FrameContatti.impostaStatoConnessione(true, "Stato: connesso, Server di Log arrestato.");
                        FrameContatti.mostraMessaggioErrore("Errore Server di Log", "Server di Log Arrestato. Disconnesso dal Server di Log.");
                        terminaSocketServerDiLog();
                        break;
                    default:
                        break;
                }
            }
        } catch (InterruptedException | IOException | ClassNotFoundException ex) {
            if (!threadSospeso) {
                FrameContatti.mostraMessaggioErrore("Errore Comunicazione con Server di Log", "Errore durante la comunicazione con il Server di Log: " + ex.getMessage() + ".");
            }
        }
    }

    private boolean inviaMessaggio(TipoMessaggio tipo, String text) { // 4
        try {
            if (connessoAlServerDiLog) {
                Messaggio message = new Messaggio(tipo, emailConnessione, "Server di Log", text);
                uscita.writeObject(message);
            }
        } catch (IOException ex) {
            FrameContatti.mostraMessaggioErrore("Errore Comunicazione con Server di Log", "Errore durante la comunicazione con il Server di Log: " + ex.getMessage() + ".");
            return false;
        }

        return true;
    }

    public void inviaMessaggioLog(TipoMessaggioDiLog tipoLog) { // 5
        MessaggioDiLog ml = new MessaggioDiLog(tipoLog, emailConnessione);
        inviaMessaggio(TipoMessaggio.MESSAGGIO_LOG, serializzaXML(ml));
    }

    private String serializzaXML(MessaggioDiLog ml) { // 6
        XStream xs = new XStream();
        String stringaMessaggioLog = xs.toXML(ml);

        return stringaMessaggioLog;
    }

    public void disconnetti() throws IOException { // 7
        sospendi();
        if (inviaMessaggio(TipoMessaggio.CLIENT_DISCONNESSO, emailConnessione)) {
            connessoAlServerDiLog = false;
            uscita.close();
            ingresso.close();
            so.close();
        }
    }

    private void terminaSocketServerDiLog() { // 8
        sospendi();
        connessoAlServerDiLog = false;
        try {
            uscita.close();
            ingresso.close();
            so.close();
        } catch (IOException e) {
        }
    }

    public void sospendi() { // 9
        threadSospeso = true;
    }

    synchronized void riprendi() { // 10
        threadSospeso = false;
        notify();
    }
}

/*
Note:
(0) Costruttore SocketServerDiLog().
    Inizializza le varibili impostante l'indirizzo IP e la porta del Server di 
    Log a cui connettersi.

(1) Funzione connetti().
    Inizializza Socket, ObjectOutputStream e ObjectInputStream per il canale
    di comunicazione con il Server di Log. Invia al Server di Log il messaggio
    di tipo IDENTIFICAZIONE_CLIENT.

(2) Funzione connesso().
    Restituisce lo stato di connession al Server di Log.

(3) Funzione run().
    Rimane in attesa di ricezione di messaggi dal Server di Log che possono 
    essere di tipo:
        IDENTIFICAZIONE_CLIENT_FALLITA: non e' possibile connettersi al Server 
        di Log per un altro Client con lo stesso indirizzo email si trova 
        connesso ad esso.
        SERVER_LOG_ARRESTATO: comunica al Client l'arresto del Server di Log.

(4) Funzione inviaMessaggio().
    Riceve come argomenti il tipo e il testo del messaggio da inviare al Server 
    di Log.

(5) Funzione inviaMessaggioLog().
    Serializza l'oggetto della classe MessaggioDiLog che li viene passatto
    e utilizza la funzione inviaMessaggio per inviare il log xml al
    Server di Log.

(6) Funzione serializzaXML().
    Riceve come argomenti un oggetto della classe MessaggioDiLog, lo serializza
    in xml e restituisce la stringa che ne deriva.

(7) Funzione disconnetti().
    Disconnette il Client dal Server di Log inviando a questo un messaggio di 
    tipo CLIENT_DISCONNESSO per notificarlo della disconnessione.

(8) Funzione terminaSocketServerDiLog().
    Utilizzato in caso di errori durante la comunicazione con il Server di Log,
    serve a chiudere Socket, ObjectOutputStream e ObjectInputStream per il
    canale di comunicazione con il Server di Log.

(9) Funzione sospendi().
    Funzione di gestione del Thread. Sospende la ricezione dei messaggi per 
    il socket in ascolto connesso al Server di Log.

(10) Funzione riprendi().
     Funzione di gestione del Thread. Riprende la ricezione dei messaggi per
     il socket in ascolto connesso al Server di Log.
 */
