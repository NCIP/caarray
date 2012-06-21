<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<table summary="" cellpadding="0" cellspacing="0" border="0" height="20">
	<tr>
		<td width="1"><!-- anchor to skip main menu --><a href="#content"><img
			src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" /></a></td>
		<!-- link 1 begins -->
		<td height="20" class="mainMenuItem"
			onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()"
			onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()"
			><a
			class="mainMenuLink" href="Home.do"><bean:message
			key="label.menu_home" /></a></td>
		<!-- link 1 ends -->
		<td><img src="images/mainMenuSeparator.gif" width="1" height="16"
			alt="" /></td>
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
			><a class="mainMenuLink" href="Logout.do"><bean:message key="label.menu_logout" /></a></td>
		<!-- link 3 ends -->
		<td><img src="images/mainMenuSeparator.gif" width="1" height="16"
			alt="" /></td>
	</tr>
</table>
