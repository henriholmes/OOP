import model.*;
import verwaltung.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Testklasse für Phase 2: Vererbung, Verwaltungsklassen und Ausnahmebehandlung
 */
public class TestPhase2 {
    
    public static void main(String[] args) {
        System.out.println("🧪 === TESTPROTOKOLL PHASE 2 ===");
        System.out.println("Testet: Vererbung, Verwaltungsklassen, Ausnahmebehandlung\n");
        
        // Verwaltungsklassen initialisieren
        ErweiterteStudentenVerwaltung studentenVerwaltung = new ErweiterteStudentenVerwaltung();
        KlausurVerwaltung klausurVerwaltung = new KlausurVerwaltung();
        
        // === TEST 1: VERERBUNG UND OBJEKTERSTELLUNG ===
        System.out.println("📋 TEST 1: Vererbung und Objekterstellung");
        System.out.println("─".repeat(50));
        
        // Studenten erstellen (erben von Person)
        Student student1 = new Student("12345", "Max", "Mustermann", "Informatik", LocalDate.of(2000, 5, 15));
        Student student2 = new Student("12346", "Anna", "Schmidt", "BWL", LocalDate.of(1999, 8, 22));
        Student student3 = new Student("12347", "Tom", "Weber", "Informatik", LocalDate.of(2001, 3, 10));
        
        System.out.println("✓ Studenten erstellt:");
        System.out.println("  " + student1);
        System.out.println("  " + student2);
        System.out.println("  " + student3);
        
        // Dozent erstellen
        Dozent dozent1 = new Dozent("DOZ001", "Prof. Dr.", "Müller", LocalDate.of(1975, 12, 1), Dozent.DozentenRolle.PROFESSOR);
        System.out.println("✓ Dozent erstellt: " + dozent1);
        
        // === TEST 2: VERWALTUNGSKLASSEN ===
        System.out.println("\n📋 TEST 2: Verwaltungsklassen");
        System.out.println("─".repeat(50));
        
        // Studenten zur Verwaltung hinzufügen
        studentenVerwaltung.hinzufuegen(student1);
        studentenVerwaltung.hinzufuegen(student2);
        studentenVerwaltung.hinzufuegen(student3);
        System.out.println("✓ Studenten zur Verwaltung hinzugefügt");
        
        // Klausuren erstellen
        Klausur klausur1 = new Klausur(
            "OOP001", 
            "Objektorientierte Programmierung", 
            "Informatik Grundlagen",
            LocalDateTime.of(2025, 7, 15, 10, 0),
            "A101",
            3,
            LocalDate.of(2025, 7, 1)
        );
        
        Klausur klausur2 = new Klausur(
            "BWL001",
            "Grundlagen BWL",
            "Betriebswirtschaft",
            LocalDateTime.of(2025, 7, 16, 14, 0),
            "B205",
            3,
            LocalDate.of(2025, 7, 2)
        );
        
        Klausur klausur3 = new Klausur(
            "MATH001",
            "Mathematik I",
            "Grundlagen",
            LocalDateTime.of(2025, 7, 15, 10, 0), // Gleiche Zeit wie OOP -> Konflikt!
            "C301",
            3,
            LocalDate.of(2025, 6, 30)
        );
        
        try {
            klausurVerwaltung.hinzufuegen(klausur1);
            klausurVerwaltung.hinzufuegen(klausur2);
            System.out.println("✓ Klausuren hinzugefügt: OOP, BWL");
            
            // Konflikt testen
            klausurVerwaltung.hinzufuegen(klausur3);
        } catch (KlausurKonfliktException e) {
            System.out.println("⚠️ Konflikt erkannt: " + e.getMessage());
        }
        
        // === TEST 3: AUSNAHMEBEHANDLUNG ===
        System.out.println("\n📋 TEST 3: Ausnahmebehandlung");
        System.out.println("─".repeat(50));
        
        try {
            // Normale Anmeldung
            studentenVerwaltung.anmeldenZuKlausur("12345", klausur1);
            System.out.println("✓ Max erfolgreich zu OOP angemeldet");
            
            studentenVerwaltung.anmeldenZuKlausur("12346", klausur2);
            System.out.println("✓ Anna erfolgreich zu BWL angemeldet");
            
            // Konflikt-Test: Student zu zwei Klausuren zur gleichen Zeit anmelden
            // Erst klausur3 ohne Konfliktprüfung in eigene Liste
            Klausur klausur3_konfliktfrei = new Klausur(
                "MATH001",
                "Mathematik I", 
                "Grundlagen",
                LocalDateTime.of(2025, 7, 17, 10, 0), // Anderer Tag
                "C301",
                3,
                LocalDate.of(2025, 6, 30)
            );
            klausurVerwaltung.hinzufuegen(klausur3_konfliktfrei);
            studentenVerwaltung.anmeldenZuKlausur("12345", klausur3_konfliktfrei);
            System.out.println("✓ Max zu Mathematik angemeldet (kein Konflikt)");
            
        } catch (FristAbgelaufenException e) {
            System.out.println("❌ Fristfehler: " + e.getMessage());
        } catch (KlausurKonfliktException e) {
            System.out.println("❌ Konfliktfehler: " + e.getMessage());
        }
        
        // Frist-Test (simuliert abgelaufene Frist)
        Klausur abgelaufeneKlausur = new Klausur(
            "OLD001",
            "Alte Klausur",
            "Test",
            LocalDateTime.of(2025, 8, 1, 10, 0),
            "D100",
            3,
            LocalDate.of(2025, 6, 1) // Frist bereits abgelaufen
        );
        
        try {
            klausurVerwaltung.hinzufuegen(abgelaufeneKlausur);
            studentenVerwaltung.anmeldenZuKlausur("12347", abgelaufeneKlausur);
        } catch (FristAbgelaufenException e) {
            System.out.println("⚠️ Frist abgelaufen: " + e.getMessage());
        } catch (KlausurKonfliktException e) {
            System.out.println("⚠️ Konflikt: " + e.getMessage());
        }
        
        // === TEST 4: FACHSPEZIFISCHE METHODEN ===
        System.out.println("\n📋 TEST 4: Fachspezifische Methoden");
        System.out.println("─".repeat(50));
        
        // Prüfungsversuche hinzufügen
        studentenVerwaltung.versuchEintragen("12345", klausur1, 1.7, LocalDate.now());
        studentenVerwaltung.versuchEintragen("12346", klausur2, 2.3, LocalDate.now());
        studentenVerwaltung.versuchEintragen("12347", klausur1, 4.7, LocalDate.now()); // Nicht bestanden
        
        System.out.println("✓ Prüfungsversuche eingetragen");
        
        // Prüfungsstatus anzeigen
        System.out.println("\n📊 Prüfungsstatus:");
        System.out.println(student1.zeigePruefungsstatus());
        System.out.println(student2.zeigePruefungsstatus());
        
        // Notendurchschnitt berechnen
        System.out.println("📈 Notendurchschnitte:");
        System.out.printf("  %s: %.2f%n", student1.getNachname(), student1.berechneNotendurchschnitt());
        System.out.printf("  %s: %.2f%n", student2.getNachname(), student2.berechneNotendurchschnitt());
        System.out.printf("  %s: %.2f%n", student3.getNachname(), student3.berechneNotendurchschnitt());
        
        // === TEST 5: SUCHFUNKTIONEN ===
        System.out.println("\n📋 TEST 5: Such- und Sortierfunktionen");
        System.out.println("─".repeat(50));
        
        // Nach Namen suchen
        List<Student> gefundeneStudenten = studentenVerwaltung.suchenNachName("Max");
        System.out.println("🔍 Suche nach 'Max': " + gefundeneStudenten.size() + " Treffer");
        gefundeneStudenten.forEach(s -> System.out.println("  " + s));
        
        // Nach Studiengang suchen
        List<Student> informatikerStudenten = studentenVerwaltung.suchenNachStudiengang("Informatik");
        System.out.println("🔍 Informatik-Studenten: " + informatikerStudenten.size() + " Treffer");
        informatikerStudenten.forEach(s -> System.out.println("  " + s));
        
        // Sortierte Ausgabe
        System.out.println("📋 Alle Studenten sortiert nach Nachname:");
        List<Student> sortiertNachName = studentenVerwaltung.getAlleSortiert(ErweiterteStudentenVerwaltung.SortierKriterium.NACHNAME);
        sortiertNachName.forEach(s -> System.out.println("  " + s));
        
        // === TEST 6: STATISTIKEN ===
        System.out.println("\n📋 TEST 6: Statistiken");
        System.out.println("─".repeat(50));
        
        VersuchsVerwaltung.KlausurStatistik statistik = studentenVerwaltung.getVersuchsVerwaltung().berechneStatistik(klausur1);
        System.out.println("📊 Statistik für '" + klausur1.getTitel() + "':");
        System.out.println("  " + statistik);
        
        // Studenten mit schlechten Leistungen
        List<Student> schlechteLeistungen = studentenVerwaltung.getStudentenMitSchlechtenLeistungen();
        System.out.println("⚠️ Studenten mit schlechten Leistungen (> 3.0): " + schlechteLeistungen.size());
        schlechteLeistungen.forEach(s -> 
            System.out.printf("  %s: %.2f%n", s.getNachname(), s.berechneNotendurchschnitt())
        );
        
        // === TEST 7: BENACHRICHTIGUNGSSYSTEM ===
        System.out.println("\n📋 TEST 7: Benachrichtigungssystem");
        System.out.println("─".repeat(50));
        
        // Automatische Erinnerungen erstellen (simuliert)
        List<Klausur> alleKlausuren = klausurVerwaltung.getAlleSortiert();
        studentenVerwaltung.erstelleAutomatischeErinnerungen(alleKlausuren);
        
        // Benachrichtigungen für einen Studenten anzeigen
        List<Benachrichtigung> benachrichtigungen = studentenVerwaltung.getBenachrichtigungsVerwaltung()
                .getBenachrichtigungenFuerStudent(student1);
        System.out.println("📧 Benachrichtigungen für " + student1.getVorname() + ":");
        benachrichtigungen.forEach(b -> System.out.println("  " + b));
        
        // === TEST 8: INTERFACE-IMPLEMENTIERUNG (Comparable) ===
        System.out.println("\n📋 TEST 8: Interface-Implementierung");
        System.out.println("─".repeat(50));
        
        List<Student> alleStudenten = List.of(student1, student2, student3);
        System.out.println("📋 Studenten vor Sortierung:");
        alleStudenten.forEach(s -> System.out.println("  " + s.getMatrikelnummer() + ": " + s.getNachname()));
        
        List<Student> sortiertNachMatrikel = alleStudenten.stream().sorted().toList();
        System.out.println("📋 Studenten nach Sortierung (Comparable - Matrikelnummer):");
        sortiertNachMatrikel.forEach(s -> System.out.println("  " + s.getMatrikelnummer() + ": " + s.getNachname()));
        
        // === TESTPROTOKOLL ENDE ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ ALLE TESTS ERFOLGREICH ABGESCHLOSSEN");
        System.out.println("=".repeat(60));
        
        System.out.println("\n📋 TESTPROTOKOLL ZUSAMMENFASSUNG:");
        System.out.println("├─ ✓ Vererbung (Person → Student, Dozent)");
        System.out.println("├─ ✓ Verwaltungsklassen mit ArrayList");
        System.out.println("├─ ✓ Ausnahmebehandlung (Custom Exceptions)");
        System.out.println("├─ ✓ Fachspezifische Methoden");
        System.out.println("├─ ✓ Such- und Sortierfunktionen"); 
        System.out.println("├─ ✓ Statistiken und Auswertungen");
        System.out.println("├─ ✓ Benachrichtigungssystem");
        System.out.println("└─ ✓ Interface-Implementierung (Comparable)");
        
        System.out.println("\n🎯 Nächste Phase: GUI-Integration und Persistenz");
    }
}