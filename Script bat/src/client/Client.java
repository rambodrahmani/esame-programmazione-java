/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class Client {

    private static GestoreParametriConfigurazioneXML gestoreParametriConfig;
    private static FrameContatti frameContatti;

    private static boolean caricaConfigurazioni() {
        try {
            System.out.println("Parametri di configurazione caricati correttamente.");
            gestoreParametriConfig = new GestoreParametriConfigurazioneXML("configurazioni.xml", "configurazioni.xsd");
            gestoreParametriConfig.leggiFileDiConfigurazione();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println("Errore durante la lettura del file di configurazione " + e.getMessage() + ". Termino Client.");
            System.exit(0);
        }

        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { // 0
        if (caricaConfigurazioni()) {
            frameContatti = new FrameContatti();
        }
    }
}

/*
Note:
(0) Funzione main.
    Inizializza un oggetto GestoreParametriConfigurazioneXML. Se i parametri
    di configurazione vengono letti correttamente, allora lancia il frame
    Contatti.
 */
