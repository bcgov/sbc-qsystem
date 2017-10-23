import geb.spock.GebReportingSpec
import pages.app.QUserPage
import spock.lang.Unroll

class FlowSpecs extends GebReportingSpec {

    def "Info"(){
		given: "At Info Page"
		to QUserPage
        println("${ new Date().getDateTimeString() } [FlowSpecs] at Page: \"" + title + "\", url: " + getCurrentUrl())
		
		when: "Do nothing"
		
		then: "Still at Info Page"
		at QUserPage
	}
}
