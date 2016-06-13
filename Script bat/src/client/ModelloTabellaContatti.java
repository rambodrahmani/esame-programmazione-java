/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class ModelloTabellaContatti extends AbstractTableModel {

    private List<Contatto> listaElementi;
    private int cacheNumeroElementi;

    public ModelloTabellaContatti() { // 0
        listaElementi = new ArrayList<>();
        cacheNumeroElementi = 0;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) { // 1
        return false;
    }

    @Override
    public int getRowCount() { // 2
        return listaElementi.size();
    }

    @Override
    public int getColumnCount() { // 3
        return 1;
    }

    @Override
    public String getColumnName(int column) { // 4
        String nome = null;
        switch (column) {
            case 0:
                nome = "Email";
                break;
        }
        return nome;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) { // 5
        Class classe = String.class;
        switch (columnIndex) {
            case 1:
                classe = String.class;
                break;
        }
        return classe;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { // 6
        Object valore = null;
        Contatto contatto = listaElementi.get(rowIndex);
        switch (columnIndex) {
            case 0:
                valore = contatto.getEmail();
                break;
            case 1:
                valore = contatto.getIndirizzoIP();
                break;
            case 2:
                valore = contatto.getData();
                break;
        }
        return valore;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) { // 7
        Contatto contatto = listaElementi.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (aValue instanceof String) {
                    contatto.setEmail(aValue.toString());
                }
                break;
            case 1:
                if (aValue instanceof Integer) {
                    contatto.setIndirizzoIP(aValue.toString());
                }
                break;
            case 2:
                if (aValue instanceof Integer) {
                    contatto.setData(aValue.toString());
                }
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void aggiornaContatti(ObservableList<Contatto> nuovaListaContatti) { // 8
        listaElementi = new ArrayList<>();

        nuovaListaContatti.stream().forEach((contatto) -> {
            listaElementi.add(new Contatto(contatto.getEmail(), contatto.getIndirizzoIP(), contatto.getData()));
        });

        if (listaElementi.size() >= cacheNumeroElementi) {
            fireTableRowsInserted(0, listaElementi.size());
        } else {
            fireTableRowsInserted(0, cacheNumeroElementi);
        }

        cacheNumeroElementi = listaElementi.size();
    }
}

/*
Note:
(0) Costruttore ModelloTabellaContatti().
    Inizializza la variabile listaEmelenti a ArrayList<>() e cacheNumeroElementi
    la imposta uguale a 0.

(1) Funzione isCellEditable().
    Restitusice sempre false: le celle della tabella non sono modificabili.

(2) Funzione getRowCount().
    Restitusice il numero di elementi della variabile List<Contatto> 
    listaElementi.

(3) Funzione getColumnCount().
    Imposta ad 1 il numero di colonne della tabella.

(4) Funzione getColumnName().
    Imposta una unica colonna per la tabella, quella di "Email", che contiene
    l'indirizzo email con il quale il contatto di riferimento si e'
    connesso al servizio di messaggistica.

(5) Funzione getColumnClass().
    Restituisce la classe del tipo di dati presente nella colonna indicata come
    argomento passato alla funzione.

(6) Funzione getValueAt().
    Restituisce il valore che si trova nella cella in posizione delle due 
    variabili passate: int rowIndex, int columnIndex.

(7) Funzione setValueAt().
    Imposta il valore che si trova nella cella in posizione delle due 
    variabili passate: int rowIndex, int columnIndex.

(8) Funzione aggiornaContatti().
    Riceve come argomento il risultato della query della classe 
    GestoreBaseDiDati sotto forma di ObservableList<Contatto>. itera il 
    contenuto della ObservableList e aggiorna il contenuto della variabile di 
    tipo List<Contatto> contenente la lista dei Client connessi della classe.
 */
