import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class VocabularyTrainer {
    private ArrayList<Stage> stages;

    VocabularyTrainer(ArrayList<Stage> stages) {
        this.stages = stages;
    }

    ArrayList<Stage> getStages() {
        return stages;
    }

    private void setStages(ArrayList<Stage> stages) {
        this.stages = stages;
    }

    /**
     * Exercise vocabulary
     */
    void exercise() {

        if (isEmpty()) {
            System.out.println("Ihre Vokabelliste ist leer. Sie können nichts üben.\n");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int choice, correct = 0, incorrect = 0;
        Date currentDate = new Date();

        // Create copy of the orginal vocabularyTrainer to make changes on it when answer was correct/incorrect
        Gson gson = new Gson();
        VocabularyTrainer copyOfVocabularyTrainer = gson.fromJson(gson.toJson(this), VocabularyTrainer.class);

        System.out.println("Wählen Sie eine Richtung in die Sie üben möchten");
        System.out.println("0) Muttersprache --> Fremdsprache");
        System.out.println("1) Fremdsprache --> Muttersprache");

        do {
            choice = getInput(scanner);
        } while (choice != 0 && choice != 1);

        System.out.println("Das Üben der Vokabeln beginnt jetzt:\n");

        // Iterate through all stages
        for (int i = 0; i < stages.size(); i++) {

            // Skip stage if it doesn't contain any word
            if (stages.get(i).getWords().isEmpty()) {
                System.out.println("Die Stufe " + stages.get(i).getName() + " ist leer.\n");
                continue;
            }

            // Get time difference between today and the last time practiced date and calculate the day difference afterwards
            long timeDiff = currentDate.getTime() - stages.get(i).getLastTimePracticed().getTime();
            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

            // Skip stage if the time difference is too low (weekly < 7, monthly < 30)
            if ((i == 1 && daysDiff < 7) || (i == 2 && daysDiff < 30)) {
                System.out.println("Die Stufe " + stages.get(i).getName() + " wurde bereits geübt.\n");
                continue;
            }

            System.out.println("\nEs werden die Wörter der Stufe " + stages.get(i).getName() + " geübt.\n");

            // Iterate through all words in stage
            for (int k = 0; k < stages.get(i).getWords().size(); k++) {

                Stage stage = stages.get(i);
                Word word = stages.get(i).getWords().get(k);

                // Create copy of the word
                Word copyWord = getWord(copyOfVocabularyTrainer, i, word);

                // Show mother or foreignLanguage depending on choice earlier
                if (choice == 0) {
                    System.out.println(word.getMotherLanguage());
                } else {
                    System.out.println(word.getForeignLanguage());
                }

                // Ask for answer
                System.out.println("--------------------------------");
                System.out.println("Antwort: ");
                String answer = scanner.next();

                int stageIndex = stages.indexOf(stage);
                int lastStageIndex = stages.size() - 1;

                // motherLanguage --> foreignLanguage has been chosen
                if (choice == 0) {

                    // Answer is correct
                    if (answer.equals(word.getForeignLanguage())) {
                        System.out.println("Antwort korrekt\n");
                        correct++;

                        // Remove the word from current stage
                        copyOfVocabularyTrainer.stages.get(stageIndex).getWords().remove(copyWord);

                        // If the word isn't on the last stage then add it to the next one
                        if ((stageIndex + 1) <= lastStageIndex) {
                            copyOfVocabularyTrainer.stages.get(stageIndex + 1).getWords().add(copyWord);
                        }

                        // Answer is incorrect
                    } else {
                        System.out.println("Antwort falsch\n");
                        System.out.println("Richtig wäre: " + word.getForeignLanguage());
                        System.out.println("Das Wort wurde wieder zum " + stages.get(0).getName() + " Stapel zurück gestellt.\n");

                        // Remove the word from current stage
                        copyOfVocabularyTrainer.getStages().get(stageIndex).getWords().remove(copyWord);

                        // Add the word to the first stage
                        copyOfVocabularyTrainer.getStages().get(0).getWords().add(copyWord);

                        incorrect++;
                    }
                }

                // foreignLanguage --> motherLanguage has been chosen
                if (choice == 1) {
                    if (answer.equals(word.getMotherLanguage())) {
                        System.out.println("Antwort korrekt\n");
                        correct++;

                        // Remove the word from current stage
                        copyOfVocabularyTrainer.stages.get(stageIndex).getWords().remove(copyWord);

                        // If the word isn't on the last stage then add it to the next one
                        if ((stageIndex + 1) <= lastStageIndex) {
                            copyOfVocabularyTrainer.stages.get(stageIndex + 1).getWords().add(copyWord);
                        }

                        // Answer is incorrect
                    } else {
                        System.out.println("Antwort falsch");
                        System.out.println("Richtig wäre: " + word.getMotherLanguage());
                        System.out.println("Das Wort wurde wieder zum " + stages.get(0).getName() + " Stapel zurück gestellt.\n");

                        // Remove the word from current stage
                        copyOfVocabularyTrainer.getStages().get(stageIndex).getWords().remove(copyWord);
                        // Add the word to the first stage
                        copyOfVocabularyTrainer.getStages().get(0).getWords().add(copyWord);

                        incorrect++;
                    }
                }
            }
            stages.get(i).setLastTimePracticed(new Date());
        }

        // Update the stages by setting them from the original vocabularyTrainer with the changed stages
        this.setStages(copyOfVocabularyTrainer.getStages());

        System.out.println("--------------------------------");
        System.out.println("Sie haben " + correct + " richtige Wörter");
        System.out.println("Sie haben " + incorrect + " falsche Wörter\n");
    }

    /**
     * Get specific word from stage
     *
     * @param copyOfVocabularyTrainer copy of the original vocabularyTrainer for changes when a word has been answered correct/incorrect.
     * @param index                   of the word
     * @param word                    which is searched for
     * @return word which has been found
     */
    private Word getWord(VocabularyTrainer copyOfVocabularyTrainer, int index, Word word) {
        Word copyWord = null;

        // Iterate through stage
        for (Word wordElement : copyOfVocabularyTrainer.stages.get(index).getWords()) {

            // Check if word is found by comparing the motherLanguage and foreignLanguage word
            if (wordElement.getMotherLanguage().equals(word.getMotherLanguage()) && wordElement.getForeignLanguage().equals(word.getForeignLanguage())) {
                copyWord = wordElement;
                break;
            }
        }
        return copyWord;
    }

    /**
     * List all vocabulary words
     */
    void listVocabulary() {

        if (isEmpty()) {
            System.out.println("Ihre Vokabelliste ist leer.\n");
            return;
        }

        for (Stage stage : stages) {

            if (stage.getWords().isEmpty()) {
                continue;
            }

            System.out.println("\nStufe: " + stage.getName());

            for (Word word : stage.getWords()) {
                System.out.println(word.getMotherLanguage() + " | " + word.getForeignLanguage());
            }
        }
        System.out.println();
    }

    /**
     * Add word to stage
     */
    void addWord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wie heisst das Wort in Ihrer Muttersprache?");
        String motherLanguage = scanner.nextLine();
        System.out.println("Wie heisst das Wort in der Fremdsprache?");
        String foreignLanguage = scanner.nextLine();

        this.stages.get(0).getWords().add(new Word(motherLanguage, foreignLanguage));

        System.out.println("Das Wort wurde erfolgreich hinzugefügt.\n");
    }

    /**
     * Remove word from stage
     */
    void removeWord() {

        if (isEmpty()) {
            System.out.println("Ihre Vokabelliste ist leer. Sie können nichts löschen.\n");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        int input;
        int stageId;
        int wordId;
        boolean wordExists;

        do {
            System.out.println("Welches Wort möchten Sie löschen?");

            printWords();
            input = getInput(scanner);

            wordId = input % 10;
            stageId = input / 10;

            try {
                stages.get(stageId).getWords().remove(wordId);
                wordExists = true;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Ein Wort mit dieser ID existiert nicht.\n");
                wordExists = false;
            }

        } while (!wordExists);

        System.out.println("Wort erfolgreich gelöscht.\n");
    }

    /**
     * Get user input
     *
     * @param scanner Is used to get user input
     * @return User input as a String
     */
    private int getInput(Scanner scanner) {
        int input;
        try {
            input = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Eingabe ungültig");
            input = -1;
            scanner.next();
        }
        return input;
    }

    /**
     * Save the vocabularyTrainer as a json file
     */
    void save() {
        Gson gson = new Gson();
        String json = gson.toJson(this);

        try {
            FileWriter file = new FileWriter("vocabulary.json");
            file.write(json);
            file.close();
        } catch (IOException e) {
            System.out.println("Beim Speichern ist ein Fehler aufgetreten.\n");
        }

        System.out.println("Die Vokabeln wurden erfolgreich gespeichert unter dem Namen: " + "vocabulary.json\n");
    }

    /**
     * Load the vocabularyTrainer from a json file
     */
    void load() {
        byte[] encoded;
        Gson gson = new Gson();
        File file = new File("vocabulary.json");

        if (!file.exists() || file.isDirectory()) {
            System.out.println("Es existiert keine Datei mit dem Namen " + "vocabulary.json" + " zum Laden.\n");
            return;
        }

        try {
            encoded = Files.readAllBytes(Paths.get("vocabulary.json"));
        } catch (IOException e) {
            System.out.println("Beim Laden ist ein Fehler aufgetreten.\n");
            return;
        }

        String vocabularyTrainerJson = new String(encoded, StandardCharsets.UTF_8);
        VocabularyTrainer vocabularyTrainer = gson.fromJson(vocabularyTrainerJson, VocabularyTrainer.class);
        stages = vocabularyTrainer.getStages();
        System.out.println("Vokabeln erfolgreich geladen.\n");
    }

    /**
     * Check if stage is empty
     *
     * @return true or false (boolean)
     */
    private boolean isEmpty() {
        for (Stage stage : stages) {
            if (!stage.getWords().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print all words with an index to let user choose which word to delete
     */
    private void printWords() {
        if (isEmpty()) {
            System.out.println("Ihre Vokabelliste ist leer. Sie können nichts löschen.\n");
            return;
        }

        System.out.println("Ihre Vokabeln:");

        for (int i = 0; i < this.getStages().size(); i++) {
            if (stages.get(i).getWords().isEmpty()) {
                continue;
            }

            System.out.println(stages.get(i).getName() + ":");

            for (int k = 0; k < this.getStages().get(i).getWords().size(); k++) {
                System.out.println("ID: " + i + k + ") " +
                        stages.get(i).getWords().get(k).getMotherLanguage() + " | " +
                        stages.get(i).getWords().get(k).getForeignLanguage());
            }
            System.out.println("--------------------------------");
        }
    }
}
