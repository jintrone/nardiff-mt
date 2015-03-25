package nardiff.mt

class NarrativeRequest {

    Narrative root_narrative;
    Narrative parent_narrative;
    Date when_assigned;
    Turker assigned_to;
    Date when_completed;
    Integer priority;
    Integer depth;

    static constraints = {
        when_assigned nullable: true
        assigned_to nullable: true
        when_completed nullable: true
    }

    static mapping = {
        root_narrative index: "root_narrative_idx"
        priority index: "priority_idx"
        assigned_to index: "assigned_to_idx"
        when_assigned index: "when_assigned_idx"
    }
}
