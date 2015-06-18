package nardiff.mt


import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor


/**
 * Created by josh on 6/17/15.
 */
class NardiffServiceTest extends Specification {


    def nardiffService

    def "test multiple users attempting to create new narratives"() {

        setup:
        ExecutorService executor = Executors.newFixedThreadPool(8)
        nardiffService.setBranching([5,5,5])

        def tasks = (1..15).collect {
            { -> nardiffService.openNarrative("a${it}","b${it}") } as Callable<Narrative>
        }

        when:
        List<Future<Narrative>> results = executor.invokeAll(tasks)

        then:
        results.each {
            Narrative n = it.get()
            println "${n.id}->${n.root_narrative.id}"
            n!=null
        }
    }

}