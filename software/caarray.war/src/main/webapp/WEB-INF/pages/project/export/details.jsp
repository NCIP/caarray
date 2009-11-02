<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane paneTitleKey="project.tabs.export" subtab="true">
    <div class="boxpad">
        <p class="instructions">
            Select the format in which you would like to export this Experiment to.
        </p>

        <c:url var="exportToMageTabUrl" value="/ajax/project/export/exportToMageTab.action">
            <c:param name="project.id" value="${project.id}" />
        </c:url>

        <c:url var="exportToGeoZipUrl" value="/ajax/project/export/exportToGeo.action">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="type" value="ZIP" />
        </c:url>
        <c:url var="exportToGeoTgzUrl" value="/ajax/project/export/exportToGeo.action">
            <c:param name="project.id" value="${project.id}" />
            <c:param name="type" value="TGZ" />
        </c:url>

        <table class="form">
            <tr><td>
        <caarray:action url="${exportToMageTabUrl}" actionClass="launch_download" text="Download Consolidated MAGE-TAB" />
            </td></tr>

        <s:if test="geoValidation.isEmpty()">
            <tr><td>
            <s:if test="geoZipOk">
                <caarray:action url="${exportToGeoZipUrl}" actionClass="launch_download" text="Download GEO SOFT ZIP Archive" />
            </s:if>
            <s:else>
                <b>Experiment too large to be packaged in a GEO SOFT ZIP archive.</b>
            </s:else>
            </td></tr>
            <tr><td>
            <caarray:action url="${exportToGeoTgzUrl}" actionClass="launch_download" text="Download GEO SOFT TAR.GZ ('tarball') Archive" />
            </td></tr>
        </s:if>
        <s:else>
            <tr><td>
                <b>GEO SOFT export is not available for this experiment for the folowing reasons:</b>
                <ul>
                    <s:iterator value="geoValidation" id = "message">
                        <li><c:out value="${message}"/></li>
                    </s:iterator>
                </ul>
            </td></tr>
        </s:else>
        </table>
    </div>
</caarray:tabPane>


