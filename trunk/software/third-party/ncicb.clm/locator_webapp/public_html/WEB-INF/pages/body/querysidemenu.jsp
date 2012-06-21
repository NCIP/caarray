<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested"%>

<%@ page import="java.util.*"%>
<%@ page import="gov.nih.nci.logging.webapp.util.Constants"%>
<%@ page import="gov.nih.nci.logging.webapp.viewobjects.*"%>


<table summary="" cellpadding="0" cellspacing="0" border="0" width="150" height="100%">

	
	<tr>
		<td class="subMenuPrimaryTitle" height="21"><bean:message
			key="label.query_section" /><!-- anchor to skip sub menu --><a
			href="#content"><img src="images/shim.gif" alt="Skip Menu" width="1"
			height="1" border="0" /></a></td>
	</tr>
	<tr>
		<td valign="top" class="contentPage">
		<table summary="" cellpadding="3" cellspacing="0" border="0" width="100">
			<html:form styleId="QueryForm" action="/Query">
				<html:hidden property="activity" value="submitQuery" />

				<tr>
					<td class="formMessage" colspan="3"><bean:message
						key="label.required_indicator" /></td>
				</tr>
				<tr>
					<td class="formTitle" height="20" colspan="3"><bean:message
						key="label.search_criteria" /></td>
				</tr>
				<bean:define name="<%=Constants.PROTECTED_ATTRIBUTES%>" id="protectionAttributesHashMap"/>
				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=field1><bean:message
						key="label.application" /></td>
											
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="application" size="30" name="application" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="application"/>" readonly="readonly">
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=application size=30 name=application value="<bean:write name="queryForm" property="application"/>" readonly="readonly">
					</logic:notPresent>						
					</td>
				</tr>		
				<!-- 
					Log Level
				 -->		
				<bean:define name="<%=Constants.LOGLEVEL_MAP%>" id="logLevelMap" />
				<tr>
						<td class=formRequiredNotice width=5>*</td>
						<td class=formLabel><LABEL for=field3><bean:message
							key="label.loglevel_type" /></LABEL></td>
						<td class=formField>
							<html:select property="logLevel">
								<html:options collection="logLevelMap" property="key" labelProperty="value"/>
							</html:select>
						</td>					
				</tr>
				
				
				<!-- 
					Server 
				 -->
				<bean:define name="<%=Constants.SERVER_NAME_COLLECTION%>" id="serverObjectCollection" type="java.util.Collection"/>
				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=server><bean:message
						key="label.server" /></LABEL></td>
					<td class=formField>
						<html:select property="server">
							<html:options collection="serverObjectCollection" property="name" labelProperty="name" />
						</html:select>
					</td>
				</tr>
				<!-- 
					User 
				 -->
				<logic:present name="<%=Constants.PROTECTED_ATTRIBUTES%>">
					<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=user><bean:message
							key="label.user" /></LABEL></td>

					<% 
						HashMap protectionAttributes = (HashMap)protectionAttributesHashMap;
						if(protectionAttributes!=null){
							if(!protectionAttributes.values().isEmpty() && protectionAttributes.containsKey(Constants.USER_ATTRIBUTE)){
								ArrayList userList = (ArrayList) protectionAttributes.get(Constants.USER_ATTRIBUTE);
								if(userList!=null && userList.size()>0){
									HashMap users = new HashMap();
									Iterator itt = userList.iterator();
									while(itt.hasNext()){
										String temp = (String)itt.next();
										users.put(temp,temp);
									}
									request.getSession().setAttribute("USER_LIST",users);
								 %>
									<bean:define name="USER_LIST" id="userMap"/>
									<td class=formField>
										<html:select property="user">
											<html:options collection="userMap" property="key" labelProperty="value"/>
										</html:select>
									</td>
								<%}else{ %>
										<td class=formField>
										<logic:present name="<%=Constants.CURRENT_FORM%>">
											<INPUT class="formField" id="user" size="30" name="user" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="user"/>" >
										</logic:present>
										<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
											<INPUT class=formField id=user size=30 name=user value="<bean:write name="queryForm" property="user"/>">
										</logic:notPresent>					
										</td>									
									<% 
								}
							}else{
								%>
								<td class=formField>
									<logic:present name="<%=Constants.CURRENT_FORM%>">
										<INPUT class="formField" id="user" size="30" name="user" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="user"/>" >
									</logic:present>
									<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
										<INPUT class=formField id=user size=30 name=user value="<bean:write name="queryForm" property="user"/>">
									</logic:notPresent>					
								</td>
								<%
							 }
						}
					 %>
					</tr>
				</logic:present>
				<logic:notPresent name="<%=Constants.PROTECTED_ATTRIBUTES%>">
					<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=user><bean:message key="label.user" /></LABEL></td>
						<td class=formField>
						<logic:present name="<%=Constants.CURRENT_FORM%>">
							<INPUT class="formField" id="user" size="30" name="user" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="user"/>" >
						</logic:present>
						<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
							<INPUT class=formField id=user size=30 name=user value="<bean:write name="queryForm" property="user"/>">
						</logic:notPresent>					
						</td>
					</tr>
				</logic:notPresent>
				<!-- 
					Organization 
				 -->
				<logic:present name="<%=Constants.PROTECTED_ATTRIBUTES%>">
					<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=organization><bean:message
							key="label.organization" /></LABEL></td>

					<% 
						HashMap protectionAttributes = (HashMap)protectionAttributesHashMap;
						if(protectionAttributes!=null){

							if(!protectionAttributes.values().isEmpty() && protectionAttributes.containsKey(Constants.ORGANIZATION_ATTRIBUTE)){


								ArrayList orgList = (ArrayList) protectionAttributes.get(Constants.ORGANIZATION_ATTRIBUTE);
								if(orgList!=null && orgList.size()>0){
									
									HashMap organizations = new HashMap();
									
									Iterator itt = orgList.iterator();
									
									while(itt.hasNext()){
										String temp = (String)itt.next();
										organizations.put(temp,temp);
									}
									request.getSession().setAttribute("ORG_LIST",organizations);

								 %>
									<bean:define name="ORG_LIST" id="organizationMap"/>
									<td class=formField>
										<html:select property="organization">
											<html:options collection="organizationMap" property="key" labelProperty="value"/>
										</html:select>
									</td>
								<%}else{ %>
									<td class=formField>
										<logic:present name="<%=Constants.CURRENT_FORM%>">
											<INPUT class="formField" id="organization" size="30" name="organization" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="organization"/>" >
										</logic:present>
										<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
											<INPUT class=formField id=organization size=30 name=organization value="<bean:write name="queryForm" property="organization"/>">
										</logic:notPresent>
									</td>
									<% 
								}
							}else{
								%>
								<td class=formField>
										<logic:present name="<%=Constants.CURRENT_FORM%>">
											<INPUT class="formField" id="organization" size="30" name="organization" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="organization"/>" >
										</logic:present>
										<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
											<INPUT class=formField id=organization size=30 name=organization value="<bean:write name="queryForm" property="organization"/>">
										</logic:notPresent>
								</td>
								
								<%
							 }
						}
					
					 %>
					</tr>
				</logic:present>
				<logic:notPresent name="<%=Constants.PROTECTED_ATTRIBUTES%>">
					<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=organization><bean:message
							key="label.organization" />2</LABEL></td>
						<td class=formField>
						<logic:present name="<%=Constants.CURRENT_FORM%>">
							<INPUT class="formField" id="organization" size="30" name="organization" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="organization"/>" >
						</logic:present>
						<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
							<INPUT class=formField id=organization size=30 name=organization value="<bean:write name="queryForm" property="organization"/>">
						</logic:notPresent>
						</td>
						
					</tr>
				</logic:notPresent>
				

				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=sessionID><bean:message
						key="label.session_id" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="sessionID" size="30" name="sessionID" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="sessionID"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=sessionID size=30 name=sessionID value="<bean:write name="queryForm" property="sessionID"/>">
					</logic:notPresent>
					</td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=message><bean:message
						key="label.message" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="message" size="30" name="message" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="message"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=message size=30 name=message value="<bean:write name="queryForm" property="message"/>">
					</logic:notPresent>
					</td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=ndc><bean:message key="label.ndc" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="ndc" size="30" name="ndc" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="ndc"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=ndc size=30 name=ndc value="<bean:write name="queryForm" property="ndc"/>">
					</logic:notPresent>
					</td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=thread><bean:message
						key="label.thread" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="thread" size="30" name="thread" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="thread"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=thread size=30 name=thread value="<bean:write name="queryForm" property="thread"/>">
					</logic:notPresent>
					</td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=throwable><bean:message
						key="label.throwable" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="throwable" size="30" name="throwable" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="throwable"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=throwable size=30 name=throwable value="<bean:write name="queryForm" property="throwable"/>">
					</logic:notPresent>
					</td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=objectID><bean:message
						key="label.object_id" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="objectID" size="30" name="objectID" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="objectID"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=objectID size=30 name=objectID value="<bean:write name="queryForm" property="objectID"/>">
					</logic:notPresent>
					</td>
				</tr>
				<!-- 
					ObjectName 
				
				<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=objectName><bean:message key="label.object_name" /></LABEL></td>
						<td class=formField>
						<logic:present name="<%=Constants.CURRENT_FORM%>">
							<INPUT class="formField" id="objectName" size="30" name="objectName" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="objectName"/>" >
						</logic:present>
						<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
							<INPUT class=formField id=objectName size=30 name=objectName value="<bean:write name="queryForm" property="objectName"/>">
						</logic:notPresent>					
						</td>
					</tr>
					-->
				<!-- 
					ObjectName 
				-->
				<logic:present name="<%=Constants.PROTECTED_ATTRIBUTES%>">
					<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=objectName><bean:message
							key="label.object_name" /></LABEL></td>

					<% 
						HashMap protectionAttributes = (HashMap)protectionAttributesHashMap;
						if(protectionAttributes!=null){
							if(!protectionAttributes.values().isEmpty() && protectionAttributes.containsKey(Constants.OBJECT_NAME_ATTRIBUTE)){
								ArrayList objectNameList = (ArrayList) protectionAttributes.get(Constants.OBJECT_NAME_ATTRIBUTE);
								if(objectNameList!=null && objectNameList.size()>0){
									HashMap objectNames = new HashMap();
									Iterator itt = objectNameList.iterator();
									while(itt.hasNext()){
										String temp = (String)itt.next();
										objectNames.put(temp,temp);
									}
									request.getSession().setAttribute("OBJECTNAME_LIST",objectNames);
								 %>
									<bean:define name="OBJECTNAME_LIST" id="objectNameMap"/>
									<td class=formField>
										<html:select property="objectName">
											<html:options collection="objectNameMap" property="key" labelProperty="value"/>
										</html:select>
									</td>
								<%}else{ %>
										<td class=formField>
										<logic:present name="<%=Constants.CURRENT_FORM%>">
											<INPUT class="formField" id="objectName" size="30" name="objectName" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="objectName"/>" >
										</logic:present>
										<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
											<INPUT class=formField id=objectName size=30 name=objectName value="<bean:write name="queryForm" property="objectName"/>">
										</logic:notPresent>					
										</td>									
									<% 
								}
							}else{
								%>
								<td class=formField>
									<logic:present name="<%=Constants.CURRENT_FORM%>">
										<INPUT class="formField" id="objectName" size="30" name="objectName" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="objectName"/>" >
									</logic:present>
									<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
										<INPUT class=formField id=objectName size=30 name=objectName value="<bean:write name="queryForm" property="objectName"/>">
									</logic:notPresent>					
								</td>
								<%
							 }
						}
					 %>
					</tr>
				</logic:present>
				<logic:notPresent name="<%=Constants.PROTECTED_ATTRIBUTES%>">
					<tr>
						<td class=formRequiredNotice width=5></td>
						<td class=formLabel><LABEL for=objectName><bean:message key="label.object_name" /></LABEL></td>
						<td class=formField>
						<logic:present name="<%=Constants.CURRENT_FORM%>">
							<INPUT class="formField" id="objectName" size="30" name="objectName" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="objectName"/>" >
						</logic:present>
						<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
							<INPUT class=formField id=objectName size=30 name=objectName value="<bean:write name="queryForm" property="objectName"/>">
						</logic:notPresent>					
						</td>
					</tr>
				</logic:notPresent> 
				
				
				<tr>
					<td class=formRequiredNotice width=5></td>
					<td class=formLabel><LABEL for=operation><bean:message
						key="label.operation" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="operation" size="30" name="operation" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="operation"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=operation size=30 name=operation value="<bean:write name="queryForm" property="operation"/>">
					</logic:notPresent>
					</td>
				</tr>

				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=startDate><bean:message
						key="label.start_date" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="startDate" size="10" name="startDate" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="startDate"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=startDate size=10 name=startDate value="<bean:write name="queryForm" property="startDate"/>">
					</logic:notPresent> <bean:message
						key="label.date_format" /></td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=startTime><bean:message
						key="label.start_time" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="startTime" size="10" name="startTime" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="startTime"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=startTime size=10 name=startTime value="<bean:write name="queryForm" property="startTime" />">
					</logic:notPresent> <bean:message
						key="label.time_format" /></td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=endDate><bean:message
						key="label.end_date" /></LABEL></td> 
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="endDate" size="10" name="endDate" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="endDate"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=endDate size=10 name=endDate value="<bean:write name="queryForm" property="endDate"/>">
					</logic:notPresent> <bean:message
						key="label.date_format" /></td> 
				</tr>
				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=endTime><bean:message
						key="label.end_time" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" id="endTime" size="10" name="endTime" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="endTime"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=endTime size=10 name=endTime value="<bean:write name="queryForm" property="endTime"/>">
					</logic:notPresent> <bean:message
						key="label.time_format" /></td>
				</tr>
				<tr>
					<td class=formRequiredNotice width=5>*</td>
					<td class=formLabel><LABEL for=recordCount><bean:message
						key="label.records_per_page" /></LABEL></td>
					<td class=formField>
					<logic:present name="<%=Constants.CURRENT_FORM%>">
						<INPUT class="formField" type="integer" id="recordCount" size="10" name="recordCount" value="<bean:write name="<%=Constants.CURRENT_FORM%>" property="recordCount"/>" >
					</logic:present>
					<logic:notPresent name="<%=Constants.CURRENT_FORM%>">
						<INPUT class=formField id=recordCount size=10 name=recordCount value="<bean:write name="queryForm" property="recordCount"/>">
					</logic:notPresent>
					</td>
				</tr>
				<tr>
					<td align="right" colspan="3"><!-- action buttons begins -->
					<table cellpadding="4" cellspacing="0" border="0">
						<tr>
							<td><input class="actionButton" type="submit"
								value='<bean:message key="label.submit_button"/>' /></td>
							</html:form>
							<html:form  action="/Query">	
							<td><input class="actionButton" type="submit"
								value='<bean:message key="label.reset_button"/>' /></td>
							</html:form>
						</tr>
					</table>
					<!-- action buttons end --></td>
				</tr>


		</table>
		</td>
	</tr>
	<tr>
		<td class="subMenuFooter" height="22">&nbsp;</td>
	</tr>
</table>
