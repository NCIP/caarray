<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles"
	prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template"
	prefix="template"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested"
	prefix="nested"%>


<%@ page import="gov.nih.nci.logging.webapp.viewobjects.*"%>
<%@ page import="gov.nih.nci.logging.webapp.util.*"%>
<TR>
	<TD vAlign=top width="100%">

	<TABLE class=contentPage cellSpacing=2 cellPadding=0 width=100%
		summary="" border=0>
		<TBODY>
			<TR>
				<TD >
					<h3>Query Page</h3>
				Populate the search criteria and click submit to retrieve matching search results from the log database.
				<br>
				<br> 
				More information about the Query Section.<ul>
				<li><b>Application</b></li><br>
					The Name of the application whose logs are viewable. <b>Readonly field</b>  and <b>Required field</b> 
				<li><b>Log Level</b></li><br>
					Log Level for the log message. The list of Log Levels allowed for search. <b>Required field</b>
				<li><b>Server</b></li><br>
					Server name for the log message. The list of server names available in the log database. <b>Required field</b>
				<li><b>User</b></li><br>
					User Login Name  for the log message.
				<li><b>Organization</b></li><br>
					Organization for the log message.
				<li><b>Session ID</b></li><br>
					Session ID for the log message.
				<li><b>Message</b></li><br>
					Message for the log message.
				<li><b>NDC</b></li><br>
					NDC for the log message.
				<li><b>Thread</b></li><br>
					Thread for the log message.
				<li><b>Throwable</b></li><br>
					Throwable for the log message.
				<li><b>Object ID</b></li><br>
					Object ID  for the log message.
				<li><b>Object Name</b></li><br>
					Object Name
				<li><b>Operation</b></li><br>
					Operation for the log message.
				<li><b>Start Date</b></li><br>
					Start Date for the log message. <b>Required field</b>
				<li><b>Start Time</b></li><br>
					Start Time for the log message. <b>Required field</b>
				<li><b>End Date</b></li><br>
					End Date for the log message. <b>Required field</b>
				<li><b>End Time</b></li><br>
					End Time for the log message. <b>Required field</b>
				<li><b>Records Per Page</b></li><br>
					Records Per Page to be displayed in the search results. <b>Required field</b>
																			 
			</ul>
			</td>
		</tr>
		
		</TBODY>
	</TABLE>

	</TD>
</TR>

