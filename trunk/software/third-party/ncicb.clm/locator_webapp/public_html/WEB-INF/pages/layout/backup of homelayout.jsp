<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>
<html:html locale="en">
<head>
<title><tiles:getAsString name="title" /></title>
<META name="author" content="Vijay Parmar">
<META name="keywords"
	content="Common Logging Module, NCICB , CLM, Logging">
<META name="description"
	content="Log Locator Tool to locate and view logs generated via Common Logging Module">

<base
	href='<%=(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/")%>' />
<link rel="stylesheet" href="style/styleSheet.css" type="text/css" />
<link rel="stylesheet" href="style/translocator.css" type="text/css" />
<script language="JavaScript" src="scripts/script.js" type="text/javascript"></script>
</head>

<body>
<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="100%">

	<!-- nci hdr begins -->
	<tiles:get name="nci_header" />
	<!-- nci hdr ends -->

	<tr>
		<td height="100%" valign="top">
		<table summary="" cellpadding="0" cellspacing="0" border="0"
			height="100%">

			<!-- application hdr begins -->
			<tiles:get name="app_header" />
			<!-- application hdr ends -->

			<tr>
				<td width="190" valign="top" class="subMenu"></td>
				<td valign="top" width="100%">
				<table summary="" cellpadding="0" cellspacing="0" border="0"
					width="100%" height="100%">
					<tr>
						<td height="20" width="100%" class="mainMenu">
						<!-- main menu begins -->
						<tiles:get name="menu"/>
						<!-- main menu ends --></td>
					</tr>

					<!--_____ main content begins _____-->
					<tiles:get name="content" />
					<!--_____ main content ends _____-->

					<tr>
						<td height="20" width="100%" class="footerMenu">
						<!-- application ftr begins -->
						<tiles:get name="app_footer" />
						<!-- application ftr ends --></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td><!-- footer begins -->
		<tiles:get name="nci_footer" />
		<!-- footer ends --></td>
	</tr>
</table>
</body>
</html:html>
