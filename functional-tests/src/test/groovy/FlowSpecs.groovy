import geb.Browser
import geb.spock.GebReportingSpec
import pages.app.QUserPage
import spock.lang.Unroll

class FlowSpecs extends GebReportingSpec {

    @Unroll
    def "Signing in with a valid account"() {

        given: "given the QUserPage home page"
            to QUserPage

        when: "My credentials are entered and login is clicked"
            authModule.signIn(System.getenv('TEST_USERNAME'),System.getenv('TEST_PASSWORD'))

        then: "The home page refreshes" 
            at QUserPage
            waitFor { $("span", class:"login-text z-label")[3].text() != "" }
        
        expect: "expect to be logged in"
            assert { $("span", "class":"login-text z-label")[3].text() == System.getenv('TEST_USERNAME') }
    }
}
