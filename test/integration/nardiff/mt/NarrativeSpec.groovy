package nardiff.mt


import grails.test.spock.IntegrationSpec


/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class NarrativeSpec extends IntegrationSpec {

    static String storytext = "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like)."

    static Narrative generateNarrative(Narrative parent) {
        Narrative n = new Narrative(parent, "AGGSIGNMENT")
        n.text = storytext
        n.save([flush: true, failOnError: true])
    }

    def setup() {
    }

    def cleanup() {
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
