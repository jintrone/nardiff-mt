package nardiff.mt

class Turker {

    String mturk_id;
    Integer age;
    String gender;
    Integer education;

    static constraints = {
        age nullable: true
        gender nullable: true
        education nullable: true
    }
}
