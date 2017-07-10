package edu.msu.mi.gwurk

/**
 * Created by josh on 6/22/17.
 */
class ActiveHits {


    static constraints = {hits nullable: true}

    static belongsTo = [taskRunner:TaskRun]
    static hasMany = [hits:HitView]

}
