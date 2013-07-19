<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Collaboration Group</title>
</head>
<body>
    <h1>Manage Collaboration Group</h1>
    <caarray:helpPrint/>
    <div class="padme">
    <div id="tabboxwrapper_notabs">
      <div class="boxpad2">
        <h3>
          <a href="listGroups.action">Collaboration Groups</a> &gt;
          <span class="dark">
            <c:if test="${empty targetGroup}">
                Add a New Collaboration Group
                <c:set var="displayMembers" value="none"/>
            </c:if>
            <c:if test="${!empty targetGroup}">
                ${targetGroup.group.groupName}
                <c:set var="displayMembers" value="block"/>
            </c:if>
          </span>
        </h3>
      </div>
      <div class="boxpad" style="padding-bottom: 10px;">
        <caarray:successMessages />
        <c:if test="${empty targetGroup}">
          <p class="instructions" style="margin-bottom: 10px;">Choose a name for the group.</p>
        </c:if>
        <s:form action="/protected/collaborators/name.action" cssClass="form" id="newGroupForm">
          <s:token/>
          <s:textfield requiredLabel="true" name="groupName" key="collaboration.group.name" size="50" maxlength="254" tabindex="1" value="%{targetGroup.group.groupName}"/>
          <s:hidden name="targetGroup" value="%{targetGroup.id}"/>
          <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:focusFirstElement formId="newGroupForm"/>
        <caarray:actions>
            <caarray:action onclick="document.getElementById('newGroupForm').submit();" actionClass="save" text="Save" />
        </caarray:actions>

        <div style="display: ${displayMembers};">
        <div class="line" style="margin-bottom: 15px;"></div>

        <div>
        <h2 class="bolder">
          Members
        </h2>
       <div class="addlink">
           <c:url value="/protected/collaborators/preAdd.action" var="addUsersUrl">
             <c:param name="targetGroup" value="${targetGroup.id}"/>
           </c:url>
          <caarray:linkButton url="${addUsersUrl}" actionClass="add" text="Add a New Group Member" />
        </div>
       </div>
       <%@ include file="/WEB-INF/pages/collaborators/editTable.jsp" %>
       <del class="btnwrapper">
        <c:url value="/protected/collaborators/delete.action" var="deleteUrl">
          <c:param name="targetGroup" value="${targetGroup.id}"/>
        </c:url>
         <ul id="btnrow">
           <li><caarray:linkButton url="listGroups.action" actionClass="cancel" text="Cancel"/>
           <li><caarray:linkButton url="${deleteUrl}" actionClass="delete" text="Delete"/>
         </ul>
        </del>
      </div>
  </div>
  </div>
  </div>
</body>
</html>