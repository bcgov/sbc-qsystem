import geb.Module 
import pages.app.QUserPage 
import pages.app.QSystemInfoPage 
import geb.spock.GebReportingSpec 
import spock.lang.Unroll 
 
/** 
 * Example of a Module in Groovy. 
 */ 
 
class LoginModule extends Module { 
 
    void userLogin(){ 
        given: "I start on the home page" 
            to HomePage 
        when: "My credentials are input and login is clicked" 
             
            $("input", id:"bUHZh-real").value("Mark")           // look into static assignments of id attributes 
            $("input", id:"bUHZl").value("")                    // https://www.zkoss.org/wiki/ZK_Developer's_Reference/Testing/Testing_Tips 
            $("button","class":"login-button z-button").click() // THIS ui-sref="authentication.signin" needs to be implemented in the .zul source 
         
        then: "The home page refreshes" 
            at SignedIn                                         // SignedIn will be the same as Homepage since the redirect is "#" 
            waitFor { $("span", "class":"login-text z-label")[0].text() != "" } 
         
        expect: "I am logged in" 
        assert { $("span", "class":"login-text z-label")[0].text() == "Mark" } 
    } 
} 
 
// https://www.zkoss.org/wiki/ZK_Client-side_Reference/General_Control/UI_Composing#Find_a_Widget_at_Client 
 
// https://www.zkoss.org/javadoc/latest/jsdoc/zk/Widget.html#$n() 
// execute these javascripts to get values 
 
// zk.Widget.$('$usr').setValue("Mark") 
zk.Widget.$(jq('$usr')).setValue('Mark') 
zk.Widget.$(jq('$usr')).smartUpdate('value', 'Mark') 
 
// zk.Widget.$('$usr').getValue() 
 
 
// zk.Widget.$('$pwd').$n().value 
 
// jq('$btnSubmit').click();