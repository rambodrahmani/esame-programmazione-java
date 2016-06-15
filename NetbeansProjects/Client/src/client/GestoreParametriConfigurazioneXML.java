/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.thoughtworks.xstream.XStream;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */

public class GestoreParametriConfigurazioneXML {

    private final String pathFileConfigurazione;
    private final String pathFileXSDConfigurazione;
    public static ParametriConfigurazione parametri;

    public GestoreParametriConfigurazioneXML(String pathFileConfig, String pathFileXSD) {
        pathFileConfigurazione = pathFileConfig;
        pathFileXSDConfigurazione = pathFileXSD;
    }

    public void leggiFileDiConfigurazione() throws IOException, ParserConfigurationException, SAXException {
        if (validaFileDiConfigurazione()) {
            XStream xs = new XStream();
            String configurazioniLette = new String(Files.readAllBytes(Paths.get(pathFileConfigurazione)));
            parametri = (ParametriConfigurazione) xs.fromXML(configurazioniLette);
        }
    }

    public boolean validaFileDiConfigurazione() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Document d = db.parse(new File(pathFileConfigurazione));
        Schema s = sf.newSchema(new StreamSource(new File(pathFileXSDConfigurazione)));
        s.newValidator().validate(new DOMSource(d));

        return true;
    }
}

/*
Note:
(0) Costruttore GestoreParametriConfigurazioneXML().
    Come argomenti riceve la locazione del file di configurazione locale
    da cui leggere i parametri e il file .xsd da utilizzare per validare 
    i parametri presenti nel file di configurazione prima della lettura.

(1) Funzione leggiFileDiConfigurazione().
    Legge il file di configurazione locale in XML dopo che i campi sono stati
    validati utilizzando lo schema .xsd indicato.

(2) Funzione validaFileDiConfigurazione().
    Valida i parametri presenti nel file di cofigurazione XML locale utilizzando
    lo schema XSD indicato.
 */
