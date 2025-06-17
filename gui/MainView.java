package gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class MainView {

    private final Stage stage;
    private BorderPane root;

    public MainView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // === Sidebar (Navigation) ===
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(200);

        Button btnStart = new Button("🏠 Start");
        Button btnStudierende = new Button("🎓 Studierende");
        Button btnPruefungen = new Button("🧪 Prüfungen");

        for (Button b : new Button[]{btnStart, btnStudierende, btnPruefungen}) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.getStyleClass().add("nav-button");
        }

        sidebar.getChildren().addAll(btnStart, btnStudierende, btnPruefungen);

        // === Inhalt rechts (wird dynamisch ersetzt) ===
        StackPane contentPane = new StackPane();
        contentPane.getChildren().add(new Label("Willkommen zur Verwaltungsanwendung"));

        // === Root-Layout: links Navigation, rechts Inhalt ===
        root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentPane);

        // === Scene erstellen ===
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Studierendenverwaltung");
        stage.setScene(scene);
        stage.show();

        // === Button-Aktionen ===
        btnStart.setOnAction(e -> contentPane.getChildren().setAll(new Label("Willkommen zur Verwaltungsanwendung")));
        btnStudierende.setOnAction(e -> contentPane.getChildren().setAll(new StudentsView()));
        btnPruefungen.setOnAction(e -> contentPane.getChildren().setAll(new ExamsView()));
    }
}

