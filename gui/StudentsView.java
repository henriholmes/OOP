package gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Student;
import verwaltung.StudentenVerwaltung;

import java.util.List;

public class StudentsView extends VBox {

    private final StudentenVerwaltung verwaltung = new StudentenVerwaltung();
    private final ListView<String> listView = new ListView<>();

    public StudentsView() {
        setSpacing(10);
        setPadding(new Insets(20));

        // Eingabefelder für neuen Student
        TextField tfMatrikel = new TextField();
        tfMatrikel.setPromptText("Matrikelnummer");

        TextField tfVorname = new TextField();
        tfVorname.setPromptText("Vorname");

        TextField tfNachname = new TextField();
        tfNachname.setPromptText("Nachname");

        TextField tfStudiengang = new TextField();
        tfStudiengang.setPromptText("Studiengang");

        // Suchfeld
        TextField tfSuche = new TextField();
        tfSuche.setPromptText("Nachname suchen");

        // Buttons
        Button btnAdd = new Button("➕ Hinzufügen");
        Button btnDelete = new Button("🗑 Löschen");
        Button btnRefresh = new Button("🔄 Alle anzeigen");
        Button btnSearch = new Button("🔍 Suchen");

        // Initiale Anzeige
        aktualisiereListe();

        // Aktionen
        btnAdd.setOnAction(e -> {
            Student s = new Student(
                tfMatrikel.getText(),
                tfVorname.getText(),
                tfNachname.getText(),
                tfStudiengang.getText()
            );
            verwaltung.hinzufuegen(s);
            aktualisiereListe();
            tfMatrikel.clear();
            tfVorname.clear();
            tfNachname.clear();
            tfStudiengang.clear();
        });

        btnDelete.setOnAction(e -> {
            verwaltung.loeschen(tfMatrikel.getText());
            aktualisiereListe();
        });

        btnRefresh.setOnAction(e -> aktualisiereListe());

        btnSearch.setOnAction(e -> {
            listView.getItems().clear();
            List<Student> gefiltert = verwaltung.suchenNachName(tfSuche.getText());
            gefiltert.forEach(s -> listView.getItems().add(s.toString()));
        });

        // Layout
        HBox eingaben = new HBox(10, tfMatrikel, tfVorname, tfNachname, tfStudiengang);
        HBox aktionen = new HBox(10, btnAdd, btnDelete, btnRefresh);
        HBox suche = new HBox(10, tfSuche, btnSearch);

        getChildren().addAll(new Label("🎓 Studierendenliste"), listView, eingaben, aktionen, suche);
    }

    private void aktualisiereListe() {
        listView.getItems().clear();
        List<Student> studenten = verwaltung.suchenNachName(""); // alle
        studenten.forEach(s -> listView.getItems().add(s.toString()));
    }
}
