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


    def "Login"(){
		given:
		to QUserPage
		
		when:
		
		Thread.Sleep(3000.longValue())
		$("input", 0).value = "English"
		Thread.Sleep(3000.longValue())
        $("input", 1).value = "Administrator"
        $("button", 0).click()
		
		then:
		assert ($("span", text: "Administrator").text == "Administrator")
	  }

	  
/*
dPwPn
    @Unroll
    def "Navigate Page from: #startPage, click Link: #clickLink, Assert Page: #assertPage"(){
        when:
        to startPage

        and:
        (1..clickCount).each{
            $("a", id:"$clickLink").click()
        }

        then:
        at assertPage

        where:
        startPage           | clickLink                     | clickCount    | timeoutSeconds    || assertPage
        QUserPagePage       | "navbar-notifications"        | 1             | 3                 || NotificationsPage
    }
	*/
}
