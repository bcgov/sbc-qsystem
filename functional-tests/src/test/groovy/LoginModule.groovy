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

        def usr = '#' + browser.driver.executeScript("zk.Widget.\$('$usr').uuid") + '-real'
        def pwd = browser.driver.executeScript("zk.Widget.\$('$pwd').uuid")

        given: "I start on the home page" 
            to QUserPage 
        when: "My credentials are input and login is clicked" 
            
            // https://www.zkoss.org/wiki/ZK_Client-side_Reference/General_Control/UI_Composing#Find_a_Widget_at_Client 
            // https://www.zkoss.org/javadoc/latest/jsdoc/zk/Widget.html#$n()
            //retrieves the combobox by excecuting jquery selector and the zk client side widget functions


            $("input", id:usr).value("Mark")                    // look into static assignments of id attributes 
            $("input", id:pwd).value("password")                // https://www.zkoss.org/wiki/ZK_Developer's_Reference/Testing/Testing_Tips 
            $("button","class":"login-button z-button").click() // THIS ui-sref="authentication.signin" needs to be implemented in the .zul source 
         
        then: "The home page refreshes" 
            at QUserPage
            waitFor { $("span", "class":"login-text z-label")[0].text() != "" } 
         
        expect: "I am logged in" 
        assert { $("span", "class":"login-text z-label")[0].text() == "Mark" } 
    } 
}