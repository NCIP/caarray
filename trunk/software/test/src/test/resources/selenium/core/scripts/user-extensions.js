/*
 * By default, Selenium looks for a file called "user-extensions.js", and loads and javascript
 * code found in that file. This file is a sample of what that file could look like.
 *
 * user-extensions.js provides a convenient location for adding extensions to Selenium, like
 * new actions, checks and locator-strategies.
 * By default, this file does not exist. Users can create this file and place their extension code
 * in this common location, removing the need to modify the Selenium sources, and hopefully assisting
 * with the upgrade process.
 *
 * You can find contributed extensions at http://wiki.openqa.org/display/SEL/Contributed%20User-Extensions
 */

// The following examples try to give an indication of how Selenium can be extended with javascript.


Selenium.prototype.doType = function(locator, value) {
	/**
   * Sets the value of an input field, as though you typed it in.
   * 
   * <p>Can also be used to set the value of combo boxes, check boxes, etc. In these cases,
   * value should be the value of the option selected, not the visible text.</p>
   * 
   * @param locator an <a href="#locators">element locator</a>
   * @param value the value to type
   */
   
   	//Patch to allow Firefox to work with File Uploads (see http://forums.openqa.org/thread.jspa?messageID=6315&#6315)   
	var element = this.page().findElement(locator);
	if (element.type == "file") {
		try {
			netscape.security.PrivilegeManager.enablePrivilege("UniversalFileRead");
		} catch (e) {
			alert("Permission to read and upload local files was denied.");
		}
	
	}
	this.page().replaceText(element, value);
};


