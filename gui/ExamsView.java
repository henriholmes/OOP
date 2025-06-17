package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ExamsView extends VBox {

    public ExamsView() {
        Label label = new Label("🧪 Prüfungsverwaltung (noch leer)");
        this.getChildren().add(label);
        this.setSpacing(20);
        this.setStyle("-fx-padding: 20;");
    }
}

