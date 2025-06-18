package src.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Student erbt von Person und erweitert um studentenspezifische Eigenschaften
 */
public class Student extends Person implements Comparable<Student> {
    private String matrikelnummer;
    private String studiengang;
    private List<Versuch> versuche;
    private List<Klausur> angemeldeteKlausuren;
    
    public Student(String matrikelnummer, String vorname, String nachname, String studiengang) {
        super(matrikelnummer, vorname, nachname, LocalDate.now()); // Temporary
        this.matrikelnummer = matrikelnummer;
        this.studiengang = studiengang;
        this.versuche = new ArrayList<>();
        this.angemeldeteKlausuren = new ArrayList<>();
    }
    
    public Student(String matrikelnummer, String vorname, String nachname, String studiengang, LocalDate geburtsdatum) {
        super(matrikelnummer, vorname, nachname, geburtsdatum);
        this.matrikelnummer = matrikelnummer;
        this.studiengang = studiengang;
        this.versuche = new ArrayList<>();
        this.angemeldeteKlausuren = new ArrayList<>();
    }
    
    // Getter/Setter
    public String getMatrikelnummer() { return matrikelnummer; }
    public String getStudiengang() { return studiengang; }
    public void setStudiengang(String studiengang) { this.studiengang = studiengang; }
    
    // Versuch-Management
    public void addVersuch(Versuch versuch) {
        if (versuch != null) {
            this.versuche.add(versuch);
        }
    }
    
    public void removeVersuch(Versuch versuch) {
        this.versuche.remove(versuch);
    }
    
    public List<Versuch> getVersuche() {
        return new ArrayList<>(versuche); // Defensive copy
    }
    
    // Klausur-Anmeldung
    public void anmeldenZuKlausur(Klausur klausur) throws FristAbgelaufenException {
        if (klausur.istFristAbgelaufen()) {
            throw new FristAbgelaufenException("Anmeldefrist für " + klausur.getTitel() + " ist abgelaufen!");
        }
        if (!angemeldeteKlausuren.contains(klausur)) {
            angemeldeteKlausuren.add(klausur);
        }
    }
    
    public List<Klausur> getAngemeldeteKlausuren() {
        return new ArrayList<>(angemeldeteKlausuren);
    }
    
    /**
     * Zeigt den aktuellen Prüfungsstatus für alle Klausuren
     */
    public String zeigePruefungsstatus() {
        StringBuilder status = new StringBuilder();
        status.append("Prüfungsstatus für ").append(getVorname()).append(" ").append(getNachname()).append(":\n");
        
        for (Klausur klausur : angemeldeteKlausuren) {
            List<Versuch> klausurVersuche = getVersucheFuerKlausur(klausur);
            if (klausurVersuche.isEmpty()) {
                status.append("- ").append(klausur.getTitel()).append(": Angemeldet, noch nicht absolviert\n");
            } else {
                Versuch letzterVersuch = klausurVersuche.get(klausurVersuche.size() - 1);
                if (letzterVersuch.istBestanden()) {
                    status.append("- ").append(klausur.getTitel()).append(": BESTANDEN (Note: ")
                          .append(letzterVersuch.getNote()).append(")\n");
                } else {
                    status.append("- ").append(klausur.getTitel()).append(": Nicht bestanden (Versuch ")
                          .append(klausurVersuche.size()).append("/").append(klausur.getMaxVersuche()).append(")\n");
                }
            }
        }
        
        return status.toString();
    }
    
    /**
     * Berechnet den Notendurchschnitt aller bestandenen Prüfungen
     */
    public double berechneNotendurchschnitt() {
        List<Double> bestandeneNoten = new ArrayList<>();
        
        for (Versuch versuch : versuche) {
            if (versuch.istBestanden()) {
                bestandeneNoten.add(versuch.getNote());
            }
        }
        
        if (bestandeneNoten.isEmpty()) {
            return 0.0; // Keine bestandenen Prüfungen
        }
        
        double summe = bestandeneNoten.stream().mapToDouble(Double::doubleValue).sum();
        return summe / bestandeneNoten.size();
    }
    
    private List<Versuch> getVersucheFuerKlausur(Klausur klausur) {
        return versuche.stream()
                .filter(v -> v.getKlausur().equals(klausur))
                .toList();
    }
    
    @Override
    public int compareTo(Student other) {
        return this.matrikelnummer.compareTo(other.matrikelnummer);
    }
    
    @Override
    public String toString() {
        return matrikelnummer + ": " + vorname + " " + nachname + " (" + studiengang + ")";
    }
}
