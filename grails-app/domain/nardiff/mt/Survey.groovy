package nardiff.mt

class Survey {

    String mturk_id
    Long narrativeId
    Integer tx1, tx2, tx3, txc, tx4, tx5
    Integer if1,if2,if3,ifc,if4,if5,if6
    Integer sh1,sh2,sh3
    Integer action1_0,action1_1,action2_0,action2_1

    static constraints = {
        tx1 nullable: true
        tx2 nullable: true
        tx3 nullable: true
        txc nullable: true
        tx4 nullable: true
        tx5 nullable: true

        if1 nullable: true
        if2 nullable: true
        if3 nullable: true
        if4 nullable: true
        ifc nullable: true
        if5 nullable: true
        if6 nullable: true

        sh1 nullable: true
        sh2 nullable: true
        sh3 nullable: true

        action1_0 nullable: true
        action1_1 nullable: true
        action2_0 nullable: true
        action2_1 nullable: true



    }
}
