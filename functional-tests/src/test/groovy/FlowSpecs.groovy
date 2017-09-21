import geb.Browser
import geb.spock.GebReportingSpec
import pages.app.QUserPage
import spock.lang.Unroll

class FlowSpecs extends GebReportingSpec {

    @Unroll
    def "Landing"(){
        given: "At QUserPage"
        to QUserPage

        when: "Do nothing"

        then: "Still at QUserPage"
        at QUserPage
    }

    def "Signing in with a valid account"(){

        given: "I start on the home page" 
            to QUserPage 

        when: "My credentials are input and login is clicked" 

            def user = js."zk.Widget.\$('\$usr').uuid" + "-real"
            def password = js."zk.Widget.\$('\$pwd').uuid"

            $("input", id:user).value("mwalle")
            $("input", id:password).value("password")
            $("button",class:"login-button z-button")[0].click()
         
        then: "The home page refreshes" 
            at QUserPage
            waitFor { $("span", class:"login-text z-label")[0].text() != "" }
         
        expect: "I am logged in" 
            assert { $("span", class:"login-text z-label")[3].text() == "mwalle" }
    }
}
