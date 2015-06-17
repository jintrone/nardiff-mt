package nardiff.mt

import edu.msu.mi.gwurk.Task
import edu.msu.mi.gwurk.Workflow
import spock.lang.Specification


/**
 * Created by josh on 6/16/15.
 */
class BootstrapSpec extends Specification {

    void "validate installation"() {

        expect:
        NarrativeSeed.count() == 5
        Narrative.count() == 5
        Workflow.count() == 1
        Task.count() == 5

    }


}