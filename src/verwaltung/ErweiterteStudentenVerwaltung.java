package verwaltung;

import util.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.*;

/**
 * Erweiterte Studentenverwaltung mit zusätzlichen Funktionen
 */
public class ErweiterteStudentenVerwaltung {
    private List<Student> studenten;
    private VersuchsVerwaltung versuchsVerwaltung;
    private BenachrichtigungsVerwaltung benachrichtigungsVerwaltung;
    
    public ErweiterteStudentenVerwaltung() {
        this.studenten = new ArrayList<>();
        this.versuchsVerwaltung = new VersuchsVerwaltung();
        this.benachrichtigungsVerwaltung = new BenachrichtigungsVerwaltung();
        erstelleTabelleWennNichtVorhanden();
        ladeDatenAusDatenbank();
    }
    
    /**
     * Fügt einen neuen Studenten hinzu
     */
    public void hinzufuegen(Student student) {
        if (!studenten.contains(student)) {
            studenten.add(student);
            speichereInDatenbank(student);
        }
    }
    
    /**
     * Löscht einen Studenten anhand der Matrikelnummer
     */
    public boolean loeschen(String matrikelnummer) {
        boolean entfernt = studenten.removeIf(s -> s.getMatrikelnummer().equals(matrikelnummer));
        if (entfernt) {
            loescheAusDatenbank(matrikelnummer);
        }
        return entfernt;
    }
    
    /**
     * Sucht Studenten nach Nachname (Volltext)
     */
    public List<Student> suchenNachName(String nachname) {
        return studenten.stream()
                .filter(s -> s.getNachname().toLowerCase().contains(nachname.toLowerCase()) ||
                           s.getVorname().toLowerCase().contains(nachname.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Sucht Studenten nach Studiengang
     */
    public List<Student> suchenNachStudiengang(String studiengang) {
        return studenten.stream()
                .filter(s -> s.getStudiengang().toLowerCase().contains(studiengang.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Gibt alle Studenten sortiert zurück
     */
    public List<Student> getAlleSortiert(SortierKriterium kriterium) {
        Comparator<Student> comparator;
        
        switch (kriterium) {
            case NACHNAME:
                comparator = Comparator.comparing(Student::getNachname)
                                     .thenComparing(Student::getVorname);
                break;
            case VORNAME:
                comparator = Comparator.comparing(Student::getVorname)
                                     .thenComparing(Student::getNachname);
                break;
            case STUDIENGANG:
                comparator = Comparator.comparing(Student::getStudiengang)
                                     .thenComparing(Student::getNachname);
                break;
            case MATRIKELNUMMER:
            default:
                comparator = Comparator.comparing(Student::getMatrikelnummer);
                break;
        }
        
        return studenten.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    /**
     * Findet Student anhand Matrikelnummer
     */
    public Student findeNachMatrikelnummer(String matrikelnummer) {
        return studenten.stream()
                .filter(s -> s.getMatrikelnummer().equals(matrikelnummer))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Meldet einen Studenten zu einer Klausur an
     */
    public void anmeldenZuKlausur(String matrikelnummer, Klausur klausur) 
            throws FristAbgelaufenException, KlausurKonfliktException {
        Student student = findeNachMatrikelnummer(matrikelnummer);
        if (student == null) {
            throw new IllegalArgumentException("Student nicht gefunden: " + matrikelnummer);
        }
        
        // Prüfe auf Konflikte mit bereits angemeldeten Klausuren
        for (Klausur angemeldet : student.getAngemeldeteKlausuren()) {
            if (angemeldet.konfliktMit(klausur)) {
                throw new KlausurKonfliktException("Zeitkonflikt mit " + angemeldet.getTitel());
            }
        }
        
        // Anmeldung durchführen
        student.anmeldenZuKlausur(klausur);
        klausur.studentHinzufuegen(student);
        
        // Benachrichtigung senden
        benachrichtigungsVerwaltung.benachrichtigeKlausuranmeldung(student, klausur);
    }
    
    /**
     * Registriert einen Prüfungsversuch und benachrichtigt den Studenten
     */
    public void versuchEintragen(String matrikelnummer, Klausur klausur, double note, LocalDate datum) {
        Student student = findeNachMatrikelnummer(matrikelnummer);
        if (student == null) {
            throw new IllegalArgumentException("Student nicht gefunden: " + matrikelnummer);
        }
        
        Versuch versuch = new Versuch(student, klausur, note, datum);
        versuchsVerwaltung.versuchHinzufuegen(versuch);
        
        // Benachrichtigung über verfügbare Note
        benachrichtigungsVerwaltung.benachrichtigeNotenVerfuegbar(student, versuch);
    }
    
    /**
     * Gibt Studenten mit schlechten Leistungen zurück (Durchschnitt > 3.0)
     */
    public List<Student> getStudentenMitSchlechtenLeistungen() {
        return studenten.stream()
                .filter(s -> {
                    double durchschnitt = s.berechneNotendurchschnitt();
                    return durchschnitt > 3.0 && durchschnitt > 0; // > 0 bedeutet es gibt Noten
                })
                .sorted(Comparator.comparing(Student::berechneNotendurchschnitt).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Erstellt automatische Erinnerungen für alle Studenten
     */
    public void erstelleAutomatischeErinnerungen(List<Klausur> klausuren) {
        benachrichtigungsVerwaltung.erstelleFristerinnerungen(studenten, klausuren);
    }
    
    public VersuchsVerwaltung getVersuchsVerwaltung() {
        return versuchsVerwaltung;
    }
    
    public BenachrichtigungsVerwaltung getBenachrichtigungsVerwaltung() {
        return benachrichtigungsVerwaltung;
    }
    
    public enum SortierKriterium {
        MATRIKELNUMMER, NACHNAME, VORNAME, STUDIENGANG
    }
    
    // Datenbankoperationen
    private void erstelleTabelleWennNichtVorhanden() {
        String sql = """
            CREATE TABLE IF NOT EXISTS student (
                matrikelnummer TEXT PRIMARY KEY,
                vorname TEXT NOT NULL,
                nachname TEXT NOT NULL,
                studiengang TEXT NOT NULL,
                geburtsdatum TEXT
            )
        """;
        
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Student-Tabelle: " + e.getMessage());
        }
    }
    
    private void speichereInDatenbank(Student student) {
        String sql = "INSERT OR REPLACE INTO student (matrikelnummer, vorname, nachname, studiengang, geburtsdatum) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getMatrikelnummer());
            pstmt.setString(2, student.getVorname());
            pstmt.setString(3, student.getNachname());
            pstmt.setString(4, student.getStudiengang());
            pstmt.setString(5, student.getGeburtsdatum() != null ? student.getGeburtsdatum().toString() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern des Studenten: " + e.getMessage());
        }
    }
    
    private void loescheAusDatenbank(String matrikelnummer) {
        String sql = "DELETE FROM student WHERE matrikelnummer = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matrikelnummer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen des Studenten: " + e.getMessage());
        }
    }
    
    private void ladeDatenAusDatenbank() {
        String sql = "SELECT * FROM student";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String geburtsdatumStr = rs.getString("geburtsdatum");
                LocalDate geburtsdatum = geburtsdatumStr != null ? LocalDate.parse(geburtsdatumStr) : null;
                
                Student student = new Student(
                        rs.getString("matrikelnummer"),
                        rs.getString("vorname"),
                        rs.getString("nachname"),
                        rs.getString("studiengang"),
                        geburtsdatum
                );
                studenten.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Laden der Studenten: " + e.getMessage());
        }
    }
    
    /**
     * Zeigt alle Studenten auf der Konsole an
     */
    public void alleAnzeigen() {
        if (studenten.isEmpty()) {
            System.out.println("Keine Studenten vorhanden.");
            return;
        }
        
        System.out.println("\n=== ALLE STUDENTEN ===");
        studenten.stream()
                .sorted()
                .forEach(System.out::println);
        System.out.println("Gesamt: " + studenten.size() + " Studenten\n");
    }
}
