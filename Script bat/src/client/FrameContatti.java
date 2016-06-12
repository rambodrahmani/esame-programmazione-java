/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.*;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import messaggio.TipoMessaggioDiLog;
import messaggio.Messaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class FrameContatti extends JFrame implements ActionListener, MenuListener {

    public static GestoreBaseDiDati gestoreBaseDiDati;
    public static SocketServerDiLog serverDiLog;
    public static ServerSocketConversazioni serverConversazioni;
    public static String emailConnessione;
    private static boolean statoConnessione;
    private static ObservableList<Contatto> contattiConnessi;
    private static ConcurrentHashMap<String, FrameConversazione> conversazioniAperte;
    private final Timer timerListaContatti;

    // Componenti del frame Contatti
    private JFrame frameContatti;
    private static Container contentPaneContatti;
    private JMenuBar barraMenu;
    private JMenu menuStrumenti;
    private JMenuItem menuItemStatistiche;
    private JPanel panelContenitore;
    private JPanel groupBoxPanel1;
    private TitledBorder titoloGroupBox1;
    private JPanel groupBoxPanel2;
    private TitledBorder titoloGroupBox2;
    private JScrollPane scrollPaneTabellaContatti;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JLabel lblEmail;
    private static JLabel lblStato;
    private static JTextField txtEmail;
    private static JButton btnConnetti;
    private JTable tabellaContatti;
    private ModelloTabellaContatti modelloTabellaContatti;

    FrameContatti() { // 0
        statoConnessione = false;

        timerListaContatti = new Timer();
        
        serverDiLog = new SocketServerDiLog();
        
        contattiConnessi = FXCollections.observableArrayList();
        conversazioniAperte = new ConcurrentHashMap<>();

        allestisciFrameContatti();

        try {
            serverConversazioni = new ServerSocketConversazioni();
            serverConversazioni.start();
        } catch (IOException ex) {
            mostraMessaggioErrore("Errore Avvio Server Conversazioni", "Errore durante l'avvio del Server per le Conversazioni con i contatti: " + ex.getMessage());
        }
    }

    private void allestisciFrameContatti() { // 1
        allestisciMenuFrameContatti();
        frameContatti = new JFrame("Chat Client");
        ImageIcon img = new ImageIcon("client.png");
        frameContatti.setIconImage(img.getImage());
        allestisciContentPaneContatti();
        frameContatti.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frameContatti.setResizable(false);
        frameContatti.pack();
        frameContatti.setLocationRelativeTo(null);

        frameContatti.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(contentPaneContatti, "Sei sicuro di voler chiudere il Client?", "Chiusura Client", JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    disconnettiDalServizioDiMessaggistica();
                    frameContatti.dispose();
                    System.exit(0);
                }
            }
        });

        frameContatti.setVisible(true);
    }

    private void allestisciMenuFrameContatti() { // 1.1
        barraMenu = new JMenuBar();
        menuStrumenti = new JMenu("Strumenti");
        menuStrumenti.addMenuListener((MenuListener) this);
        menuItemStatistiche = new JMenuItem("Statistiche");
        menuItemStatistiche.addActionListener(this);
        menuStrumenti.add(menuItemStatistiche);
        barraMenu.add(menuStrumenti);
    }

    private void allestisciPanel1() { // 1.2
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(10, 5));
        panel1.setPreferredSize(new Dimension(350, 90));
        panel1.setBorder(new EmptyBorder(5, 5, 0, 5));
        lblEmail = new JLabel("Email:");
        panel1.add(lblEmail, BorderLayout.WEST);
        txtEmail = new JTextField(18);
        panel1.add(txtEmail, BorderLayout.CENTER);
        allestisciPanel2();
        panel1.add(panel2, BorderLayout.SOUTH);
    }

    private void allestisciPanel2() { // 1.3
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(10, 5));
        panel2.setPreferredSize(new Dimension(350, 32));
        panel2.setBorder(new EmptyBorder(0, 0, 5, 0));
        btnConnetti = new JButton("Connetti");
        btnConnetti.addActionListener(this);
        panel2.add(btnConnetti, BorderLayout.EAST);
    }

    private void allestisciGroupBox1() { // 1.4
        titoloGroupBox1 = BorderFactory.createTitledBorder("Connessione al Server");
        groupBoxPanel1 = new JPanel();
        groupBoxPanel1.setLayout(new BorderLayout(10, 5));
        groupBoxPanel1.setPreferredSize(new Dimension(350, 95));
        groupBoxPanel1.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 0, 0, 0), titoloGroupBox1));
        allestisciPanel1();
        groupBoxPanel1.add(panel1, BorderLayout.CENTER);
    }

    private void allestisciTabellaContatti() { // 1.5
        modelloTabellaContatti = new ModelloTabellaContatti();
        tabellaContatti = new JTable(modelloTabellaContatti);
        tabellaContatti.setPreferredScrollableViewportSize(new Dimension(350, 450));
        tabellaContatti.setFillsViewportHeight(true);
        tabellaContatti.setRowHeight(25);
        tabellaContatti.getTableHeader().setPreferredSize(new Dimension(30, 30));
        ((DefaultTableCellRenderer) tabellaContatti.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        tabellaContatti.setIntercellSpacing(new Dimension(5, 5));
        tabellaContatti.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    serverDiLog.inviaMessaggioLog(TipoMessaggioDiLog.DOPPIO_CLICK_LISTA_CONTATTI);
                    creaNuovaConversazione(tabellaContatti.getValueAt(row, 0).toString(), null);
                }
            }
        });
        scrollPaneTabellaContatti = new JScrollPane(tabellaContatti);
    }

    private void allestisciPanel3() { // 1.6
        panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(10, 5));
        panel3.setPreferredSize(new Dimension(350, 450));
        panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
        allestisciTabellaContatti();
        panel3.add(scrollPaneTabellaContatti, BorderLayout.CENTER);
    }

    private void allestisciGroupBox2() { // 1.7
        titoloGroupBox2 = BorderFactory.createTitledBorder("Contatti");
        groupBoxPanel2 = new JPanel();
        groupBoxPanel2.setLayout(new BorderLayout(10, 5));
        groupBoxPanel2.setPreferredSize(new Dimension(350, 450));
        groupBoxPanel2.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), titoloGroupBox2));
        allestisciPanel3();
        groupBoxPanel2.add(panel3, BorderLayout.CENTER);
    }

    private void allestisciPanel4() { // 1.8
        panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(10, 5));
        panel4.setPreferredSize(new Dimension(350, 20));
        panel4.setBorder(new EmptyBorder(2, 5, 0, 0));
        lblStato = new JLabel("Stato: Non connesso.");
        lblStato.setFont(new Font(lblStato.getFont().toString(), Font.PLAIN, 11));
        panel4.add(lblStato, BorderLayout.NORTH);
    }

    private void allestisciPanelContenitore() { // 1.9
        panelContenitore = new JPanel();
        panelContenitore.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        panelContenitore.setLayout(new BorderLayout());
        frameContatti.setJMenuBar(barraMenu);
        allestisciGroupBox1();
        panelContenitore.add(groupBoxPanel1, BorderLayout.NORTH);
        allestisciGroupBox2();
        panelContenitore.add(groupBoxPanel2, BorderLayout.CENTER);
        allestisciPanel4();
        panelContenitore.add(panel4, BorderLayout.SOUTH);
    }

    private void allestisciContentPaneContatti() { // 1.10
        contentPaneContatti = frameContatti.getContentPane();
        contentPaneContatti.setLayout(new BorderLayout(10, 5));
        allestisciPanelContenitore();
        contentPaneContatti.add(panelContenitore, BorderLayout.SOUTH);
    }

    @Override
    public void menuSelected(MenuEvent e) { // 2.1
        if (statoConnessione == true) {
            if (e.getSource() == menuStrumenti) {
                serverDiLog.inviaMessaggioLog(TipoMessaggioDiLog.CLICK_MENU_STRUMENTI);
            }
        }
    }

    @Override
    public void menuDeselected(MenuEvent e) { // 2.1
    }

    @Override
    public void menuCanceled(MenuEvent e) { // 2.1
    }

    @Override
    public void actionPerformed(ActionEvent e) { // 2.2
        if (e.getSource() == menuItemStatistiche) {
            if (statoConnessione == true) {
                apriFrameStatistiche();
                serverDiLog.inviaMessaggioLog(TipoMessaggioDiLog.CLICK_MENU_STATISTICHE);
            } else {
                mostraMessaggioErrore("Non Connesso al Servizio di Messaggistica", "Devi connetterti al servizio di messaggistica prima di poter accedere alle statistiche.");
            }
        } else if (e.getSource() == btnConnetti) {
            connettiAlServizioDiMessaggistica();
        }
    }

    private void apriFrameStatistiche() { // 3
        FrameStatistiche frameStatistiche = new FrameStatistiche();
    }

    private void connettiAlServizioDiMessaggistica() { // 4
        if (statoConnessione == true) {
            serverDiLog.inviaMessaggioLog(TipoMessaggioDiLog.CLICK_PULSANTE_DISCONNETTI);
            disconnettiDalServizioDiMessaggistica();
            return;
        }

        emailConnessione = txtEmail.getText();
        if (!(emailConnessione.length() > 0)) {
            mostraMessaggioErrore("Errore Connessione", "Inserisci l'Email da utilizzare per connettersi.");
            return;
        }

        if (!(validaIndirizzoEmail(emailConnessione))) {
            mostraMessaggioErrore("Errore Connessione", "Indirizzo Email inserito non valido.");
            return;
        }

        if (connettiAlServerDiLog()) {
            gestoreBaseDiDati = new GestoreBaseDiDati();
            connettiAlDatabase();
            serverDiLog.inviaMessaggioLog(TipoMessaggioDiLog.CLICK_PULSANTE_CONNETTI);
        }
    }

    private void disconnettiDalServizioDiMessaggistica() { // 5
        if (statoConnessione == true) {
            disconnettiDalServerDiLog();
            disconnettiDalDatabase();
            impostaStatoConnessione(false, "Stato: Non connesso.");
        }
    }

    private boolean connettiAlServerDiLog() { // 6
        if (!(serverDiLog.connesso())) {
            try {
                serverDiLog.connetti();
            } catch (IOException ex) {
                mostraMessaggioErrore("Errore Connessione Server di Log", "Errore durante la connessione con il Server di Log: " + ex.getMessage());
                return false;
            }
            return true;
        }

        return false;
    }

    private void disconnettiDalServerDiLog() { // 7
        if (serverDiLog.connesso()) {
            try {
                serverDiLog.disconnetti();
            } catch (IOException ex) {
                mostraMessaggioErrore("Errore Comunicazione con Server di Log", "Errore durante la comunicazione con il Server di Log: " + ex.getMessage() + ".");
            }
        }
    }

    private void connettiAlDatabase() { // 8
        if (!(gestoreBaseDiDati.connesso())) {
            try {
                gestoreBaseDiDati.registraInDB();
                aggiornaTabellaContatti();
                avviaTimerAggiornamento();
            } catch (SQLException | SocketException ex) {
                mostraMessaggioErrore("Errore Connessione Database", "Errore durante la connessione con il Database: " + ex.getMessage());
            }
        }
    }

    private void disconnettiDalDatabase() { // 9
        if (gestoreBaseDiDati.connesso()) {
            try {
                gestoreBaseDiDati.rimuoviDaDB();
                svuotaTabellaContatti();
                timerListaContatti.cancel();
            } catch (SQLException ex) {
                mostraMessaggioErrore("Errore Disconnessione Database", "Errore durante la disconnessione con il Database: " + ex.getMessage());
            }
        }
    }

    public static void impostaStatoConnessione(boolean status, String text) { // 10
        statoConnessione = status;
        lblStato.setText(text);

        if (statoConnessione == true) {
            txtEmail.setEditable(false);
            btnConnetti.setText("Disconnetti");
        } else {
            txtEmail.setEditable(true);
            btnConnetti.setText("Connetti");
        }

        contentPaneContatti.repaint();
    }

    private void avviaTimerAggiornamento() { // 11
        try {
            timerListaContatti.scheduleAtFixedRate(taskListaContatti, 2000, 2000);
        } catch(IllegalStateException e) {
        }
    }

    private TimerTask taskListaContatti = new TimerTask() { // 12
        @Override
        public void run() {
            if (gestoreBaseDiDati.connesso()) {
                aggiornaTabellaContatti();
            }
        }
    };

    private void aggiornaTabellaContatti() { // 13
        try {
            contattiConnessi.clear();
            contattiConnessi = gestoreBaseDiDati.ottieniListaContattiConnessi();
            modelloTabellaContatti.aggiornaContatti(contattiConnessi);
            contentPaneContatti.repaint();
        } catch (SQLException ex) {
            mostraMessaggioErrore("Errore Aggiornamento Lista Contatti", "Errore durante l'aggiornamento della lista dei contatti: " + ex.getMessage());
        }
    }

    private void svuotaTabellaContatti() { // 14
        contattiConnessi.clear();
        modelloTabellaContatti.aggiornaContatti(contattiConnessi);
    }

    public static void gestisciNuovoMessaggioRicevuto(Messaggio m) { // 15
        creaNuovaConversazione(m.getMittente(), m);
    }

    private static void creaNuovaConversazione(String emailMittente, Messaggio m) { // 16
        Iterator iteratoreConversazioni = conversazioniAperte.keySet().iterator();

        while (iteratoreConversazioni.hasNext()) {
            String chiave = (String) iteratoreConversazioni.next();
            if (chiave.equals(emailMittente)) {
                if (m != null) {
                    FrameConversazione frameConversazioneEsistente = (FrameConversazione) conversazioniAperte.get(chiave);
                    frameConversazioneEsistente.gestisciNuovoMessaggio(m);
                } else {
                    mostraMessaggioErrore("Coversazione Aperta", "Hai gia' una conversazione in corso con questo cotatto. Termina la precedente conversazione per poterne aprire una nuova.");
                }
                return;
            }
        }

        contattiConnessi.stream().forEach((contatto) -> {
            if (contatto.getEmail().equals(emailMittente)) {
                try {
                    FrameConversazione nuovoFrameConversazione = new FrameConversazione(contatto, m);
                    conversazioniAperte.put(emailMittente, nuovoFrameConversazione);
                } catch (IOException ex) {
                    mostraMessaggioErrore("Errore avvio Conversazione", "Errore durante l'avvio della conversazione: " + ex.getMessage());
                }
            }
        });
    }

    public static void rimuoviConversazione(String emailMittente) { // 17
        contattiConnessi.stream().forEach((contatto) -> {
            if (contatto.getEmail().equals(emailMittente)) {
                conversazioniAperte.remove(emailMittente);
            }
        });
    }

    private boolean validaIndirizzoEmail(String email) { // 18
        String patternEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(patternEmail);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static void mostraMessaggioErrore(String titolo, String messaggio) { // 19
        JOptionPane.showMessageDialog(contentPaneContatti, messaggio, titolo, JOptionPane.ERROR_MESSAGE);
    }
}

/*
Note:
(0) Costruttore FrameContatti().
    Inizializza le variabili della classe FrameContatti.
    Avvia il ServerSocket in ascolto per le connessioni da parte degli altri 
    Client.
    Chiama la funzione che inizializza i vari componenti del JFrame.

(1) Funzione allestisciFrameContatti().
    Inizializza i vari componenti con cui viebe popolato il JFrame.
    Le funzioni dalla (1.1) alla (1.10) vengono chiamata a catena l'una 
    dall'altra: questo per evitare di scrivere una unica funzione troppo
    lunga.

(2.2) Funzione menuSelected().
      La classe implementa MenuListener, quindi ho dovuto implementarne i metodi
      richiesti.
      Gli eventi possibili sono:
          Click sul menuStrumenti: viene inviato un messaggio di Log al Server.
          

(2.2) Funzione actionPerformed().
      La classe implementa ActionListener, quindi ho dovuto implementare questo
      metodo.
      Gli eventi possibile sono:
          Click sul menuStrumenti: viene inviato un messaggio di Log al Server.
          
          Click sul menuItemStatistiche: viene inviato un messaggio di Log al 
          Server e viene aperto il Frame Statistiche.
          
          Click sul button btnConnetti: il Client si connette al Servizio di 
          Messaggistica.

(3) Funzione apriFrameStatistiche().
    Inizializza una nuova istanza della classe FrameStatistiche.

(4) Funzione connettiAlServizioDiMessaggistica().
    Controlla che l'indirizzo email inserito dall'utente sia corretto, connette
    il Client al Server di Log e lo registra nella base di dati nella Tabella 
    "contatti".

(5) Funzione disconnettiDalServizioDiMessaggistica().
    Disconnette il Client dal Server di Log e lo rimuove dalla base di dati.

(6) Funzione connettiAlServerDiLog().
    Connette il Client al Server di Log utilizzando la classe SocketServerDiLog.
    Funzione chiamata da connettiAlServizioDiMessaggistica().

(7) Funzione disconnettiDalServerDiLog().
    Disconnette il Client dal Server di Log utilizzando la classe 
    SocketServerDiLog.
    Funzione chiamata da disconnettiDalServizioDiMessaggistica().

(8) Funzione connettiAlDatabase().
    Connette il Client al Database di Log utilizzando la classe 
    GestoreBaseDiDati.
    Funzione chiamata da connettiAlServizioDiMessaggistica().

(9) Funzione disconnettiDalDatabase().
    Disconnette il Client al Database di Log utilizzando la classe 
    GestoreBaseDiDati.
    Funzione chiamata da disconnettiDalServizioDiMessaggistica().

(10) Funzione impostaStatoConnessione().
     Funzione di utilita' utilizzata per impostare il testo della JLabel 
     lblStato, che indica lo stato di connessione al Servizio di Messaggistica.

(11) Funzione avviaTimerAggiornamento().
     Avvia il Timer che a intervalli di 2 secondi utilizza la classe 
     GestoreBaseDiDati per aggiornare la lista dei contatti connessi.

(12) TimerTask taskListaContatti().
     TimerTask eseguito dal Timer di aggiornamento della lista dei contatti.

(13) Funzione aggiornaTabellaContatti().
     Utilizza la classe GestoreBaseDiDati per ottenere la nuova lista dei 
     contatti connessi a intervalli di 2 secondi, come impostato nel Timer
     di aggiornamento.

(14) Funzione svuotaTabellaContatti().
     Utilizzato al momento della disconnessione dal Servizio di Messaggistica,
     per svuotare il contenuto della Tabella contatti.

(15) Funzione gestisciNuovoMessaggioRicevuto().
     Quando il SocketConversazione associato a un contatto riceve un messaggio,
     chiama questa funzione che si occupa di smistarlo.

(16) Funzione creaNuovaConversazione().
     Crea un nuovo JFrame conversazione istanziando un nuovo oggetto 
     FrameConversazione.
     La funzione controlla la presenza di una conversazione in corso con tale 
     contatto, oppure se vi e' la necessita' di avviare una nuova finestra
     di conversazione.

(17) Funzione rimuoviConversazione().
     Quando l'utente chiude una finestra di conversazione, questa viene rimossa
     dalla lista delle conversazioni aperte.

(17) Funzione validaIndirizzoEmail().
     Utilizza le espressioni regex per validare l'indirizzo email inserito
     dall'utente per connettersi al servizio di messaggistica.

(18) Funzione mostraMessaggioErrore().
     Funzione di utilita' utilizzata per mostrare una MessageDialog di tipo
     ERROR_MESSAGE, utilizzata al verificarsi di errori durante l'utilizzo.
 */
