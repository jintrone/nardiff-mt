package nardiff.mt

class NarrativeSeed {


    String distractorTask
    String text
    String title
    String survey


    static hasMany = [narratives: Narrative]

    static mapping = {
        text type: "text"
        survey type: "text"
        distractorTask type: "text"
    }
}
