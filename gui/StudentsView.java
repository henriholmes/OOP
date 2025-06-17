package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StudentsView extends VBox {

    public StudentsView() {
        this.getChildren().add(new Label("🧑‍🎓 Studierendenansicht (Demo)"));
        this.setSpacing(20);
        this.setStyle("-fx-padding: 20;");
    }
}
