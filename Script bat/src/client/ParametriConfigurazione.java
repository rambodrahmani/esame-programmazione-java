/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.Serializable;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class ParametriConfigurazione implements Serializable  {

    public int portaServerDiLog;
    public String indirizzoIPServerDiLog;
    public int portaClient;
    public String indirizzoIPDatabase;
    public int portaDatabase;
    public String usernameDatabase;
    public String passwordDatabase;
    public int numUltimiMessaggiCache;

}

/*
Note:
(0) Classe ParametriConfigurazione.
    Contiene i campi public in cui vengono memorizzati i parametri letti
    dal file di configurazione locale XML. Utilizzato dalla classe
    GestoreParametriConfigurazioneXML.
 */
