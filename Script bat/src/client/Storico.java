/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class Storico { // 0

    private SimpleStringProperty email;
    private SimpleDoubleProperty durata;

    Storico(String e, Double d) {
        email = new SimpleStringProperty(e);
        durata = new SimpleDoubleProperty(d);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String s) {
        email = new SimpleStringProperty(s);
    }

    public Double getDurata() {
        return durata.get();
    }

    public void setDurata(Double d) {
        durata = new SimpleDoubleProperty(d);
    }
}

/*
Note:
(0) Classe utilizzata dalla classe GestoreBaseDiDati per la lettura delle
    statistiche relative al numero di ore spese connesse per ciascun Client
    dalla tabella "storico" della base di dati.
 */
