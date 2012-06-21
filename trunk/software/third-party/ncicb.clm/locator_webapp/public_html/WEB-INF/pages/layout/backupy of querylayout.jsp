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
<script language="JavaScript" src="scripts/script.js"
	type="text/javascript"></script>
</head>

<body>
<table summary="" cellpadding="0" cellspacing="0" border="1"
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
				<td width="190" valign="top" class="subMenu"><!-- submenu begins -->
				<tiles:get name="query_side_menu" /> <!-- submenu ends --></td>
				<td valign="top" width="100%">
				<table summary="" cellpadding="0" cellspacing="0" border="0"
					width="100%" height="100%">
					<tr>
						<td height="20" width="100%" class="mainMenu"><!-- main menu begins -->
						<tiles:get name="menu" /> <!-- main menu ends --></td>
					</tr>

					<!--_____ main content begins _____-->
					<tr>
						<td width="100%" valign="top"><br>

						<!-- target of anchor to skip menus --><a name="content" />

						<table summary="" cellpadding="0" cellspacing="0" border="0"
							class="contentPage" width="600">
							<tr>
								<td><tiles:get name="content" /></td>
							</tr>
						</table></td>
					</tr>
					<!--_____ main content ends _____-->


				</table>
				</td>
			</tr>

			<tr>
				<td height="20" width="100%" class="footerMenu"><!-- application ftr begins -->
				<tiles:get name="app_footer" /><!-- application ftr ends --></td>
			</tr>
			
		</table>
		</td>
	</tr>
	<tr>
		<td><!-- footer begins -->
			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				class="ftrTable">
				<tr>
					<td valign="top" align="center">
					<div align="center"><a href="http://www.cancer.gov/"><img
						src="images/footer_nci.gif" width="63" height="31"
						alt="National Cancer Institute" border="0"></a> <a
						href="http://www.dhhs.gov/"><img src="images/footer_hhs.gif"
						width="39" height="31"
						alt="Department of Health and Human Services" border="0"></a> <a
						href="http://www.nih.gov/"><img src="images/footer_nih.gif"
						width="46" height="31" alt="National Institutes of Health"
						border="0"></a> <a href="http://www.firstgov.gov/"><img
						src="images/footer_firstgov.gif" width="91" height="31"
						alt="FirstGov.gov" border="0"></a></div>
					</td>
				</tr>
			</table>
		<!-- footer ends --></td>
	</tr>

</table>
</body>
</html:html>
