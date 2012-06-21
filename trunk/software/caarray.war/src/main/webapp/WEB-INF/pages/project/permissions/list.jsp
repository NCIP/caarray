<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
 <c:url value="/protected/ajax/project/permissions/listSamples.action" var="pagingUrl"/>
 <ajax:displayTag ajaxFlag="true" id="datatable2" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent">
            <display:table class="searchresults" cellspacing="0" list="${sampleResults}" requestURI="${pagingUrl}" id="row" pagesize="10"
              excludedParams="__checkbox_sampleSecurityLevels" >
            <caarray:displayTagProperties/>
            <display:setProperty name="paging.banner.group_size" value="3"/>
            <display:column title="${checkboxAll}">
                <s:checkbox id="chk%{#attr.row.id}" name="sampleSecurityLevels" fieldValue="%{#attr.row.id}" value="false" theme="simple" />
            </display:column>
            <display:column titleKey="experiment.samples.name" >
                         <c:url var="sampleUrl" value="/ajax/project/listTab/Samples/view.action">
                            <c:param name="project.id" value="${project.id}" />
                            <c:param name="currentSample.id" value="${row.id}" />
                        </c:url>
                        <c:url var="projectUrl" value="/project/details.action">
                            <c:param name="project.id" value="${project.id}"/>
                            <c:param name="initialTab" value="annotations"/>
                            <c:param name="initialTab2" value="samples"/>
                            <c:param name="initialTab2Url" value="${sampleUrl}"/>
                        </c:url>
                        <a href="${projectUrl}"><caarray:abbreviate value="${row.name}" maxWidth="20" title="${row.description}" /></a>
            </display:column>

            <display:column titleKey="project.permissions.selectSecLevel">
                    <c:choose>
                        <c:when test="${!empty accessProfile.sampleSecurityLevels &&
                            accessProfile.sampleSecurityLevels[row] != null}">
                            <c:set var="sampleSecLevel" value="${accessProfile.sampleSecurityLevels[row]}"/>
                            <fmt:message key="${sampleSecLevel.resourceKey}"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="SecurityLevel.none"/>
                        </c:otherwise>
                    </c:choose>
            </display:column>
            </display:table>
</ajax:displayTag>