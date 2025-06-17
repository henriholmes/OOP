package verwaltung;

import model.Student;
import util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentenVerwaltung {

    public void hinzufuegen(Student s) {
        String sql = "INSERT INTO student (matrikelnummer, vorname, nachname, studiengang) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getMatrikelnummer());
            pstmt.setString(2, s.getVorname());
            pstmt.setString(3, s.getNachname());
            pstmt.setString(4, s.getStudiengang());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fehler beim Hinzufügen: " + e.getMessage());
        }
    }

    public void loeschen(String matrikelnummer) {
        String sql = "DELETE FROM student WHERE matrikelnummer = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matrikelnummer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fehler beim Löschen: " + e.getMessage());
        }
    }

    public List<Student> suchenNachName(String nachname) {
        List<Student> ergebnisse = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE nachname LIKE ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.setString(1, "%" + nachname + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ergebnisse.add(new Student(
                    rs.getString("matrikelnummer"),
                    rs.getString("vorname"),
                    rs.getString("nachname"),
                    rs.getString("studiengang")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei der Suche: " + e.getMessage());
        }
        return ergebnisse;
    }

    public void alleAnzeigen() {
        String sql = "SELECT * FROM student";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(
                    rs.getString("matrikelnummer") + ": " +
                    rs.getString("vorname") + " " +
                    rs.getString("nachname") + " (" +
                    rs.getString("studiengang") + ")"
                );
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Anzeigen: " + e.getMessage());
        }
    }
}