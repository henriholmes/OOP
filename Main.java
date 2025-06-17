import model.Student;
import verwaltung.StudentenVerwaltung;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        StudentenVerwaltung verwaltung = new StudentenVerwaltung();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("1) Student hinzufügen");
            System.out.println("2) Alle anzeigen");
            System.out.println("3) Nach Nachname suchen");
            System.out.println("4) Student löschen");
            System.out.println("5) Beenden");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    System.out.print("Matrikelnummer: ");
                    String m = sc.nextLine();
                    System.out.print("Vorname: ");
                    String v = sc.nextLine();
                    System.out.print("Nachname: ");
                    String n = sc.nextLine();
                    System.out.print("Studiengang: ");
                    String s = sc.nextLine();
                    verwaltung.hinzufuegen(new Student(m, v, n, s));
                    break;
                case "2":
                    verwaltung.alleAnzeigen();
                    break;
                case "3":
                    System.out.print("Nachname: ");
                    String name = sc.nextLine();
                    List<Student> gefunden = verwaltung.suchenNachName(name);
                    gefunden.forEach(System.out::println);
                    break;
                case "4":
                    System.out.print("Matrikelnummer: ");
                    String del = sc.nextLine();
                    verwaltung.loeschen(del);
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Ungültige Eingabe.");
            }
        }

        sc.close();
    }
}