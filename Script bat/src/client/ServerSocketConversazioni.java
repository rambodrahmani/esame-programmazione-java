/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class ServerSocketConversazioni extends Thread {

    private final ServerSocket serverSocket;
    
    ServerSocketConversazioni() throws IOException { // 0
        serverSocket = new ServerSocket(GestoreParametriConfigurazioneXML.parametri.portaClient);
    }

    @Override
    public void run() { // 1
        try {
            while (true) {                
                Socket nuovoSocket = serverSocket.accept();
                SocketConversazione nuovaConversazione = new SocketConversazione(nuovoSocket);
                nuovaConversazione.start();
            }
        } catch (IOException ex) {
            FrameContatti.mostraMessaggioErrore("Errore Connessione con Contatto", "Errore durante la connessione con il Contatto selezionato: " + ex.getMessage());
        }
    }
}

/*
Note:
(0) Costruttore ServerSocketConversazioni().
    Avvia il ServerSocket in ascolto per le connessioni da parte degli altri 
    Client.

(1) Funzione run().
    Funzione run() del Thread esteso dalla classe.
    Rimane in ascolto per le connessioni da parte degli altri Client.
    Ad ogni Client che si connette, associa un oggetto della classe 
    SocketConversazione.
 */
