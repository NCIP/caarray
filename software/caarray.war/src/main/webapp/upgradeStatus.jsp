<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">
    <s:if test="${upgrading}">
        setTimeout(function() {
            window.location='<c:url value="/upgradeStatus.action"/>';
        }, 5000);
    </s:if>
</script>

<div class="padme">
    <div id="tabboxwrapper_notabs">
        <div class="boxpad2">
            <h3>Updating Database</h3>
        </div>

        <div class="boxpad">
            <p class="instructions">
                Your database schema version is out of date.
                Please wait while the following migration scripts are run.
            </p>
            <display:table class="searchresults" cellspacing="0" list="${upgradeList}" id="row" pagesize="20">
                <caarray:displayTagProperties/>
                <display:column property="fromVersion" titleKey="upgrade.fromVersion"/>
                <display:column property="toVersion" titleKey="upgrade.toVersion"/>
                <display:column titleKey="upgrade.status">
                    <c:if test="${not empty row.status}">
                        <fmt:message key="upgrade.status.${row.status}">
                            <fmt:param><c:url value="/" /></fmt:param>
                        </fmt:message>
                    </c:if>
                </display:column>
            </display:table>
        </div>
    </div>
</div>