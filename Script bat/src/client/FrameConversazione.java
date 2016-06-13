/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import static client.FrameContatti.gestoreBaseDiDati;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import messaggio.Messaggio;
import messaggio.TipoMessaggio;
import messaggio.TipoMessaggioDiLog;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JTextArea;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class FrameConversazione extends JFrame implements ActionListener {

    private Socket so;
    private ObjectOutputStream uscita;

    private final GestoreCacheConversazioni gestoreCacheConv;
    private final Contatto destinatario;
    private List<String> messaggiConversazione;
    private final Timer timerHeartBeat;

    // Componenti frame conversazione.
    private JFrame frameConversazione;
    private Container contentPaneConversazione;
    private JPanel panel1;
    private JPanel panel2;
    private JEditorPane txtAConversazione;
    private JTextArea txtAMessaggio;
    private JScrollPane scrollPaneMessaggio;
    private JButton btnInviaMessaggio;
    private JScrollPane scrollPConversazione;

    FrameConversazione(Contatto contatto, Messaggio messaggioIniziale) throws IOException { // 0
        destinatario = contatto;

        messaggiConversazione = new ArrayList<>();

        gestoreCacheConv = new GestoreCacheConversazioni(destinatario);
        messaggiConversazione = gestoreCacheConv.prelevaConversazione();

        if (messaggioIniziale != null) {
            messaggiConversazione.add("<b>" + messaggioIniziale.getMittente() + ":</b><br>");
            messaggiConversazione.add(messaggioIniziale.getTesto() + "<br><br>");
        }

        timerHeartBeat = new Timer();

        allestisciFrameConversazione();

        connettiAlServerConversazione();
    }

    private void allestisciFrameConversazione() { // 1
        frameConversazione = new JFrame("Conversazione con " + destinatario.getEmail());
        ImageIcon img = new ImageIcon("client.png");
        frameConversazione.setIconImage(img.getImage());

        contentPaneConversazione = frameConversazione.getContentPane();

        allestisciPanel1();

        frameConversazione.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameConversazione.setResizable(false);
        frameConversazione.pack();
        frameConversazione.setLocationRelativeTo(null);

        frameConversazione.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    gestoreCacheConv.salvaConversazione(ottieniTestoConversazioneCache());
                    terminaFinestraDiConversazione();
                } catch (IOException ex) {
                    mostraMessaggioErrore("Errore Salvataggio Conversazione", "Errore: impossibile salvare la conversazione: " + ex.getMessage());
                }
            }
        });

        frameConversazione.setVisible(true);

        txtAMessaggio.requestFocus();
    }

    private void allestisciPanel1() { // 1.1
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(10, 5));
        panel1.setPreferredSize(new Dimension(450, 300));
        panel1.setBorder(new EmptyBorder(10, 5, 0, 5));

        txtAConversazione = new JEditorPane("text/html", "");
        txtAConversazione.setAutoscrolls(true);
        txtAConversazione.setEditable(false);
        txtAConversazione.setText(ottieniTestoConversazione());
        scrollPConversazione = new JScrollPane(txtAConversazione);

        panel1.add(scrollPConversazione, BorderLayout.CENTER);

        contentPaneConversazione.add(panel1, BorderLayout.CENTER);

        allestisciPanel2();
    }

    private void allestisciPanel2() { // 1.2
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(10, 5));
        panel2.setPreferredSize(new Dimension(450, 45));
        panel2.setBorder(new EmptyBorder(5, 5, 5, 5));

        txtAMessaggio = new JTextArea("");
        txtAMessaggio.setEditable(true);
        txtAMessaggio.setLineWrap(true);
        txtAMessaggio.setWrapStyleWord(true);

        scrollPaneMessaggio = new JScrollPane(txtAMessaggio);

        panel2.add(scrollPaneMessaggio, BorderLayout.CENTER);

        btnInviaMessaggio = new JButton("Invia");
        btnInviaMessaggio.addActionListener(this);
        panel2.add(btnInviaMessaggio, BorderLayout.EAST);

        contentPaneConversazione.add(panel2, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // 2
        if (e.getSource() == btnInviaMessaggio) {
            if (txtAMessaggio.getText().length() > 0) {
                if (inviaMessaggio(TipoMessaggio.MESSAGGIO_NORMALE, txtAMessaggio.getText())) {
                    messaggiConversazione.add("<b>Tu:</b><br>");
                    messaggiConversazione.add(txtAMessaggio.getText() + "<br><br>");
                    txtAConversazione.setText(ottieniTestoConversazione());
                    txtAMessaggio.setText("");
                    FrameContatti.serverDiLog.inviaMessaggioLog(TipoMessaggioDiLog.CLICK_PULSANTE_INVIA);
                    txtAMessaggio.requestFocus();
                }
            }
        }
    }

    private void connettiAlServerConversazione() { // 3
        try {
            so = new Socket(destinatario.getIndirizzoIP(), GestoreParametriConfigurazioneXML.parametri.portaClient);
            uscita = new ObjectOutputStream(so.getOutputStream());
            timerHeartBeat.scheduleAtFixedRate(taskHeartBeat, 1000, 1000);
        } catch (IOException ex) {
            mostraMessaggioErrore("Errore Connessione", "Errore durante la connessione con il Contatto: " + ex.getMessage());
        }
    }

    private final TimerTask taskHeartBeat = new TimerTask() { // 4
        @Override
        public void run() {
            if (gestoreBaseDiDati.connesso()) {
                try {
                    uscita.writeObject(new Messaggio(TipoMessaggio.MESSAGGIO_HEARTBEAT, FrameContatti.emailConnessione, destinatario.getEmail(), "Messaggio di heartbeat"));
                } catch (IOException e) {
                    mostraMessaggioErrore("Contatto Disconnesso", "Il contatto " + destinatario.getEmail() + " si e' disconnesso dal servizio di messaggistica. Non sara' possibile inviare ulteriori messaggi.");
                    txtAMessaggio.setEnabled(false);
                    timerHeartBeat.cancel();
                }
            }
        }
    };

    private String ottieniTestoConversazione() { // 5
        String testoConversazione = "";
        Iterator<String> messaggiIterator = messaggiConversazione.iterator();
        while (messaggiIterator.hasNext()) {
            testoConversazione += messaggiIterator.next();
        }

        return testoConversazione;
    }

    private String ottieniTestoConversazioneCache() { // 6
        String testoConversazione = "";
        List<String> ultimiMessaggiConversazione;
        int numUltimiMessaggiCache = GestoreParametriConfigurazioneXML.parametri.numUltimiMessaggiCache;

        if (messaggiConversazione.size() > 2 * numUltimiMessaggiCache) {
            ultimiMessaggiConversazione = messaggiConversazione.subList(messaggiConversazione.size() - 2 * numUltimiMessaggiCache, messaggiConversazione.size());
        } else {
            ultimiMessaggiConversazione = messaggiConversazione.subList(0, messaggiConversazione.size());
        }
        Iterator<String> messaggiIterator = ultimiMessaggiConversazione.iterator();
        while (messaggiIterator.hasNext()) {
            testoConversazione += messaggiIterator.next() + "\n";
        }

        return testoConversazione;
    }

    public void gestisciNuovoMessaggio(Messaggio m) { // 7
        messaggiConversazione.add("<b>" + m.getMittente() + ":</b><br>");
        messaggiConversazione.add(m.getTesto() + "<br><br>");
        txtAConversazione.setText(ottieniTestoConversazione());
        txtAConversazione.setCaretPosition(txtAConversazione.getDocument().getLength());
    }

    private boolean inviaMessaggio(TipoMessaggio tipo, String testo) { // 8
        try {
            if (so.isConnected()) {
                Messaggio messaggio = new Messaggio(tipo, FrameContatti.emailConnessione, destinatario.getEmail(), testo);
                uscita.writeObject(messaggio);
                FrameContatti.gestoreBaseDiDati.inserisciMessaggioInDB(messaggio);
            } else {
                throw new IOException("Contatto disconnesso.");
            }
        } catch (IOException e) {
            mostraMessaggioErrore("Errore Invio Messaggio", "Errore durante l'invio del messaggio al Client: " + e.getMessage());
            terminaSocket();
            return false;
        } catch (SQLException se) {
            mostraMessaggioErrore("Errore Invio Messaggio", "Errore durante l'inserimento del messaggio del Database: " + se.getMessage());
        }
        return true;
    }

    private void terminaFinestraDiConversazione() { // 9
        timerHeartBeat.cancel();
        terminaSocket();
        FrameContatti.rimuoviConversazione(destinatario.getEmail());
    }

    private void terminaSocket() { // 10
        try {
            if (so != null) {
                uscita.close();
                so.close();
            }
        } catch (IOException e) {
        }
    }

    void mostraMessaggioErrore(String titolo, String messaggio) { // 11
        JOptionPane.showMessageDialog(contentPaneConversazione, messaggio, titolo, JOptionPane.ERROR_MESSAGE);
    }
}

/*
Note:
(0) Costruttore FrameConversazione().
    Riceve come argomenti l'oggetto Contatto con le informazioni relative al
    Client con cui si conversa e l'eventuale messaggio iniziale (nel caso si
    stia aprendo la finestra di conversazione per un messaggio appena ricevuto).

(1) Funzione allestisciFrameConversazione().
    Chiama a catena le funzioni 1.1 e 1.2 per inizializzare i componenti del
    JFrame della finestra di conversazione.

(2) Funzione actionPerformed().
    Rimane in ascolto per gli eventi relativi ai componenti del JFrame della 
    finestra di conversazione.
    L'unico evento che riceve e' la pressione del pulsante btnInviaMessaggio
    per l'invio del messaggio inserito nell'area di testo adiacente.

(3) Funzione connettiAlServerConversazione().
    Richiamata subito dopo l'inizializzazione dei componenti del JFrame, 
    inizializza Socket, ObjectInputStream e ObjectOutputStream per connettersi
    al ServerSocket in ascolto sulla macchina del Client con cui si conversa.

(4) TimerTask taskHeartBeat().
    TimerTask eseguito dal Timer timerHeartBeat.
    Permette di controllare a intervalli di tempo regolabili, che il contatto
    con cui si conversa sia ancora connesso al servizio di messaggistica.
    
(5) Funzione ottieniTestoConversazione().
    I messaggi scambiati nella conversazione sono salvati in una variabile di
    tipo ArrayList<>(), questa funzione restituisce questi elementi concatenati
    uno all'altro sotto forma di stringa.
    Questa stringa viene passata alla funzione setText del JEditorPane che
    imposta il testo dell'area di conversazione utilizzando i tag HTML.

(6) Funzione ottieniTestoConversazioneCache().
    Compie lo stesso lavoro della funzione ottieniTestoConversazione, con la
    differenza che restituisce solamente gli ultimi n messaggi, 
    (n = numUltimiMessaggiCache) come indicato nel file di configurazione xml.

(7) Funzione gestisciNuovoMessaggio().
    Quando il SocketConversazione riceve un messaggio, questa funzione si occupa
    di aggiungere agli elementi della ArrayList<>() contenente i messaggi
    e di aggiornare il contenuto della JEditorPane contenente la conversazione.

(8) Funzione inviaMessaggio().
    Riceve come argomento l'oggetto di tipo Messaggio da scrivere tramite
    l'ObjectOutputStream sul canale di comunicazione con l'altro Socket.

(9) Funzione terminaFinestraDiConversazione().
    Termina l'ObjectOutputStream che invia i messaggi al contatto con cui si
    conversa e rimuove la finestra di conversazione dalla lista delle
    conversazioni attive.

(10) Funzione terminaSocket().
    Termina Socket, ObjectOutputStream e ObjectInputStream per il Client con
    il quale si conversa in caso di errore.

(11) Funzione mostraMessaggioErrore().
    Funzione di utilita' utilizzata per mostrare una MessageDialog di tipo
    ERROR_MESSAGE, utilizzata al verificarsi di errori durante l'utilizzo.
 */
