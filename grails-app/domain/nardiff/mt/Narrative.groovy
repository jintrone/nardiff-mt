package nardiff.mt

class Narrative {

    Integer root_narrative_id;
    Narrative parent_narrative;
    Turker completed_by;
    String distractor_answer;
    Integer time_reading;
    Integer time_distrator;
    Integer time_writing;
    String text;
    String distractorTask;
    Boolean too_simple;


    static constraints = {
        root_narrative_id nullable:true
        parent_narrative nullable:true
        completed_by nullable:true
        distractor_answer nullable:true
        time_reading nullable:true
        time_distrator nullable:true
        time_writing nullable:true
    }

    static mapping = {
        text type: "text"
        distractorTask type: "text"
        root_narrative_id index: "root_narrative_id_idx"
    }



}
