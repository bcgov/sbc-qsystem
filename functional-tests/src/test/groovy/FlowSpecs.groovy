import geb.spock.GebReportingSpec
import pages.app.QUserPage
import pages.app.QSystemInfoPage
import spock.lang.Unroll

class FlowSpecs extends GebReportingSpec {

    def "Info"(){
		given: "At Info Page"
		to QSystemInfoPage
		
		when: "Do nothing"
		
		then: "Still at Info Page"
		at QSystemInfoPage
	}
}
