package nardiff.mt

class Narrative {

    Narrative parent_narrative;
    int depth = 0
    String assignmentId
    String workerId
    Date opened
    Date closed
    Integer time_reading;
    Integer time_distrator;
    Integer time_writing;
    String text;
    Boolean too_simple = false
    Boolean abandoned = false
    Boolean expanding = false
    String distractor_answer
    int stage = 0


    static belongsTo = [root_narrative: NarrativeSeed]

    static hasMany = [children:Narrative]

    static constraints = {
        root_narrative nullable:true
        parent_narrative nullable:true
        assignmentId nullable:true
        distractor_answer nullable:true
        time_reading nullable:true
        time_distrator nullable:true
        time_writing nullable:true
        workerId nullable:true
        text nullable:true
        opened nullable:true
        closed nullable:true

    }





    static mapping = {
        text type: "text"

    }

    public Narrative(NarrativeSeed seed) {
        seed.addToNarratives(this)
        this.depth = 0
        this.opened = new Date()
        this.closed = new Date()
        this.text = seed.text
        this.expanding = true
    }

    public Narrative(Narrative parent, String assignmentId, String workerId) {
        //this.parent_narrative = parent
        this.depth = parent.depth+1
        this.assignmentId = assignmentId
        this.workerId = workerId
        this.opened = new Date()

    }



}
