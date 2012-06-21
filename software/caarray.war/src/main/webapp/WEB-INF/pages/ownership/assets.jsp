<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Ownership</title>
    <script type="text/javascript">
function toggleAll(/*boolean*/checked, /*div*/div) {
    var inputs = div.getElementsByTagName("input");
    var numElements = inputs.length;
        for (i = 0; i < numElements; i++) {
             var element = inputs[i];
             if ("checkbox" == element.type && element.checked != checked) {
                 element.checked = checked;
             }
        }
}
    </script>
</head>
<body>
    <h1>Manage Ownership</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>Assets owned by ${owner.firstName} ${owner.lastName}</h3>
                <caarray:successMessages />
                <s:actionerror/>
            </div>
            <div class="boxpad">
                <p class="instructions" style="margin-bottom: 10px;">
                    Select the assets to re-assign, then press the <strong>"Find New Owner..."</strong> button below to select the new owner.
                </p>
                <div id="theForm">
                    <s:form action="/protected/ownership/newOwner.action" cssClass="form" method="post" id="reassignForm" theme="simple">
                        <s:token/>
                        <s:hidden name="targetUserId" value="%{ownerId}"/>
                        <h4>Experiments</h4>
                            
<ajax:displayTag id="pdatatable" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" list="${projects}" requestURI="${sortUrl}"
        id="row" excludedParams="project.id" style="clear: none;">
        <caarray:displayTagProperties/>
        <display:column title="<input type='checkbox' onclick='toggleAll(this.checked, $(&quot;pdatatable&quot;));' title='Select all Experiments'/>" >
            <input type="checkbox" id="p${row.id}" value="${row.id}" name="projectIds"/>
        </display:column>
        <display:column title="Experiment ID" >
            <label for="p${row.id}">${row.experiment.publicIdentifier}</label>
        </display:column>
        <display:column property="experiment.title" title="Experiment Title" escapeXml="true" maxLength="30"/>
        <display:column title="Assay Type" >
            <c:choose>
                <c:when test="${row.experiment.assayTypes != null}">
                    <c:forEach items="${row.experiment.assayTypes}" var="currType" varStatus="status">
                        <c:if test="${!status.first}">, </c:if>${currType.name}
                    </c:forEach>
                </c:when>
                <c:otherwise>&nbsp;</c:otherwise>
            </c:choose>
        </display:column>
    </display:table>
</ajax:displayTag>

                                <h4>Collaboration Groups</h4>
                                
<ajax:displayTag id="gdatatable" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
    <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${groups}"
        requestURI="${sortUrl}" sort="list" id="row" pagesize="20">
        <caarray:displayTagProperties/>
        <display:column title="<input type='checkbox' onclick='toggleAll(this.checked, $(&quot;gdatatable&quot;));' title='Select all Groups'/>" >
            <input type="checkbox" id="g${row.id}" value="${row.id}" name="groupIds"/>
        </display:column>
        <display:column titleKey="collaboration.group.name">
            <label for="g${row.id}">${row.group.groupName}</label>
        </display:column>
        <display:column titleKey="collaboration.group.members">
          <c:if test="${fn:length(row.group.users) > 20}">
            <a href="${editUrl}">(View All ${fn:length(row.group.users)})</a>
          </c:if>
          <c:if test="${fn:length(row.group.users) <= 20}">
            <c:forEach items="${row.group.users}" var="curUser" varStatus="status">
              <c:url value="/protected/collaborators/userDetail.action" var="viewUserUrl">
                <c:param name="targetUserId" value="${curUser.userId}"/>
              </c:url>
              <a href="${viewUserUrl}">${curUser.firstName} ${curUser.lastName}</a><c:if test="${!status.last}">,</c:if>
            </c:forEach>
          </c:if>
          <c:if test="${fn:length(row.group.users) == 0}">
            (Empty group)
          </c:if>
        </display:column>
    </display:table>
</ajax:displayTag>

                        <s:submit value="Find New Owner ..."/>
                    </s:form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>