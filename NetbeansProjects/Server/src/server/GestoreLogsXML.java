/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import messaggio.Messaggio;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class GestoreLogsXML {

    private final String pathFileTXTLogs;
    private final String pathFileXSDLogs;

    GestoreLogsXML() throws IOException { // 0
        pathFileTXTLogs = GestoreParametriConfigurazioneXML.parametri.pathFileDiLog;
        pathFileXSDLogs = GestoreParametriConfigurazioneXML.parametri.pathFileXSDLogs;

        File fileDiLog = new File(pathFileTXTLogs);
        if (!fileDiLog.exists()) {
            fileDiLog.createNewFile();
        }
    }

    synchronized public boolean registraLogXML(Messaggio m) throws IOException, ParserConfigurationException, SAXException { // 1
        if (validaLogXML(m.getTesto())) {
            PrintWriter scrittoreFile = new PrintWriter(new BufferedWriter(new FileWriter(pathFileTXTLogs, true))); // 4
            scrittoreFile.append(m.getTesto() + "\n");
            scrittoreFile.flush();
            return true;
        }

        return false;
    }

    synchronized public boolean validaLogXML(String logXML) throws IOException, ParserConfigurationException, SAXException { // 2
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Document d = db.parse(new InputSource(new StringReader(logXML)));
        Schema s = sf.newSchema(new StreamSource(new File(pathFileXSDLogs)));
        s.newValidator().validate(new DOMSource(d));

        return true;
    }

    synchronized public String leggiLogs() throws FileNotFoundException, IOException { // 3
        String logsLetti = "\n";
        String linea;

        FileReader fileReader = new FileReader(pathFileTXTLogs);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((linea = bufferedReader.readLine()) != null) {
            logsLetti += linea + "\n";
        }
        bufferedReader.close();

        return logsLetti;
    }
}

/*
Note:
(0) Costruttore GestoreLogsXML().
    Imposta i campi pathFileTXTLogs e pathFileXSDLogs uguali ai parametri di
    configurazione letti dalla classe GestoreParametriConfigurazioneXML.

(1) Funzione registraLogXML().
    Riceve come parametri un oggetto di tipo Messaggio il quale campo "testo"
    contiene il testo del singolo log da registrare.
    Dopo aver chiamato la funzione validaLogXML() che valida il singolo log
    xml ricevuto, registra il singolo log nel file di log (.txt) aperto locale.

(2) Funzione validaLogXML().
    Valida il singolo log XML ricevuto tramite messaggio di tipo
    ESSAGGIO_DI_LOG e restituisce TRUE nel caso sia possibile procedere
    alla scrittura del log nel file di log aperto.

(3) Funzione getLogs().
    Legge il file di log (.txt) aperto locale e restituisce una stringa
    contenente tutti i logs presenti in tale file.

(4) https://docs.oracle.com/javase/7/docs/api/java/io/BufferedWriter.html
 */
