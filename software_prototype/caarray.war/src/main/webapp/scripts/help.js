// Pre Requisite
// 1. The variable "contextPath" is set to an appropriate value in the JSP page including this javascript file.

// Constant values
var helpURL = "/help/index.html";
var helpContext = "caArray_Online_Help";
var context = "context="+helpContext;


// Opens a new window with Left Navigation.
function openHelpWindowWithNavigation(contextName) {
	// Get the URL
	var pageURL = makeHelpURL(contextName, true);
	openWin(pageURL);
}

// Opens a new window with Left Navigation.
function openHelpWindow(contextName) {
	openHelpWindowWithNavigation (contextName);
}

// Opens a new window without Left Navigation.
function openHelpWindowWithoutNavigation(contextName) {
	// Get the URL
	var pageURL = makeHelpURL(contextName, false);
	openWin(pageURL);
}

// Actually opens the help window
function openWin(pageURL) {
	window.open (pageURL,"Help", "status,scrollbars,resizable,width=800,height=500");
}


// Create the full URL.
// Params:
//			1. contextName - If contextName is not blank or null, the topic will be appended.
//			2. includeNav - If true, the left navigation pane appears. Else, the navigation pane is not displayed.

function makeHelpURL(contextName, includeNav) {
	// Do not display the navigation pane by default.
	var nav = "single=true";
	if (includeNav) {
		// if includeNav = true, then display the navigation pane.
		nav = "single=false";
	}

	var fullURL = contextPath + helpURL + "?" + nav + "&" + context;
	if ((contextName != null) && (contextName != "")) {
		fullURL += "&topic=" + contextName;
	}

	return fullURL;
}


