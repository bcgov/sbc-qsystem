import geb.Module 
 
class AuthModule extends Module {

    // loginForm.zul
    static content = {
        loginUser { $("span", class: "bdd-usr").$("input") }
        loginPass { $("input", class: "bdd-pwd") }
        loginButton { $("button", class:"login-button")[0] }
        logoutButton { $("button", class:"login-button")[1] }
        loggedInUser { $("span", class:"login-text")[3] }
    }

    void signIn(String username, String password) {
        loginUser.value(username)
        loginPass.value(password)
        loginButton.click()
        println("${ new Date().getDateTimeString() } ${'[AuthModule]'.padRight(16)}signIn username: \"$username\" password: \"$password\"")
    }

    void signOut() {
        logoutButton.click()
        println("${ new Date().getDateTimeString() } ${'[AuthModule]'.padRight(16)}signOut ${ loginUser.value() }")
    }

    boolean isLoggedInUser(String username) {
        return (loggedInUser.text() == username) ? true : false
    }
}
