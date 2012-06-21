<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested"%>
<%@ page import="gov.nih.nci.logging.webapp.util.Constants"%>

<table summary="" cellpadding="0" cellspacing="0" border="0" height="20">
	<tr>
		<td width="1"><a href="#content"><img src="images/shim.gif"
			alt="Skip Menu" width="1" height="1" border="0" /></a></td>
		<!-- link 1 begins -->
		<td height="20" class="mainMenuItemOver"
			onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()"
			onmouseout="changeMenuStyle(this,'mainMenuItemOver'),hideCursor()"
			><a
			class="mainMenuLink" href="Home.do"><bean:message
			key="label.menu_home" /></a></td>
		<!-- link 1 ends -->
		<td><img src="images/mainMenuSeparator.gif" width="1" height="16"
			alt="" /></td>
<logic:present name="<%=Constants.LOGIN_OBJECT%>">
		<!-- link 2 begins -->
		<td height="20" class="mainMenuItem"
			onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()"
			onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()"
			><a class="mainMenuLink"
			href="Query.do"><bean:message key="label.menu_query" /></a></td>
		<!-- link 2 ends -->
		<td><img src="images/mainMenuSeparator.gif" width="1" height="16"
			alt="" /></td>
		<!-- link 3 begins -->
		<td height="20" class="mainMenuItem"
			onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()"
			onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()"
			><a class="mainMenuLink"
			href="Logout.do"><bean:message key="label.menu_logout" /></a></td>
		<!-- link 3 ends -->
		<td><img src="images/mainMenuSeparator.gif" width="1" height="16"
			alt="" /></td>
</logic:present>
	</tr>
</table>
