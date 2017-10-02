import geb.Module 
 
class AuthModule extends Module {

    static content = {
        loginUser { $("input", class: "z-combobox-input")[0] }
        loginPass { $("input", id: js."zk.Widget.\$('\$pwd').uuid") }
        loginButton { $("button",class:"login-button z-button")[0] }
    }

    void signIn(String username, String password) {
        loginUser.value(username)
        loginPass.value(password)
        loginButton.click()
    }
}