import geb.Module 
 
class AuthModule extends Module {

    static content = {
        loginUser { $("input", id: js."zk.Widget.\$('\$usr').uuid" + "-real") }
        loginPass { $("input", id: js."zk.Widget.\$('\$pwd').uuid") }
        loginButton { $("button",class:"login-button z-button")[0] }
    }

    void signIn(String username, String password) {
        loginUser.value(username)
        loginPass.value(password)
        loginButton.click()
    }
}