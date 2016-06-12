/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class GestoreCacheConversazioni {

    private FileOutputStream scrittoreFile = null;
    private FileInputStream lettoreFile = null;
    private final File fileCacheConversazione;

    GestoreCacheConversazioni(Contatto destinatario) throws IOException { // 0
        fileCacheConversazione = new File("conversazioni/" + destinatario.getEmail() + ".bin");
        
        File cartellaConversazioni = new File("conversazioni");
        if (!cartellaConversazioni.exists()) {
            if (!cartellaConversazioni.mkdirs()) {
                throw new IOException("Errore durante la creazione della cartella conversaioni.");
            }
        }
    }

    public void salvaConversazione(String testoConversazione) throws IOException { // 1
        if (!fileCacheConversazione.exists()) {
            fileCacheConversazione.createNewFile();
        }

        byte[] contentInBytes = testoConversazione.getBytes();

        scrittoreFile = new FileOutputStream(fileCacheConversazione);

        scrittoreFile.write(contentInBytes);
        scrittoreFile.flush();
        scrittoreFile.close();
    }

    public List<String> prelevaConversazione() throws FileNotFoundException, IOException { // 2
        if (!fileCacheConversazione.exists()) {
            fileCacheConversazione.createNewFile();
        }

        lettoreFile = new FileInputStream(fileCacheConversazione);

        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = lettoreFile.read()) != -1) {
            builder.append((char) ch);
        }

        lettoreFile.close();
        return new ArrayList<String>(Arrays.asList(builder.toString().split("\\r?\\n")));
    }
}

/*
Note:
(0) Costruttore GestoreCacheConversaioni().
    Controlla che sia presente la cartella conversazioni, altrimenti la crea.
    In caso di errore lancia una IOException.

(1) Funzione salvaConversazione().
    Controlla che il file .bin sia presente altrimenti lo crea. Salva all'
    interno di tale file la stringa testoConversazione contenente gli ultimi
    messaggi scambiati con il contatto, come indicato nel file di 
    configurazione locale.

(2) Funzione prelevaConversazione().
    Restituisce un ArrayList<String>, contenente le righe lette dal file
    .bin contenente gli ultimi messaggi salvati di una precedente 
    conversazione con lo stesso contatto.
 */
