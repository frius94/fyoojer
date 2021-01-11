import java.util.*;

public class Main {

    public static void main(String[] args) {

        VocabularyTrainer vocabularyTrainer = new VocabularyTrainer(new ArrayList<>());

        vocabularyTrainer.getStages().add(new Stage("Täglich", new ArrayList<>(), new Date()));
        vocabularyTrainer.getStages().add(new Stage("Wöchentlich", new ArrayList<>(), new Date()));
        vocabularyTrainer.getStages().add(new Stage("Monatlich", new ArrayList<>(), new Date()));

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        System.out.println("Willkommen zum Vokabeltrainer Programm");

        do {
            System.out.println("Was möchten Sie tun?\n");
            printOptions();

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.next();
            }

            switch (choice) {
                case 1:
                    vocabularyTrainer.exercise();
                    break;
                case 2:
                    vocabularyTrainer.listVocabulary();
                    break;
                case 3:
                    vocabularyTrainer.addWord();
                    break;
                case 4:
                    vocabularyTrainer.removeWord();
                    break;
                case 5:
                    vocabularyTrainer.save();
                    break;
                case 6:
                    vocabularyTrainer.load();
                    break;
                case 7:
                    System.out.println("Das Programm wird jetzt beendet.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Ihre Eingabe war ungültig.\n");
                    break;
            }
        } while (true);
    }

    private static void printOptions() {
        System.out.println("1) Vokabeln üben");
        System.out.println("2) Vokabeln anzeigen");
        System.out.println("3) Vokabel hinzufügen");
        System.out.println("4) Vokabel entfernen");
        System.out.println("5) Vokabelliste abspeichern");
        System.out.println("6) Vokabelliste laden");
        System.out.println("7) Programm beenden\n");
    }
}
