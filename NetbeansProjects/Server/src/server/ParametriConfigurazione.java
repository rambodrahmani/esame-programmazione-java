/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.Serializable;

/**
 *
 * @author fr33d0mf1ght3r
 */
public class ParametriConfigurazione implements Serializable { // 0

    public int portaServer;
    public String pathFileDiLog;
    public String pathFileXSDLogs;
    
}

/*
Note:
(0) Classe ParametriConfigurazione.
    Contiene i campi public in cui vengono memorizzati i parametri letti
    dal file di configurazione locale XML. Utilizzato dalla classe
    GestoreParametriConfigurazioneXML.
 */
