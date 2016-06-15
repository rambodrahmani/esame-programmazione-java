/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaggio;

import java.io.Serializable;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public enum TipoMessaggioDiLog implements Serializable { // 0
    CLICK_PULSANTE_CONNETTI,
    CLICK_PULSANTE_DISCONNETTI,
    CLICK_MENU_STRUMENTI,
    CLICK_MENU_STATISTICHE,
    CLICK_PULSANTE_INVIA,
    DOPPIO_CLICK_LISTA_CONTATTI
}

/*
Note:
(0) Enumerazione TipoMessaggioDiLog.
    Le costanti predefinite dell'enumerazione TipoMessaggioDiLog rappresentano
    i tipi di log che possono eseere inviati al Server di Log.
 */
