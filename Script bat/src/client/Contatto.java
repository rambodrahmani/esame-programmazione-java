/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class Contatto { // 0

    private SimpleStringProperty email;
    private SimpleStringProperty indirizzoIP;
    private SimpleStringProperty data;

    Contatto(String e, String i, String d) {
        email = new SimpleStringProperty(e);
        indirizzoIP = new SimpleStringProperty(i);
        data = new SimpleStringProperty(d);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String e) {
        email = new SimpleStringProperty(e);
    }

    public String getIndirizzoIP() {
        return indirizzoIP.get();
    }

    public void setIndirizzoIP(String i) {
        indirizzoIP = new SimpleStringProperty(i);
    }

    public String getData() {
        return data.get();
    }

    public void setData(String d) {
        data = new SimpleStringProperty(d);
    }
}

/*
Note:
(0) Classe utilizzata dalla classe GestoreBaseDiDati per la lettura dei Client
    connessi al servizio di messaggistica dalla tabella "contatti" ella base di
    dati.
 */
