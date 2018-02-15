function DHTMLSound() {
    document.getElementById("inviteGong").play();
}

var disconnect = 0;

var inter = setInterval(function () {
	
	   var currentURL = window.location.href;
	   
	   jq.ajax({
	     type: "POST",
         url: currentURL,
	     success: function(response ){
	       if(disconnect){
	         clearInterval(inter);
//	         window.history.go(0);
	         window.location.replace(currentURL);
           }
	     },
	     error: function()
	     {
	       disconnect = 1 ;
	      }
	   });
	  }, 60000);
