/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaggio;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class MessaggioDiLog implements Serializable { // 0

    private TipoMessaggioDiLog tipo;
    private String mittente;
    private String data;

    public MessaggioDiLog(TipoMessaggioDiLog t, String m) {
        tipo = t;
        mittente = m;
        
        Timestamp timeStampCorrente = new Timestamp(System.currentTimeMillis());
        data = timeStampCorrente.toString();
    }
    
    public void setMittente(String m) {
        mittente = m;
    }
    
    public String getMittente() {
        return mittente;
    }
    
    public void setTipo(TipoMessaggioDiLog t) {
        tipo = t;
    }
    
    public TipoMessaggioDiLog getTipo() {
        return tipo;
    }
    
    public void setData(String d) {
        data = d;
    }
    
    public String getData() {
        return data;
    }
}

/*
Note:
(0) Classe MessaggioDiLog.
    Implementa Serializable.
    Rappresenta l'oggetto che viene scritto e letto da ObjectOutputStream
    e ObjectInputStream durante la comunicazione tra Server di Log
    e Client.
 */
