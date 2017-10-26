import geb.Module 
 
class ServiceModule extends Module {

    static content = {
        btnFinish { $("button", text: "Finish") }
    }

    // match a service string to content in the list (in future return an int to index)
    boolean selectServiceLabel (String srvc) {
      println("${ new Date().getDateTimeString() } [ServiceModule] selectServiceLabel srvc: \"$srvc\"")
      return { js."jq(\$('.z-listcell-content:contains($srvc)').text().trim()" == $srvc }      
    }
}