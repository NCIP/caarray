<%@ tag display-name="arrayDesignListDownloadColumn"
        description="Renders a download column for an array design"
        body-content="empty"%>

<%@ attribute name="itemId" required="true"%>


<%@ include file="projectListTabCommon.tagf"%>

<display:column titleKey="button.download">
    <c:url value="/protected/ajax/arrayDesign/download.action" var="actionUrl">
        <c:param name="arrayDesign.id" value="${itemId}" />
        <c:param name="editMode" value="${editMode}" />
    </c:url>

    <a href="${actionUrl}">
          <img src="<c:url value="/images/ico_download.gif"/>" 
               alt="<fmt:message key="button.download"/>"
               title="<fmt:message key="button.download"/>"
          >
    </a>

</display:column>
