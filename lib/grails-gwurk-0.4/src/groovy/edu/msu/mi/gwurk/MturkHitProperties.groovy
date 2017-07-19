package edu.msu.mi.gwurk

import com.amazonaws.services.mturk.model.QualificationRequirement


/**
 * Created by josh on 2/19/14.
 */
class MturkHitProperties {

    String annotation
    long assignmentDuration
    long lifetime
    long autoApprovalDelay
    String description
    String keywords
    int maxAssignments
    double rewardAmount
    String title
    QualificationRequirement[] qualificationRequirements

    String getAnnotation(String d) {
        return annotation?:d
    }

    long getAssignmentDuration(long d) {
        return assignmentDuration?:d
    }

    long getLifetime(long d) {
        return lifetime?:d
    }

    long getAutoApprovalDelay(long d) {
        return autoApprovalDelay?:d
    }

    String getDescription(String d) {
        return description?:d
    }

    String getKeywords(String d) {
        return keywords?:d
    }

    int getMaxAssignments(int d) {
        return maxAssignments?:d
    }

    double getRewardAmount(double d) {
        return rewardAmount?:d
    }

    String getTitle(String d) {
        return title?:d
    }

    QualificationRequirement[] getQualificationRequirements(QualificationRequirement[] d) {
        return qualificationRequirements?:d
    }

}

//}
