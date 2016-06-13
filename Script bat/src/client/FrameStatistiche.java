/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Rambod Rahmani <rambodrahmani@autistici.org>
 */
public class FrameStatistiche extends JFrame {

    // componenti frame statistiche
    private JFrame frameStatistiche;
    private static Container contentPaneStatistiche;
    private JPanel panelContenitore;
    private JFXPanel pannelloFX;
    private static PieChart diagrammaATorta;
    private static ObservableList<PieChart.Data> datiDiagrammaATorta;

    FrameStatistiche() { // 0
        allestisciFrameStatistiche();

        Platform.runLater(() -> {
            initFX(pannelloFX);
        });
    }

    private void allestisciFrameStatistiche() { // 1
        frameStatistiche = new JFrame("Statistiche");
        ImageIcon img = new ImageIcon("client.png");
        frameStatistiche.setIconImage(img.getImage());

        allestisciContentPaneStatistiche();

        frameStatistiche.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameStatistiche.setResizable(false);
        frameStatistiche.pack();
        frameStatistiche.setLocationRelativeTo(null);

        frameStatistiche.setVisible(true);
    }

    private void allestisciContentPaneStatistiche() { // 1.1
        contentPaneStatistiche = frameStatistiche.getContentPane();
        contentPaneStatistiche.setLayout(new BorderLayout(10, 5));
        allestisciPanelContenitore();
        contentPaneStatistiche.add(panelContenitore, BorderLayout.CENTER);
    }

    private void allestisciPanelContenitore() { // 1.2
        panelContenitore = new JPanel();
        panelContenitore.setLayout(new BorderLayout(10, 5));
        panelContenitore.setPreferredSize(new Dimension(600, 590));
        panelContenitore.setBorder(new EmptyBorder(0, 0, 0, 0));
        pannelloFX = new JFXPanel();
        pannelloFX.setPreferredSize(new Dimension(600, 590));
        panelContenitore.add(pannelloFX, BorderLayout.CENTER);
    }

    private static void initFX(JFXPanel fxPanel) { // 2
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static Scene createScene() { // 3
        Group root = new Group();
        Scene scene = new Scene(root, 0, 0);
        try {
            ObservableList<Storico> listaStatistiche = FrameContatti.gestoreBaseDiDati.ottieniStatistiche();

            datiDiagrammaATorta = FXCollections.observableArrayList();
            listaStatistiche.stream().forEach((storico) -> {
                String titolo = storico.getEmail() + " \n (" + storico.getDurata() + " h)";
                datiDiagrammaATorta.add(new PieChart.Data(titolo, storico.getDurata()));
            });

            diagrammaATorta = new PieChart(datiDiagrammaATorta);
            diagrammaATorta.setTitle("Statistiche Contatti");
            diagrammaATorta.setPrefSize(600, 540);

            ((Group) scene.getRoot()).getChildren().add(diagrammaATorta);
        } catch (SQLException ex) {
            mostraMessaggioErrore("Errore inizializzazione Diagramma a Torta", "Errore durante l'inizializzazione del diagramma a torta delle statistiche: " + ex.getMessage());
        }

        return (scene);
    }

    static void mostraMessaggioErrore(String titolo, String messaggio) { // 4
        JOptionPane.showMessageDialog(contentPaneStatistiche, messaggio, titolo, JOptionPane.ERROR_MESSAGE);
    }

}

/*
Note:
(0) Costruttore FrameStatistiche().
    Richiama la fuzione (1) che fa parte la catena di chiamate che inizializzano
    gli elementi che compongono il JFrame della classe.

(1) Funzione allestisciFrameStatistiche().
    Richiama a catena le funzioni (1.1) e (1.2) per inizializzare gli elementi
    che compongono il JFrame della classe.

(2) Funzione initFX().
    Inizializza il panello JavaFX.

(3) Funzione createScene().
    Create l'oggetto Scene sul Thread della JavaApplication e inizializza una
    istanza della classe PieChart.

(4) Funzione mostraMessaggioErrore().
    Funzione di utilita' utilizzata per mostrare una MessageDialog di tipo
    ERROR_MESSAGE, utilizzata al verificarsi di errori durante l'utilizzo.

(5) Il pannell JavaFX usato in questa classe causa un bug aperto e irrisolto 
    nel JDK: https://bugs.openjdk.java.net/browse/JDK-8089371
 */
