package model;

public class Student {
    private String matrikelnummer;
    private String vorname;
    private String nachname;
    private String studiengang;

    public Student(String matrikelnummer, String vorname, String nachname, String studiengang) {
        this.matrikelnummer = matrikelnummer;
        this.vorname = vorname;
        this.nachname = nachname;
        this.studiengang = studiengang;
    }

    public String getMatrikelnummer() { return matrikelnummer; }
    public String getVorname() { return vorname; }
    public String getNachname() { return nachname; }
    public String getStudiengang() { return studiengang; }

    @Override
    public String toString() {
        return matrikelnummer + ": " + vorname + " " + nachname + " (" + studiengang + ")";
    }
}