<%@ tag display-name="protocolSelector" description="Renders the the protocol selector." body-content="empty"%>

<%@ attribute name="tabIndex1" required="true" type="java.lang.String" %>
<%@ attribute name="tabIndex2" required="true" type="java.lang.String" %>
<%@ attribute name="required" required="false" type="java.lang.String" %>
<%@ attribute name="multiple" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab1" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2" required="false" type="java.lang.String" %>
<%@ attribute name="returnInitialTab2Url" required="false" type="java.lang.String" %>
<%@ attribute name="hideAddButton" required="false" type="java.lang.String" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<fmt:message key="protocols" var="listLabel"/>

<c:url var="addButtonUrl" value="/protected/protocol/manage.action" >
    <c:param name="startWithEdit" value="true" />
    <c:param name="returnProjectId" value="${project.id}" />
    <c:param name="returnInitialTab1" value="${returnInitialTab1}" />
    <c:param name="returnInitialTab2" value="${returnInitialTab2}" />
    <c:param name="returnInitialTab2Url" value="${returnInitialTab2Url}" />
</c:url>
<c:url var="autocompleteUrl" value="/ajax/project/tab/Samples/retrieveXmlProtocolList.action" />

<c:if test="${editMode}">
    <script type="text/javascript">
        var protocolPicker;
    </script>
    <s:select key="protocolType" tabindex="%{#attr.tabIndex1}" list="protocolTypes" listValue="value" listKey="id" cssStyle="width: 300px;"
        headerKey="" headerValue="--Select a Protocol Type--" value="protocolType.id" onchange="ListPickerUtils.updateParams(protocolPicker, 'protocolType', $('projectForm_protocolType').value); ListPickerUtils.forceUpdate(protocolPicker);">
        <s:param name="after">
            <span id="progressMsg" style="display:none;"><img alt="Indicator" src="<c:url value="/images/indicator.gif"/>" /> Loading... </span>
        </s:param>
    </s:select>
</c:if>


<caarray:listSelector baseId="protocol" listLabel="${listLabel}" listField="${selectedProtocols}"
    listFieldName="selectedProtocols" tabIndex="${tabIndex2}" required="${required}" multiple="${multiple}"
    returnInitialTab1="${returnInitialTab1}" returnInitialTab2="${returnInitialTab2}"
    returnInitialTab2Url="${returnInitialTab2Url}" hideAddButton="${hideAddButton}" addButtonUrl="${addButtonUrl}"
    showFilter="true" filterFieldName="protocolName" allowReordering="true"
    autocompleteUrl="${autocompleteUrl}" autocompleteParamNames="protocolType" autocompleteParamValues="${protocolType.id}"/>

<c:if test="${!editMode}">
    <c:forEach items="${currentProtocolApplications}" var="currentProtocolApplication">
        <c:if test="${not empty currentProtocolApplication.values}">
            <tr>
                <td class="tdLabel"><label class="label">Protocol Parameters:</label></td>
                <td>
                    <ul>
                    <c:forEach items="${currentProtocolApplication.values}" var="value">
                        <li>${value.parameter.name} (${currentProtocolApplication.protocol.name}): ${value.displayValue} </li>
                    </c:forEach>
                    </ul>
                </td>
            </tr>
        </c:if>
    </c:forEach>
</c:if>
