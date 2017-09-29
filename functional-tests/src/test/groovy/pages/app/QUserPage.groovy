package pages.app

import geb.Page
import AuthModule

class QUserPage extends Page {

    static url = "/qsystem/quser"
    static at = { title == "QSystem" }

    static content = {
        authModule { module AuthModule }
    }
}
