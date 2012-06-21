<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested"%>

<%@ page import="gov.nih.nci.logging.api.domain.*"%>
<%@ page import="gov.nih.nci.logging.webapp.viewobjects.*"%>
<%@ page import="gov.nih.nci.logging.webapp.util.*"%>
<script>
<!--

imgout=new Image(9,9);
imgin=new Image(9,9);

imgout.src="images/expand.gif";
imgin.src="images/collapse.gif";



function filter(imagename,objectsrc){
	if (document.images){
		document.images[imagename].src=eval(objectsrc+".src");
	}
}


function shoh(id) { 
	
	if (document.getElementById) { // DOM3 = IE5, NS6
		if (document.getElementById(id).style.display == "none"){
			document.getElementById(id).style.display = 'block';
			filter(("img"+id),'imgin');			
		} else {
			filter(("img"+id),'imgout');
			document.getElementById(id).style.display = 'none';			
		}	
	} else { 
		if (document.layers) {	
			if (document.id.display == "none"){
				document.id.display = 'block';
				filter(("img"+id),'imgin');
			} else {
				filter(("img"+id),'imgout');	
				document.id.display = 'none';
			}
		} else {
			if (document.all.id.style.visibility == "none"){
				document.all.id.style.display = 'block';
			} else {
				filter(("img"+id),'imgout');
				document.all.id.style.display = 'none';
			}
		}
	}
}

function setAndSubmitPrevious()
    	{
    		document.getElementById('PreviousQueryResultsPagingForm').submit();
    	}
function setAndSubmitNext()
    	{
    		document.getElementById('NextQueryResultsPagingForm').submit();
    	}

-->
</script>

<logic:notPresent name="<%=Constants.SEARCH_RESULTS_PAGE%>">
	<tr>
		<td class="standardText"><br>
		Show Query Page introduction details.</td> 
	</tr>
</logic:notPresent>

<logic:present name="<%=Constants.SEARCH_RESULTS_PAGE%>">

	<logic:equal name="<%=Constants.SEARCH_RESULTS_PAGE%>" 	property="totalResultSize" value="<%=Constants.ZERO%>">
			<TR>
			<TD vAlign=top width="100%"><!-- target of anchor to skip menus --><A name=content><BR/>
			<TABLE class=contentPage cellSpacing=0 cellPadding=0 width=700	summary="" border=0>
				<TBODY>
					<TR>
						<TD>
						<TABLE cellSpacing=1 cellPadding=1 summary="" border=0 width=100%>
							<TBODY>
								<TR>
									<TD class=dataTablePrimaryLabel height=20>Search Results</TD>
								</TR>
								<TR>
									<TD class=dataCellText align=right height=20>
									<TABLE cellSpacing=0 cellPadding=0 border=0>
										<TBODY>
											<TR>
												<TD class="standardTextBold">
													
													<bean:write name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="searchResultMessage" />
												</TD>
											</TR>
										</TBODY>
									</TABLE>
									</TD>
								</TR>	
								
							</TBODY>
						</TABLE>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
			</TR>

	</logic:equal>
	<logic:notEqual name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="totalResultSize" value="<%=Constants.ZERO%>">
			<TR>
			<TD vAlign=top width="100%"><!-- target of anchor to skip menus --><A name=content><BR/>
			<TABLE class=contentPage cellSpacing=0 cellPadding=0 width=100%	summary="" border=0>
				<TBODY>
					<TR>
						<TD>
						<TABLE cellSpacing=0 cellPadding=0 summary="" border=0>
							<TBODY>
								<TR>
									<TD class=dataTablePrimaryLabel height=20>Search Results</TD>
								</TR>
								<!-- paging begins -->
								<TR>
									<TD class=dataPagingSection align=right height=20>
									<TABLE cellSpacing=0 cellPadding=0 border=0>
										<TBODY>
											<TR>
												
												<logic:notEqual name="<%=Constants.SEARCH_RESULTS_PAGE%>" 	property="isFirstPage" value="<%=Constants.TRUE%>">
													<html:form styleId="PreviousQueryResultsPagingForm" action="/QueryResultsPaging">
													<input type="hidden" name="targetPageNumber" value="<bean:write name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="previousPageNumber" />">
													<TD class=dataPagingText align=right><A	href="javascript: setAndSubmitPrevious()"> < Previous</A></TD>
													</html:form>
												</logic:notEqual>
												<logic:equal name="<%=Constants.SEARCH_RESULTS_PAGE%>" 	property="isFirstPage" value="<%=Constants.TRUE%>">
													<TD class=dataPagingText align=right></TD>
												</logic:equal>
												<TD class=dataPagingText align=middle>| Page  <bean:write name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="currentPageNumber"/> of 
													<bean:write name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="lastPageNumber" /> |</TD>
													
												<logic:notEqual name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="isLastPage" value="<%=Constants.TRUE%>">
													<html:form styleId="NextQueryResultsPagingForm" action="/QueryResultsPaging">
													<input type="hidden" name="targetPageNumber" value="<bean:write name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="nextPageNumber" />">
													<TD class=dataPagingText align=right><A	href="javascript: setAndSubmitNext()"> Next > </A></TD>
													</html:form>
												</logic:notEqual>
												<logic:equal name="<%=Constants.SEARCH_RESULTS_PAGE%>" 	property="isLastPage" value="<%=Constants.TRUE%>">
													<TD class=dataPagingText align=right></TD>
												</logic:equal>

											</TR>
										</TBODY>
									</TABLE>
									</TD>
								</TR>
								<!-- paging ends -->
					
								<TR>
									<TD>
									<TABLE class=dataTable cellSpacing=0 cellPadding=3 width="700"
										summary="Log Search Results data" border=0>
										<TBODY>
										<bean:define id="oddRow" value="true" />											
										<bean:define name="<%=Constants.SEARCH_RESULTS_PAGE%>" property="searchResultObjects" id="searchResults" />	
										<logic:iterate name="searchResults" id="logMessage" type="LogMessage">
											<%if (oddRow.equals("true")) { oddRow = "false";%>
												<TR class=dataRowLight>
											<%}else{ oddRow = "true";%>											
												<TR class=dataRowDark>
											<%}%>
												<TD class=dataCellText>
												<TABLE cellSpacing=3 cellPadding=0 border=0 width=100%>
													<TR>
														<TD class=standardTextBold width=10% >Server:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="server"/></TD>
														<TD class=standardTextBold width=10%>Application:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="application"/></TD>
														<TD class=standardTextBold width=10%>Organization:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="organization"/></TD>
													</TR>
													<TR>
														<TD class=standardTextBold width=10%>Created On:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="createdDate"/></TD>
														<TD class=standardTextBold width=10%>Username:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="userName"/></TD>
														<TD class=standardTextBold width=10%>Level:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="logLevel"/></TD>														
													</TR>
													<TR>
														
														<TD class=standardTextBold width=10%>Session ID:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="sessionID"/></TD>
														<TD class=standardTextBold width=10%>Thread:</TD>
														<TD class=standardText colspan=3 width=15%><bean:write name="logMessage" property="thread"/></TD>														

													</TR>
													<TR>													
														<TD class=standardTextBold colspan=1 width=10%>Message:</TD>
														<TD class=standardText colspan=5 width=15%><bean:write name="logMessage" property="message" filter="false" /></TD>
													</TR>
													<TR>													
														<TD class=standardTextBold colspan=1 width=10%>NDC:</TD>
														<TD class=standardText colspan=5 width=15%><bean:write name="logMessage" property="ndc" filter="false" /></TD>
													</TR>
													<TR>													
														<TD class=standardTextBold colspan=1 width=10%>Throwable:</TD>
														<TD class=standardText colspan=5 width=15%><bean:write name="logMessage" property="throwable" filter="false" /></TD>
													</TR>
													<% if(logMessage.isObjectStateLog()){ 
													%>	

													<TR>
														<TD class=standardTextBold width=10%>Object ID:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="objectID"/></TD>
														<TD class=standardTextBold width=10%>Object Name:</TD>
														<TD class=standardText width=15%><bean:write name="logMessage" property="objectName"/></TD>
														<TD class=standardTextBold colspan=1 width=10%>Operation:</TD>
														<TD class=standardText colspan=1 width=15%><bean:write name="logMessage" property="operation"/></TD>
													</TR>																							
													<bean:define name="logMessage" property="objectAttributeSet" id="objectAttributes" />	

													<TR>													
														<TD colspan=8>
															<a  class="standardTextBold" onClick="shoh('<bean:write name="logMessage" property="id"/>');">Object Attribute Details</a>
															<a onClick="shoh('<bean:write name="logMessage" property="id"/>');">
																<img src="images/expand.gif" name="img<bean:write name="logMessage" property="id"/>" border="0" alt="arrow icon: Click here to expand the details.">
															</a>
															
															<div style="display: none;" id="<bean:write name="logMessage" property="id"/>" >		
															<table summary="Enter summary of data here" cellpadding="3"	cellspacing="0" border="0" class="dataTable" width="100%">
																<tr>
																	<th class="dataTableHeader" scope="col" align="center">Attribute</th>
																	<th class="dataTableHeader" scope="col" align="center">Current Value</th>
																	<logic:equal name="logMessage" property="operation" value="UPDATE">
																	<th class="dataTableHeader" scope="col" align="center">Previous Value</th>
																	</logic:equal>
																</tr>
																<logic:iterate name="objectAttributes" id="objectAttribute" type="ObjectAttribute">																
																<tr class="dataRowLight">
																	<td class="dataCellText">&nbsp;<bean:write name="objectAttribute" property="attributeName"/> </td>
																	<td class="dataCellText">&nbsp;<bean:write name="objectAttribute" property="currentValue"  filter="false" /> </td>
																	<logic:equal name="logMessage" property="operation" value="UPDATE">
																	<td class="dataCellText">&nbsp;<bean:write name="objectAttribute" property="previousValue" filter="false" /> </td>
																	</logic:equal>
																</tr>
																</logic:iterate>
															</table>
															</div>
														</TD>
													<TR>
													
													<% } %>
												</TABLE>

												</TD>
											</TR>
										
										
										</logic:iterate>

										</TBODY>
									</TABLE>
									</TD>
								</TR>
								
								
							</TBODY>
						</TABLE>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</A>
			</TD>
		</TR>

	</logic:notEqual>

</logic:present>
