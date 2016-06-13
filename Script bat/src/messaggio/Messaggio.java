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
public class Messaggio implements Serializable { // 0

    private TipoMessaggio tipo;
    private String mittente;
    private String destinatario;
    private String testo;

    public Messaggio(TipoMessaggio t, String m, String d, String te) {
        tipo = t;
        mittente = m;
        destinatario = d;
        testo = te;
    }

    public void setTipo(TipoMessaggio t) {
        tipo = t;
    }

    public TipoMessaggio getTipo() {
        return tipo;
    }

    public void setMittente(String m) {
        mittente = m;
    }

    public String getMittente() {
        return mittente;
    }

    public void setDestinatario(String d) {
        destinatario = d;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setTesto(String te) {
        testo = te;
    }

    public String getTesto() {
        return testo;
    }
}

/*
Note:
(0) Classe Messaggio.
    Implementa Serializable.
    Rappresenta l'oggetto che viene scritto e letto da ObjectOutputStream
    e ObjectInputStream durante la comunicazione tra Server di Log
    e Client o tra due Client.
 */
