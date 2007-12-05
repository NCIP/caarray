<%@ tag display-name="protocolSelector" description="Renders the the protocol selector." body-content="empty"%>
<%@ attribute name="returnInitialTab1" required="true" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2" required="true" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2Url" required="true" type="java.lang.String" %>
<%@ attribute name="tabIndex1" required="true" type="java.lang.String" %>
<%@ attribute name="tabIndex2" required="true" type="java.lang.String" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>
<c:url var="addProtocolUrl" value="/protected/protocol/manage.action" >
    <c:param name="startWithEdit" value="true" />
    <c:param name="returnProjectId" value="${project.id}" />
    <c:param name="returnInitialTab1" value="${returnInitialTab1}" />
    <c:param name="returnInitialTab2" value="${returnInitialTab2}" />
    <c:param name="returnInitialTab2Url" value="${returnInitialTab2Url}" />
</c:url>
<s:select key="protocolType" tabindex="${tabIndex1}" list="protocolTypes" listValue="value" listKey="id" cssStyle="width: 300px;"
    headerKey="" headerValue="--Select a Protocol Type--" value="protocolType.id">
    <s:param name="after">
        <span id="progressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /> Loading.. </span>
    </s:param>
</s:select>
<s:select key="protocol" tabindex="${tabIndex2}" list="protocols" value="protocol.id" cssStyle="width: 300px;">
    <s:param name="after">
        <caarray:linkButton style="margin-left: 305px; margin-top: -20px;" actionClass="add" text="Add" url="${addProtocolUrl}" onclick="return TabUtils.confirmNavigateFromForm()"/>
    </s:param>
</s:select>
