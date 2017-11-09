import geb.Module 
 
class TicketModule extends Module {

    static content = {
        // index.zul
        btnInvite { $("button", class:"bdd-invite") }
        btnAdd { $("button", class: "bdd-add-citizen") }
        btnServe { $("button", class: "bdd-screen") }

        //addTicketDialogue.zul
        inputSearch { $("input", class: "bdd-input-search") }
        inputCategory { $("input", class: "bdd-input-category") }
        
        selectService { $("tr", class: "z-listitem").findAll { it.displayed } }

        btnAddToQueue { $("button", class: "bdd-add-to-queue") }
        btnBegin { $("button", class: "bdd-begin-service") }
        btnCancel { $("button", class: "bdd-cancel-service") }
    }
}
