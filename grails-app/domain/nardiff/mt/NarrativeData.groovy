package nardiff.mt

class NarrativeData {


    Long narrativeId
    Integer time_reading;
    Integer time_distrator;
    Integer time_writing;
    String text;
    int stage = 0
    boolean fresh = false
    String distractor_answer

    static constraints = {

        text nullable:true
        time_reading nullable:true
        time_distrator nullable:true
        time_writing nullable:true
        text nullable:true
        distractor_answer nullable:true

    }

    static mapping = {
        text type: "text"
    }

    public NarrativeData(Long narrativeId) {
        this.narrativeId = narrativeId
    }
}
