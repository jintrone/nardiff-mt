package nardiff.mt


import grails.test.spock.IntegrationSpec


/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class NarrativeSpec extends IntegrationSpec {

    static String storytext = "It is a long established fact that a reader will be distracted by the readable" +
            " content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a " +
            "more-or-less normal distribution of letters, as opposed to using 'Content here, content here', " +
            "making it look like readable English. Many desktop publishing packages and web page editors now use " +
            "Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many " +
            "web sites still in their infancy. Various versions have evolved over the years, sometimes " +
            "by accident, sometimes on purpose (injected humour and the like)."

    static Narrative generateNarrative(Narrative parent) {
        Narrative n = new Narrative(parent, "AGGSIGNMENT")
        n.text = storytext
        n.save([flush: true, failOnError: true])
    }

    def Narrative finishNarrative(Narrative n) {
        nardiffService.finalizeNarrative(n,[
                text:storytext,
                timeDistractor:10,
                timeReading:10,
                timeWriting:10])
    }



    def nardiffService


    void "test cascading within during narrative construction"() {

        when:
        Narrative n = new Narrative(NarrativeSeed.list().first())
        n.save(flush:true,failOnError: true)

        then:
        n.root_narrative == NarrativeSeed.list().first()
        n in n.root_narrative.narratives

        when:
        Narrative n2 = new Narrative(n,"assignment","worker")
        n.save(flush:true,failOnError: true)

        then:
        n2.root_narrative == NarrativeSeed.list().first()
        n2 in n.root_narrative.narratives
        n2 in n.children
        n2.parent_narrative == n


    }


    void "test that narratives are pruned when they are too old"() {

        when:
        List narratives = (1..NarrativeSeed.count()).collect {
            nardiffService.openNarrative("a","b")
        }

        then:
        narratives.each { Narrative n ->
            n.abandoned = false
        }

        when:
        narratives.each { Narrative n->
            n.opened = new Date(System.currentTimeMillis() - 1000*60*11)
            n.save flush:true,failOnError: true
        }
        Narrative n = nardiffService.openNarrative("a","b")

        then:
        n==null
        narratives.each {
            it.abandoned
        }



    }


    void "test that workers can only get NarrativeSeed.count stories when calling openNarrative"() {

        when:
        List narratives = (1..NarrativeSeed.count()).collect {
            nardiffService.openNarrative("a","b")
        }
        Narrative last = nardiffService.openNarrative("a","b")
        Narrative other = nardiffService.openNarrative("b","c")

        then:
        (narratives*.root_narrative.id as Set).size() ==NarrativeSeed.count()
        last == null
        other!=null

    }

    void "test that tree construction happens correctly"() {

        setup:
        nardiffService.setBranching([2,2,2])
        def rootNarratives = Narrative.list()


        when:
        ["a","b","c"].each {workerid->
            (1..rootNarratives.size()).each {
                 nardiffService.openNarrative(workerid,"x")
            }
        }

        then:
        nardiffService.findNarrativeToExpandForWorker("d").depth == 0
        rootNarratives.each {
            it.children.size() == 3
        }


        when:
        Narrative.findAllByRoot_narrativeAndDepth(NarrativeSeed.list().first(),1).each {
            finishNarrative(it)
        }

        then:
        nardiffService.findNarrativeToExpandForWorker("d").depth == 1
        Narrative.findAllByRoot_narrativeAndDepth(NarrativeSeed.list().first(),1).collect {
            it.expanding
        }.findAll {
            it == true
        }.size() == 2

        when:
        List<Narrative> ns = ["e","f","g","h","i","j","k","l"].collect {
            nardiffService.openNarrative(it,"x")
        }

        then:
        ns.each {
            it.depth == 2
        }

        when:
        Narrative last = nardiffService.openNarrative("m","x")

        then:
        last.depth == 1

    }


    void "Nardiff story expansion"() {

        setup:
        Narrative narrative = new Narrative();
        narrative.text = storytext

        narrative.too_simple = false;

        Narrative.withTransaction { tx ->
            narrative.save flush: true, failOnError: true
            narrative.root_narrative = narrative;
            narrative.save flush: true, failOnError: true
        }

        when:
        Narrative toExpand = NardiffStuff.getNarrativeToExpand(narrative.id)

        then:
        narrative == toExpand

        when:
        Narrative childone = generateNarrative(toExpand)
        Narrative childtwo = generateNarrative(toExpand)

        then:
        childone.depth == 1
        childtwo.depth == 1
        childone != childtwo

        when:
        Narrative nextExpansion = NardiffStuff.getNarrativeToExpand(narrative.id)

        then:
        childone == nextExpansion
    }
}
