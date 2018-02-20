function DHTMLSound() {
    document.getElementById("inviteGong").play();
}

var disconnect = 0;

//var lastTimeCheck = new Date();
//var currentTimeCheck;
//var diff;

/*
var inter2 = setInterval(function () {
	   //  Initialize variables.
	   currentTimeCheck = new Date();
       timeDiff = Math.abs(currentTimeCheck - lastTimeCheck);	   
       alert("Last time: " + lastTimeCheck + "; Current time: " + currentTimeCheck + "; Diff: " + timeDiff);
       lastTimeCheck = currentTimeCheck;
}, 30000);
*/


var inter = setInterval(function () {
	
	   //  Initialize variables.
	   var currentURL = window.location.href;
	   //currentTimeCheck = new Date();
       //timeDiff = Math.abs(currentTimeCheck - lastTimeCheck);	   
	   //alert("Last time: " + lastTimeCheck + "; Current time: " + currentTimeCheck + "; Diff: " + timeDiff);
	   
	   jq.ajax({
	     type: "POST",
         url: currentURL,
	     success: function(response ){
	       if(disconnect){
	         clearInterval(inter);
//	         window.history.go(0);
	         alert("Smartboard Disconnected");
	         window.location.replace(currentURL);
           }
	     },
	     error: function()
	     {
	       disconnect = 1 ;
	      }
	   });
	  }, 5000);
*/