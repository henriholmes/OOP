package gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StartView {

    private Stage stage;

    public StartView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Titel
        Label titel = new Label("📚 Studierendenverwaltung");
        titel.setFont(new Font("Arial", 24));
        titel.getStyleClass().add("titel");

        // Menü-Buttons
        Button btnStudierende = new Button("Studierende verwalten");
        Button btnPruefungen = new Button("Prüfungen");
        Button btnNoten = new Button("Notenübersicht");

        btnStudierende.getStyleClass().add("menu-button");
        btnPruefungen.getStyleClass().add("menu-button");
        btnNoten.getStyleClass().add("menu-button");

        // Layout
        VBox layout = new VBox(20, titel, btnStudierende, btnPruefungen, btnNoten);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("background");

        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Startfenster");
        stage.setScene(scene);
        stage.show();
    }
}