import java.util.ArrayList;
import java.util.Date;

class Stage {

    private String name;
    private ArrayList<Word> words;
    private Date lastTimePracticed;

    Stage(String name, ArrayList<Word> words, Date lastTimePracticed) {
        this.name = name;
        this.words = words;
        this.lastTimePracticed = lastTimePracticed;
    }

    String getName() {
        return name;
    }

    ArrayList<Word> getWords() {
        return words;
    }

    Date getLastTimePracticed() {
        return lastTimePracticed;
    }

    void setLastTimePracticed(Date lastTimePracticed) {
        this.lastTimePracticed = lastTimePracticed;
    }
}
