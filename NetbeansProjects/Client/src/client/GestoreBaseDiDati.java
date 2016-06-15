/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import messaggio.Messaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class GestoreBaseDiDati {

    private final String emailConnessione;
    private boolean connessoAlDatabase;

    private final String indirizzoIPDatabase;
    private final int portaDatabase;
    private final String usernameDatabase;
    private final String passwordDatabase;

    GestoreBaseDiDati() { // 0
        indirizzoIPDatabase = GestoreParametriConfigurazioneXML.parametri.indirizzoIPDatabase;
        portaDatabase = GestoreParametriConfigurazioneXML.parametri.portaDatabase;
        usernameDatabase = GestoreParametriConfigurazioneXML.parametri.usernameDatabase;
        passwordDatabase = GestoreParametriConfigurazioneXML.parametri.passwordDatabase;

        emailConnessione = FrameContatti.emailConnessione;
    }

    private String ottieniIndirizzoIP() throws SocketException { // 1
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

    public void registraInDB() throws SQLException, SocketException { // 2
        Connection co = DriverManager.getConnection("jdbc:mysql://" + indirizzoIPDatabase + ":" + portaDatabase + "/c0programmazione_java", usernameDatabase, passwordDatabase);
        Statement st = co.createStatement();
        String query = "INSERT INTO `contatti` (`email`, `indirizzo_ip`, `data`) VALUES (\"" + emailConnessione + "\", \"" + ottieniIndirizzoIP() + "\", CURRENT_TIMESTAMP);";
        st.executeUpdate(query);

        query = "INSERT INTO `storico` (email, durata) "
                + "SELECT * FROM (SELECT \"" + emailConnessione + "\", 0.0) AS tmp "
                + "WHERE NOT EXISTS (SELECT email "
                + "                  FROM `storico` "
                + "                  WHERE email = \"" + emailConnessione + "\" "
                + "                 ) LIMIT 1; ";
        st.executeUpdate(query);

        st.close();
        co.close();
        
        connessoAlDatabase = true;
    }

    public void aggiornaStorico() throws SQLException { // 3
        Connection co = DriverManager.getConnection("jdbc:mysql://" + indirizzoIPDatabase + ":" + portaDatabase + "/c0programmazione_java", usernameDatabase, passwordDatabase);
        Statement st = co.createStatement();

        st.executeUpdate("UPDATE `storico` "
                + "SET durata = (SELECT (TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, t_derivata.data_connessione)) / 3600) as 'ore_connessione' "
                + "              FROM (SELECT `data` AS data_connessione "
                + "                    FROM `contatti` "
                + "                    WHERE email = \"" + emailConnessione + "\") AS t_derivata) "
                + "              WHERE email = \"" + emailConnessione + "\" ");
        st.close();
        co.close();
    }

    public void rimuoviDaDB() throws SQLException { // 4
        aggiornaStorico();

        Connection co = DriverManager.getConnection("jdbc:mysql://" + indirizzoIPDatabase + ":" + portaDatabase + "/c0programmazione_java", usernameDatabase, passwordDatabase);
        Statement st = co.createStatement();
        String query = "DELETE FROM `contatti` WHERE email = \"" + emailConnessione + "\";";
        st.executeUpdate(query);
        st.close();
        co.close();
        
        connessoAlDatabase = false;
    }

    public ObservableList<Contatto> ottieniListaContattiConnessi() throws SQLException { // 5
        ObservableList<Contatto> contattiConnessi = FXCollections.observableArrayList();
        Connection co = DriverManager.getConnection("jdbc:mysql://" + indirizzoIPDatabase + ":" + portaDatabase + "/c0programmazione_java", usernameDatabase, passwordDatabase);
        Statement st = co.createStatement();
        String query = "SELECT * FROM `contatti`";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            if (!(rs.getString("email").equals(emailConnessione))) {
                contattiConnessi.add(new Contatto(rs.getString("email"), rs.getString("indirizzo_ip"), rs.getString("data")));
            }
        }
        st.close();
        co.close();

        return contattiConnessi;
    }

    public void inserisciMessaggioInDB(Messaggio m) throws SQLException { // 6
        Connection co = DriverManager.getConnection("jdbc:mysql://" + indirizzoIPDatabase + ":" + portaDatabase + "/c0programmazione_java", usernameDatabase, passwordDatabase);
        Statement st = co.createStatement();

        String query = "INSERT INTO `messaggi` (`mittente`, `destinatario`, `testo`, `data`) VALUES (\"" + m.getMittente() + "\", \"" + m.getDestinatario() + "\", \"" + m.getTesto() + "\", CURRENT_TIMESTAMP);";
        st.executeUpdate(query);
        st.close();
        co.close();
        connessoAlDatabase = true;
    }

    public ObservableList<Storico> ottieniStatistiche() throws SQLException { // 7
        ObservableList<Storico> statistiche = FXCollections.observableArrayList();
        Connection co = DriverManager.getConnection("jdbc:mysql://" + indirizzoIPDatabase + ":" + portaDatabase + "/c0programmazione_java", usernameDatabase, passwordDatabase);
        Statement st = co.createStatement();
        String query = "SELECT * FROM `storico` ORDER BY `durata` DESC LIMIT 5;";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            statistiche.add(new Storico(rs.getString("email"), rs.getDouble("durata")));
        }
        st.close();
        co.close();

        return statistiche;
    }

    public boolean connesso() { // 8
        return connessoAlDatabase;
    }
}

/*
Note:
(0) Costruttore GestoreBaseDiDati().
    Inizializza le variabili della classe impostando indirizzo ip, porta,
    nome utente e password da utilizzare per la connessione alla base di dati.

(1) Funzione ottieniIndirizzoIP().
    Ottiene l'indirizzo IP associato alla prima interfaccia di rete attiva. Si 
    puo' cosi' ottenere l'indirizzo IP associato al computer dal router che 
    gestisce la rete e permettere di eseguire l'applicativo su piu' computer
    della rete per lo scambio di messaggi.

(2) Funzione registraInDB().
    Esegue la query che registrano il Client nella lista dei contatti connessi
    nella tabella "contatti" del database.

(3) Funzione aggiornaStorico().
    Aggiorna il numero di ore che un Client ha speso connesso al servizio di
    messaggistica aggiornando il campo durata della tabella "storico" della
    base di dati. Viene eseguito prima di rimuovere il Client appena prima che
    si disconnetta dal servizio di messaggistica.

(4) Funzione rimuoviDaDB().
    Rimuove il Client identificandolo con il suo indirizzo email dalla tabella
    "contatti" della base di dati.

(5) Funzione ottieniListaContattiConnessi().
    Ottiene il contenuto della tabella "contatti" della base di dati.

(6) Funzione inserisciMessaggioInDB().
    Salva il messaggio inviato ad un altro Client nella tabella "messaggi" della
    base di dati.

(7) Funzione ottieniStatistiche().
    Ottiene il contenuto della tabella "storico" della base di dati.

(8) Funzione connesso().
    Restitusice lo stato di connessione alla base di Dati.
 */
