package nardiff.mt

class Narrative {

    Narrative root_narrative;
    Narrative parent_narrative;
    int depth = 0
    String assignmentId
    String distractor_answer;
    Integer time_reading;
    Integer time_distrator;
    Integer time_writing;
    String text;
    String distractorTask;
    Boolean too_simple = false;


    static constraints = {
        root_narrative nullable:true
        parent_narrative nullable:true
        assignmentId nullable:true
        distractorTask nullable: true
        distractor_answer nullable:true
        time_reading nullable:true
        time_distrator nullable:true
        time_writing nullable:true

    }

    static mapping = {
        text type: "text"
        distractorTask type: "text"
    }


    public Narrative(Narrative parent, String assignmentId) {
        this.root_narrative = parent.root_narrative
        this.parent_narrative = parent
        this.depth = parent.depth+1
        this.assignmentId = assignmentId

    }



}
