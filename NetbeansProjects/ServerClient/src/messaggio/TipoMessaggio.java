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
public enum TipoMessaggio implements Serializable { // 0
    MESSAGGIO_NORMALE,
    MESSAGGIO_LOG,
    IDENTIFICAZIONE_CLIENT,
    IDENTIFICAZIONE_CLIENT_FALLITA,
    CLIENT_DISCONNESSO,
    SERVER_LOG_ARRESTATO
}

/*
Note:
(0) Enumerazione TipoMessaggio.
    Le costanti predefinite dell'enumerazione TipoMessaggio rappresentano
    i tipi di messaggio che possono eseere inviati tra Server di Log e Client
    o tra due Client.
 */
