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

alert("Starting");

if (typeof(Storage) !== "undefined") {
	var storeType = typeof(Storage);
	alert("Have Storage.  It is " + storeType);
}
else {
	alert("No web storage support.");
}

if (typeof(localStorage) !== "undefined") {
	var storeType = typeof(localStorage);
	var len1 = localStorage.length;
	localStorage.setItem("TestLocal", "IsLocal");
	var len2 = localStorage.length;
	//alert("Have localStorage.  It is " + storeType + "; LenBefore: " +  len1 + "; LenAfter: " + len2);
}

if (typeof(sessionStorage) !== "undefined") {
	var storeType = typeof(sessionStorage);
	var len1 = sessionStorage.length;
	sessionStorage.setItem("TestSession", "IsSession");
	var len2 = sessionStorage.length;
	//alert("Have sessionStorage.  It is " + storeType + "; LenBefore: " +  len1 + "; LenAfter: " + len2);
}

/*
for (var i = 0, len = localStorage.length; i < len; i++ ) {
	alert("Count: " + i + "; Key: " + localStorage.key(i) + "; Value: " + localStorage.getItem( localStorage.key(i)) );
}
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
	         //alert("Success on POST, but previous failure (disconnect is 1).  Reloading page.");
	         window.location.replace(currentURL);
           }
	       else {
             //alert("Success on POST, no previous failure (disconnect is 0).  Not reloading page.");
	       }
	     },
	     error: function()
	     {
	       //alert("No success on POST.  Setting disconnect to be 1.");
	       disconnect = 1 ;
	      }
	   });
	  }, 5000);
