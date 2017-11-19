import geb.Module 
 
class ServiceModule extends Module {

    static content = {
        btnAddNextService { $("button", class: "bdd-add-next-service") }
        btnFinish { $("button", text: "Finish") }

        textTicketId { $("span", class: "bdd-ticket-id") }
    }

    // match a service string to content in the list (in future return an int to index)
    boolean selectServiceLabel (String srvc) {
      println("${ new Date().getDateTimeString() } ${'[ServiceModule]'.padRight(16)}selectServiceLabel srvc: \"$srvc\"")
      return { js."jq(\$('.z-listcell-content:contains($srvc)').text().trim()" == $srvc }      
    }
}