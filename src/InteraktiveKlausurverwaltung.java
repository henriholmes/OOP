import model.*;
import verwaltung.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;

public class InteraktiveKlausurverwaltung {
    private static Scanner scanner = new Scanner(System.in);
    private static ErweiterteStudentenVerwaltung studentenVerwaltung = new ErweiterteStudentenVerwaltung();
    private static KlausurVerwaltung klausurVerwaltung = new KlausurVerwaltung();
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   KLAUSURVERWALTUNGSSYSTEM v2.0        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Beispieldaten laden
        ladeBeisspieldaten();
        
        boolean running = true;
        while (running) {
            zeigeHauptmenu();
            int wahl = leseInt("Ihre Wahl: ");
            
            switch (wahl) {
                case 1 -> studentenMenu();
                case 2 -> klausurenMenu();
                case 3 -> anmeldungenMenu();
                case 4 -> notenMenu();
                case 5 -> statistikMenu();
                case 6 -> benachrichtigungsMenu();
                case 0 -> {
                    System.out.println("\n👋 Auf Wiedersehen!");
                    running = false;
                }
                default -> System.out.println("❌ Ungültige Eingabe!");
            }
        }
        scanner.close();
    }
    
    private static void zeigeHauptmenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           HAUPTMENÜ                    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. 👨‍🎓 Studentenverwaltung              ║");
        System.out.println("║ 2. 📝 Klausurverwaltung                ║");
        System.out.println("║ 3. ✍️  Klausuranmeldungen               ║");
        System.out.println("║ 4. 📊 Notenverwaltung                  ║");
        System.out.println("║ 5. 📈 Statistiken                      ║");
        System.out.println("║ 6. 📧 Benachrichtigungen               ║");
        System.out.println("║ 0. 🚪 Beenden                          ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    private static void studentenMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- STUDENTENVERWALTUNG ---");
            System.out.println("1. Alle Studenten anzeigen");
            System.out.println("2. Student hinzufügen");
            System.out.println("3. Student suchen");
            System.out.println("4. Student löschen");
            System.out.println("0. Zurück zum Hauptmenü");
            
            int wahl = leseInt("Wahl: ");
            
            switch (wahl) {
                case 1 -> alleStudentenAnzeigen();
                case 2 -> studentHinzufuegen();
                case 3 -> studentSuchen();
                case 4 -> studentLoeschen();
                case 0 -> inMenu = false;
                default -> System.out.println("❌ Ungültige Eingabe!");
            }
        }
    }
    
    private static void alleStudentenAnzeigen() {
        System.out.println("\n📋 ALLE STUDENTEN:");
        System.out.println("─".repeat(60));
        List<Student> studenten = studentenVerwaltung.getAlleSortiert(
            ErweiterteStudentenVerwaltung.SortierKriterium.NACHNAME
        );
        
        if (studenten.isEmpty()) {
            System.out.println("Keine Studenten vorhanden.");
        } else {
            for (Student s : studenten) {
                System.out.printf("• %s - %s (%s, Geb: %s)\n", 
                    s.getMatrikelnummer(), 
                    s.getVorname() + " " + s.getNachname(),
                    s.getStudiengang(),
                    s.getGeburtsdatum().format(dateFormatter)
                );
            }
            System.out.println("\nGesamt: " + studenten.size() + " Studenten");
        }
    }
    
    private static void studentHinzufuegen() {
        System.out.println("\n➕ NEUEN STUDENTEN HINZUFÜGEN:");
        
        String matrikelnummer = leseString("Matrikelnummer: ");
        String vorname = leseString("Vorname: ");
        String nachname = leseString("Nachname: ");
        String studiengang = leseString("Studiengang: ");
        
        System.out.print("Geburtsdatum (TT.MM.JJJJ): ");
        String datumStr = scanner.nextLine();
        LocalDate geburtsdatum;
        try {
            geburtsdatum = LocalDate.parse(datumStr, dateFormatter);
        } catch (Exception e) {
            System.out.println("❌ Ungültiges Datum! Student nicht angelegt.");
            return;
        }
        
        Student neuerStudent = new Student(matrikelnummer, vorname, nachname, studiengang, geburtsdatum);
        studentenVerwaltung.hinzufuegen(neuerStudent);
        System.out.println("✅ Student erfolgreich hinzugefügt!");
    }
    
    private static void studentSuchen() {
        System.out.println("\n🔍 STUDENT SUCHEN:");
        System.out.println("1. Nach Name suchen");
        System.out.println("2. Nach Matrikelnummer suchen");
        System.out.println("3. Nach Studiengang suchen");
        
        int wahl = leseInt("Wahl: ");
        
        switch (wahl) {
            case 1 -> {
                String name = leseString("Name (oder Teil): ");
                List<Student> gefunden = studentenVerwaltung.suchenNachName(name);
                zeigeStudentenliste(gefunden);
            }
            case 2 -> {
                String nummer = leseString("Matrikelnummer: ");
                Student student = studentenVerwaltung.findeNachMatrikelnummer(nummer);
                if (student != null) {
                    System.out.println("\nGefunden: " + student);
                    System.out.println("Notendurchschnitt: " + 
                        String.format("%.2f", student.berechneNotendurchschnitt()));
                } else {
                    System.out.println("❌ Student nicht gefunden!");
                }
            }
            case 3 -> {
                String studiengang = leseString("Studiengang: ");
                List<Student> gefunden = studentenVerwaltung.suchenNachStudiengang(studiengang);
                zeigeStudentenliste(gefunden);
            }
        }
    }
    
    private static void studentLoeschen() {
        String matrikelnummer = leseString("Matrikelnummer des zu löschenden Studenten: ");
        if (studentenVerwaltung.loeschen(matrikelnummer)) {
            System.out.println("✅ Student gelöscht!");
        } else {
            System.out.println("❌ Student nicht gefunden!");
        }
    }
    
    private static void klausurenMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- KLAUSURVERWALTUNG ---");
            System.out.println("1. Alle Klausuren anzeigen");
            System.out.println("2. Klausur hinzufügen");
            System.out.println("3. Kommende Klausuren");
            System.out.println("0. Zurück zum Hauptmenü");
            
            int wahl = leseInt("Wahl: ");
            
            switch (wahl) {
                case 1 -> alleKlausurenAnzeigen();
                case 2 -> klausurHinzufuegen();
                case 3 -> kommendeKlausurenAnzeigen();
                case 0 -> inMenu = false;
                default -> System.out.println("❌ Ungültige Eingabe!");
            }
        }
    }
    
    private static void alleKlausurenAnzeigen() {
        System.out.println("\n📋 ALLE KLAUSUREN:");
        System.out.println("─".repeat(80));
        List<Klausur> klausuren = klausurVerwaltung.getAlleSortiert();
        
        if (klausuren.isEmpty()) {
            System.out.println("Keine Klausuren vorhanden.");
        } else {
            for (Klausur k : klausuren) {
                System.out.printf("• [%s] %s\n  Modul: %s | Datum: %s | Raum: %s | Frist: %s\n\n", 
                    k.getId(),
                    k.getTitel(),
                    k.getModul(),
                    k.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    k.getRaum(),
                    k.getAnmeldefrist().format(dateFormatter)
                );
            }
        }
    }
    
    private static void klausurHinzufuegen() {
        System.out.println("\n➕ NEUE KLAUSUR HINZUFÜGEN:");
        
        String id = leseString("Klausur-ID: ");
        String titel = leseString("Titel: ");
        String modul = leseString("Modul: ");
        String raum = leseString("Raum: ");
        
        System.out.print("Datum und Uhrzeit (TT.MM.JJJJ HH:MM): ");
        String datumStr = scanner.nextLine();
        LocalDateTime datum;
        try {
            datum = LocalDateTime.parse(datumStr, 
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (Exception e) {
            System.out.println("❌ Ungültiges Datum!");
            return;
        }
        
        System.out.print("Anmeldefrist (TT.MM.JJJJ): ");
        String fristStr = scanner.nextLine();
        LocalDate frist;
        try {
            frist = LocalDate.parse(fristStr, dateFormatter);
        } catch (Exception e) {
            System.out.println("❌ Ungültiges Datum!");
            return;
        }
        
        int maxVersuche = leseInt("Max. Versuche (Standard: 3): ");
        if (maxVersuche <= 0) maxVersuche = 3;
        
        try {
            Klausur neueKlausur = new Klausur(id, titel, modul, datum, raum, maxVersuche, frist);
            klausurVerwaltung.hinzufuegen(neueKlausur);
            System.out.println("✅ Klausur erfolgreich hinzugefügt!");
        } catch (KlausurKonfliktException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private static void kommendeKlausurenAnzeigen() {
        System.out.println("\n📅 KOMMENDE KLAUSUREN:");
        System.out.println("─".repeat(80));
        List<Klausur> kommende = klausurVerwaltung.getKommendeKlausuren();
        
        if (kommende.isEmpty()) {
            System.out.println("Keine kommenden Klausuren.");
        } else {
            for (Klausur k : kommende) {
                long tage = LocalDate.now().until(k.getDatum().toLocalDate()).getDays();
                System.out.printf("• %s - in %d Tagen\n  %s | Raum: %s\n\n", 
                    k.getTitel(),
                    tage,
                    k.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    k.getRaum()
                );
            }
        }
    }
    
    private static void anmeldungenMenu() {
        System.out.println("\n--- KLAUSURANMELDUNGEN ---");
        System.out.println("1. Student zu Klausur anmelden");
        System.out.println("2. Anmeldungen eines Studenten anzeigen");
        
        int wahl = leseInt("Wahl: ");
        
        switch (wahl) {
            case 1 -> studentZuKlausurAnmelden();
            case 2 -> anmeldungenAnzeigen();
        }
    }
    
    private static void studentZuKlausurAnmelden() {
        String matrikelnummer = leseString("Matrikelnummer: ");
        Student student = studentenVerwaltung.findeNachMatrikelnummer(matrikelnummer);
        
        if (student == null) {
            System.out.println("❌ Student nicht gefunden!");
            return;
        }
        
        System.out.println("Student: " + student);
        
        alleKlausurenAnzeigen();
        String klausurId = leseString("Klausur-ID: ");
        Klausur klausur = klausurVerwaltung.findeNachId(klausurId);
        
        if (klausur == null) {
            System.out.println("❌ Klausur nicht gefunden!");
            return;
        }
        
        try {
            studentenVerwaltung.anmeldenZuKlausur(matrikelnummer, klausur);
            System.out.println("✅ Erfolgreich angemeldet!");
        } catch (FristAbgelaufenException e) {
            System.out.println("❌ Anmeldefrist abgelaufen!");
        } catch (KlausurKonfliktException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private static void anmeldungenAnzeigen() {
        String matrikelnummer = leseString("Matrikelnummer: ");
        Student student = studentenVerwaltung.findeNachMatrikelnummer(matrikelnummer);
        
        if (student == null) {
            System.out.println("❌ Student nicht gefunden!");
            return;
        }
        
        System.out.println("\n📋 Anmeldungen von " + student.getVorname() + " " + student.getNachname() + ":");
        System.out.println(student.zeigePruefungsstatus());
    }
    
    private static void notenMenu() {
        System.out.println("\n--- NOTENVERWALTUNG ---");
        System.out.println("1. Note eintragen");
        System.out.println("2. Notenspiegel eines Studenten");
        
        int wahl = leseInt("Wahl: ");
        
        switch (wahl) {
            case 1 -> noteEintragen();
            case 2 -> notenspiegelAnzeigen();
        }
    }
    
    private static void noteEintragen() {
        String matrikelnummer = leseString("Matrikelnummer: ");
        Student student = studentenVerwaltung.findeNachMatrikelnummer(matrikelnummer);
        
        if (student == null) {
            System.out.println("❌ Student nicht gefunden!");
            return;
        }
        
        System.out.println("Student: " + student);
        System.out.println("\nAngemeldete Klausuren:");
        List<Klausur> angemeldet = student.getAngemeldeteKlausuren();
        for (int i = 0; i < angemeldet.size(); i++) {
            System.out.println((i+1) + ". " + angemeldet.get(i).getTitel());
        }
        
        int index = leseInt("Klausur-Nummer: ") - 1;
        if (index < 0 || index >= angemeldet.size()) {
            System.out.println("❌ Ungültige Auswahl!");
            return;
        }
        
        Klausur klausur = angemeldet.get(index);
        double note = leseDouble("Note (1.0 - 5.0): ");
        
        if (note < 1.0 || note > 5.0) {
            System.out.println("❌ Ungültige Note!");
            return;
        }
        
        studentenVerwaltung.versuchEintragen(matrikelnummer, klausur, note, LocalDate.now());
        System.out.println("✅ Note eingetragen!");
    }
    
    private static void notenspiegelAnzeigen() {
        String matrikelnummer = leseString("Matrikelnummer: ");
        Student student = studentenVerwaltung.findeNachMatrikelnummer(matrikelnummer);
        
        if (student == null) {
            System.out.println("❌ Student nicht gefunden!");
            return;
        }
        
        System.out.println("\n📊 NOTENSPIEGEL - " + student.getVorname() + " " + student.getNachname());
        System.out.println("─".repeat(60));
        
        List<Versuch> versuche = student.getVersuche();
        if (versuche.isEmpty()) {
            System.out.println("Noch keine Noten vorhanden.");
        } else {
            for (Versuch v : versuche) {
                System.out.printf("• %s: %.1f (%s) - %s\n",
                    v.getKlausur().getTitel(),
                    v.getNote(),
                    v.getBewertung(),
                    v.getDatum().format(dateFormatter)
                );
            }
            System.out.printf("\nNotendurchschnitt: %.2f\n", student.berechneNotendurchschnitt());
        }
    }
    
    private static void statistikMenu() {
        System.out.println("\n--- STATISTIKEN ---");
        System.out.println("1. Klausurstatistik");
        System.out.println("2. Studenten mit schlechten Leistungen");
        System.out.println("3. Studiengangstatistik");
        
        int wahl = leseInt("Wahl: ");
        
        switch (wahl) {
            case 1 -> klausurstatistikAnzeigen();
            case 2 -> schlechteLeistungenAnzeigen();
            case 3 -> studiengangstatistikAnzeigen();
        }
    }
    
    private static void klausurstatistikAnzeigen() {
        alleKlausurenAnzeigen();
        String klausurId = leseString("Klausur-ID: ");
        Klausur klausur = klausurVerwaltung.findeNachId(klausurId);
        
        if (klausur == null) {
            System.out.println("❌ Klausur nicht gefunden!");
            return;
        }
        
        VersuchsVerwaltung.KlausurStatistik stats = 
            studentenVerwaltung.getVersuchsVerwaltung().berechneStatistik(klausur);
        
        System.out.println("\n📊 STATISTIK für " + klausur.getTitel());
        System.out.println("─".repeat(50));
        System.out.println(stats);
    }
    
    private static void schlechteLeistungenAnzeigen() {
        System.out.println("\n⚠️ STUDENTEN MIT SCHLECHTEN LEISTUNGEN (Durchschnitt > 3.0):");
        System.out.println("─".repeat(60));
        
        List<Student> schlechte = studentenVerwaltung.getStudentenMitSchlechtenLeistungen();
        if (schlechte.isEmpty()) {
            System.out.println("Keine Studenten mit schlechten Leistungen.");
        } else {
            for (Student s : schlechte) {
                System.out.printf("• %s - Durchschnitt: %.2f\n", s, s.berechneNotendurchschnitt());
            }
        }
    }
    
    private static void studiengangstatistikAnzeigen() {
        System.out.println("\n📊 STUDIENGANGSTATISTIK:");
        System.out.println("─".repeat(50));
        
        // Zähle Studenten pro Studiengang
        List<Student> alle = studentenVerwaltung.getAlleSortiert(
            ErweiterteStudentenVerwaltung.SortierKriterium.STUDIENGANG
        );
        
        String letzterStudiengang = "";
        int count = 0;
        
        for (Student s : alle) {
            if (!s.getStudiengang().equals(letzterStudiengang)) {
                if (count > 0) {
                    System.out.println(letzterStudiengang + ": " + count + " Studenten");
                }
                letzterStudiengang = s.getStudiengang();
                count = 1;
            } else {
                count++;
            }
        }
        if (count > 0) {
            System.out.println(letzterStudiengang + ": " + count + " Studenten");
        }
    }
    
    private static void benachrichtigungsMenu() {
        String matrikelnummer = leseString("Matrikelnummer (oder 'alle' für alle Studenten): ");
        
        if (matrikelnummer.equalsIgnoreCase("alle")) {
            // Erstelle Erinnerungen für alle
            studentenVerwaltung.erstelleAutomatischeErinnerungen(klausurVerwaltung.getAlleSortiert());
            System.out.println("✅ Erinnerungen erstellt!");
        } else {
            Student student = studentenVerwaltung.findeNachMatrikelnummer(matrikelnummer);
            if (student == null) {
                System.out.println("❌ Student nicht gefunden!");
                return;
            }
            
            System.out.println("\n📧 BENACHRICHTIGUNGEN für " + student.getVorname() + " " + student.getNachname());
            System.out.println("─".repeat(70));
            
            List<Benachrichtigung> nachrichten = studentenVerwaltung
                .getBenachrichtigungsVerwaltung()
                .getBenachrichtigungenFuerStudent(student);
                
            if (nachrichten.isEmpty()) {
                System.out.println("Keine Benachrichtigungen vorhanden.");
            } else {
                for (Benachrichtigung b : nachrichten) {
                    System.out.println(b);
                }
            }
        }
    }
    
    // Hilfsmethoden
    private static void zeigeStudentenliste(List<Student> studenten) {
        if (studenten.isEmpty()) {
            System.out.println("Keine Studenten gefunden.");
        } else {
            System.out.println("\nGefunden: " + studenten.size() + " Studenten");
            for (Student s : studenten) {
                System.out.println("• " + s);
            }
        }
    }
    
    private static String leseString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int leseInt(String prompt) {
        System.out.print(prompt);
        try {
            int wert = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            return wert;
        } catch (Exception e) {
            scanner.nextLine(); // Clear buffer
            return -1;
        }
    }
    
    private static double leseDouble(String prompt) {
        System.out.print(prompt);
        try {
            double wert = scanner.nextDouble();
            scanner.nextLine(); // Clear buffer
            return wert;
        } catch (Exception e) {
            scanner.nextLine(); // Clear buffer
            return -1;
        }
    }
    
    private static void ladeBeisspieldaten() {
        try {
            // Beispiel-Studenten
            studentenVerwaltung.hinzufuegen(new Student("10001", "Max", "Mustermann", 
                "Informatik", LocalDate.of(2000, 5, 15)));
            studentenVerwaltung.hinzufuegen(new Student("10002", "Anna", "Schmidt", 
                "BWL", LocalDate.of(1999, 8, 22)));
            studentenVerwaltung.hinzufuegen(new Student("10003", "Tom", "Weber", 
                "Informatik", LocalDate.of(2001, 3, 10)));
            studentenVerwaltung.hinzufuegen(new Student("10004", "Lisa", "Müller", 
                "Mathematik", LocalDate.of(2000, 11, 5)));
            
            // Beispiel-Klausuren
            klausurVerwaltung.hinzufuegen(new Klausur("OOP2025", "Objektorientierte Programmierung", 
                "Informatik Grundlagen", LocalDateTime.of(2025, 7, 15, 10, 0), 
                "H1", 3, LocalDate.of(2025, 7, 1)));
            
            klausurVerwaltung.hinzufuegen(new Klausur("MATH1", "Mathematik I", 
                "Grundlagen", LocalDateTime.of(2025, 7, 20, 14, 0), 
                "A101", 3, LocalDate.of(2025, 7, 5)));
            
            klausurVerwaltung.hinzufuegen(new Klausur("BWL1", "Grundlagen BWL", 
                "Betriebswirtschaft", LocalDateTime.of(2025, 7, 18, 9, 0), 
                "B205", 3, LocalDate.of(2025, 7, 3)));
            
            System.out.println("✅ Beispieldaten geladen!");
            
        } catch (Exception e) {
            System.out.println("⚠️ Fehler beim Laden der Beispieldaten: " + e.getMessage());
        }
    }
}
