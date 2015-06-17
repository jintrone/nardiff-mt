package nardiff.mt

class NarrativeSeed {


    String distractorTask
    String text
    String title


    static hasMany = [narratives: Narrative]

    static mapping = {
        text type: "text"
        distractorTask type: "text"
    }
}
