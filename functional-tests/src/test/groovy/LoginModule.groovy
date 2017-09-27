import geb.Module 
import geb.Browser
import pages.app.QUserPage 
import geb.spock.GebReportingSpec 
import spock.lang.Unroll 
 
/** 
 * Example of a Module in Groovy. 
 */ 
 
class LoginModule extends Module { 

    void userLogin(){

        given: "I start on the home page" 
            to QUserPage 
        when: "My credentials are input and login is clicked" 
            
            def user = js."zk.Widget.\$('\$usr').uuid" + "-real"
            def password = js."zk.Widget.\$('\$pwd').uuid"

            $("input", id:user).value("mwalle")                    // look into static assignments of id attributes 
            $("input", id:password).value("password")                // https://www.zkoss.org/wiki/ZK_Developer's_Reference/Testing/Testing_Tips 
            $("button","class":"login-button z-button").click() // THIS ui-sref="authentication.signin" needs to be implemented in the .zul source 

        then: "The home page refreshes" 
            at QUserPage
            waitFor { $("span", "class":"login-text z-label")[2].text() != "" } 
         
        expect: "I am logged in"
            assert { $("span", "class":"login-text z-label")[2].text() == "mwalle" } 
    } 
}