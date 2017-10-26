package pages.app

import geb.Page
import AuthModule
import TicketModule
import ServiceModule

class QUserPage extends Page {

    static url = "quser/"
    static at = { title == "QSystem" }

    static content = {
        authModule { module AuthModule }
        ticketModule { module TicketModule }
        serviceModule { module ServiceModule }
    }
}
