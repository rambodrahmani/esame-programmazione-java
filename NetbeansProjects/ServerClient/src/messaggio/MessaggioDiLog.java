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
public class MessaggioDiLog implements Serializable {

    private final TipoMessaggioDiLog evento;
    private final String data;

    public MessaggioDiLog(TipoMessaggioDiLog e) {
        evento = e;
        
        Timestamp timeStampCorrente = new Timestamp(System.currentTimeMillis());
        data = timeStampCorrente.toString();
    }
}

/*
Note:
(0) 

 */
