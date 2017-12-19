import org.codehaus.groovy.runtime.StackTraceUtils
import geb.Browser
import geb.spock.GebReportingSpec
import geb.Browser
import geb.spock.GebReportingSpec
import pages.app.QUserPage
import spock.lang.*

@Stepwise
@Unroll
class FlowSpecs extends GebReportingSpec {

    @Shared String test_username = System.getenv('TEST_USERNAME')
    @Shared String test_password = System.getenv('TEST_PASSWORD')


    /**
     * Gherkin
     * =======
     * Scenario: CSR wants to access the CFMS
     *     Given CSR has a valid username and password to access CFMS
     *     When CSR enters their credentials
     *     And they sign in
     *     Then they have accessed CFMS
     */
    def "001 - Signing in with a valid account"() {

        println("001 - Signing in with a valid account")

        given: "given the QUserPage home page"
            to QUserPage
            println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}at Page: \"" + title + "\", url: " + getCurrentUrl())

        and: "they are not signed in"
            assert !authModule.loggedInUser.displayed

        when: "My credentials are entered and login is clicked"
            authModule.signIn(test_username,test_password)

        then: "The home page refreshes" 
            at QUserPage
            waitFor { !authModule.isLoggedInUser("") }

        and: "expect to be logged in"
            assert authModule.isLoggedInUser(test_username)
    }

    /**
     * Gherkin
     * =======
     * Scenario: A customer arrives in a no call office
     *   Given the CSR is logged in 
     *     And the queue and hold are both empty
     *     And they are not currently conducting any service
     *    When a customer arrives expecting the service 'Batching - NR'
     *    Then CSR adds that service
     *     And the CSR chooses the correct service type
     *     Then They finish the service
     */
    def "002 - Work process on a single service non-queued engagement"() {

        println("\n002 - Work process on a single service non-queued engagement")

        def srvc = "Emails (All)"

        given: "already logged in"
            at QUserPage
            println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}at Page: \"" + title + "\", url: " + getCurrentUrl())
            assert authModule.isLoggedInUser(test_username) == true

        and: "they are not conducting any service"
            assert ticketModule.btnServe.css("display") == "none"

        when: "a client arrives"
            ticketModule.btnAdd.click()
            waitFor { $("div", class:"z-modal-mask").css("display") == "block" }

        then: "the CSR searches for their service"
            waitFor { ticketModule.inputSearch.isDisplayed() == true }
            ticketModule.inputSearch.firstElement().clear()
            ticketModule.inputSearch << srvc

        when: "the CSR searches and chooses a service"
            waitFor {ticketModule.selectService.size() == 1}
            ticketModule.selectService[0].click()
            ticketModule.btnBegin.click()

        then: "the service is started"
            serviceModule.selectServiceLabel(srvc)

        when: "the service is finished"
            waitFor { serviceModule.btnFinish.isDisplayed() == true }
            println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}Ticket ID: " + ticketModule.textTicketId.text())
            serviceModule.btnFinish.click()
            waitFor { $("div", class:"z-modal-mask")[1].css('display') == 'none' }

        then: "The user returns to the main page"
            $("div", class:"z-modal-mask")[1].css('display') == 'none'
    }

    /**
     * Gherkin
     * =======
     * Scenario: A citizen arrives in a no call office with two services needed
     *   Given the CSR is logged in 
     *     And the queue and hold are both empty
     *     And they are not currently conducting any service
     *    When the citizen arrives expecting the service 'MSP Payment - Rev'
     *    Then CSR adds that service and works it until completed
     *    When the CSR is ready to add the next service
     *    Then the CSR adds the second service 'Property Tax - Rev'
     *    When the CSR has finised with the second service,
     *    Then They finish the service
     */

     //Doesn't work with new Chrome Headless, temporarly commented out
    // def "003 - Work process on two-service non-queued engagement"() {

    //     println("\n003 - Work process on two-service non-queued engagement")

    //     def srvc1 = "Payment - MSP"
    //     def srvc2 = "Payment - SDPR-POC"

    //     given: "already logged in"
    //         at QUserPage
    //         println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}at Page: \"" + title + "\", url: " + getCurrentUrl())
    //         assert authModule.isLoggedInUser(test_username) == true

    //     and: "they are not conducting any service"
    //         assert ticketModule.btnServe.css("display") == "none"

    //     when: "a client arrives"
    //         ticketModule.btnAdd.click()
    //         waitFor { $("div", class:"z-modal-mask").first().css("display") == "block" }

    //     then: "the CSR searches for their service"
    //         waitFor { ticketModule.inputSearch.isDisplayed() == true }
    //         ticketModule.inputSearch.firstElement().clear()
    //         ticketModule.inputSearch << srvc1

    //     when: "the CSR searches and chooses a service"
    //         waitFor {ticketModule.selectService.size() == 1}
    //         ticketModule.selectService[0].click()
    //         ticketModule.btnBegin.click()

    //     then: "the service is started"
    //         serviceModule.selectServiceLabel(srvc1)

    //     when: "the next service is started"
    //         waitFor { serviceModule.btnAddNextService.isDisplayed() == true }
    //         println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}Ticket ID: " + ticketModule.textTicketId.text())
    //         serviceModule.btnAddNextService.click()
    //         waitFor { $("div", class:"z-modal-mask").first().css("display") == "block" }

    //     then: "the CSR searches for their service"
    //         waitFor { ticketModule.inputSearch.isDisplayed() == true }
    //         ticketModule.inputSearch.firstElement().clear()
    //         ticketModule.inputSearch << srvc2

    //     when: "the CSR searches and chooses a service"
    //         waitFor {ticketModule.selectService.size() == 1}
    //         ticketModule.selectService[0].click()
    //         ticketModule.btnApply.click()

    //     then: "the service is started"
    //         serviceModule.selectServiceLabel(srvc2)

    //     when: "the service is finished"
    //         waitFor { serviceModule.btnFinish.isDisplayed() == true }
    //         println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}Ticket ID: " + ticketModule.textTicketId.text())
    //         serviceModule.btnFinish.click()
    //         waitFor { $("div", class:"z-modal-mask")[1].css('display') == 'none' }

    //     then: "The user returns to the main page"
    //         $("div", class:"z-modal-mask")[1].css('display') == 'none'
    // }

    /**
     * Gherkin
     * =======
     * Scenario: CSR is fininshed for the day
     *     Given the CSR is logged in to CFMS
     *     When they logout
     *     Then they are no longer logged in to CFMS
     */
    def "004 - Logging out after signing in"() {

        println("\n004 - Logging out after signing in")

        given: "CSR is already signed in"
            at QUserPage
            println("${ new Date().getDateTimeString() } ${'[FlowSpecs]'.padRight(16)}at Page: \"" + title + "\", url: " + getCurrentUrl())
            assert authModule.isLoggedInUser("") == false

        when: "the CSR signs out"
            authModule.signOut()

        then: "They are no longer signed in"
            waitFor { authModule.isLoggedInUser("") == true }
            authModule.isLoggedInUser("") == true
    }

}
