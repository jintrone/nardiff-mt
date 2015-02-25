package nardiff.mt

class NarrativeRequest {

    Narrative root_narrative;
    Narrative parent_narrative;
    Date when_assigned;
    Turker assigned_to;
    Date when_completed;
    Integer priority;

    static constraints = {
        when_assigned nullable: true
        assigned_to nullable: true
        when_completed nullable: true


    }
}
