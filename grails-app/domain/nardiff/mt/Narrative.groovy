package nardiff.mt


class Narrative {

    Narrative parent_narrative;
    int depth = 0
    String assignmentId
    String workerId
    Date opened
    Date closed

    Boolean too_simple = false
    Boolean abandoned = false





    static belongsTo = [root_narrative: NarrativeSeed]

    static hasMany = [children:Narrative]

    static constraints = {
        root_narrative nullable:true
        parent_narrative nullable:true
        assignmentId nullable:true
        workerId nullable:true
        opened nullable:true
        closed nullable:true

    }





    public Narrative(NarrativeSeed seed) {
        seed.addToNarratives(this)
        this.depth = 0
        this.opened = new Date()
        this.closed = new Date()

    }

    public Narrative(Narrative parent, String assignmentId, String workerId) {
        this.depth = parent.depth+1
        this.assignmentId = assignmentId
        this.workerId = workerId
        this.opened = new Date()

    }


    public NarrativeData getData() {
        NarrativeData data = NarrativeData.findByNarrativeId(this.id)
        if (!data) {
            NarrativeData.withTransaction {
                data = new NarrativeData(this.id)
                data.save([flush:true,failOnError: true])
            }
        }
        data
    }





}
