<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true"  paneTitleKey="file.manage">

    <div class="boxpad">
        <div class="addlink">
                <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('projectForm', 'tabboxlevel2wrapper'); return false;"/>
        </div>
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <p class="instructions">All files listed below will have a new file type assigned.</p>

        <s:form action="ajax/project/files/changeFileType" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabbox1wrapper'); return false;">
            <s:token/>

                            <s:select requiredLabel="true" name="changeToFileType" label="Select New File Type" 
                              list="%{availableFileTypes}" listValue="%{getText('experiment.files.filetype.' + name)}"
                              listKey="name" value="fileType.name"/>

                <display:table class="searchresults" cellspacing="0" defaultsort="1" list="${selectedFiles}" pagesize="${pageSize}"
                    requestURI="${sortUrl}" id="row" excludedParams="project.id">
                    <caarray:displayTagProperties/>
                    <display:column titleKey="experiment.files.name" >
                        <c:out value="${row.name}" />
                    <s:hidden name="selectedFiles" value="%{#attr.row.id}" />
                    </display:column>
                    <display:column titleKey="experiment.files.type.current" >
                        <c:choose>
                            <c:when test="${row.fileType != null}">
                                <fmt:message key="experiment.files.filetype.${row.fileType.name}" />
                            </c:when>
                            <c:otherwise>
                                <fmt:message key="experiment.files.filetype.unknown" />
                            </c:otherwise>
                        </c:choose>
                    </display:column>

                </display:table>

            <s:hidden name="project.id" />
            <input type="submit" class="enableEnterSubmit"/>

        </s:form>
        <caarray:actions>
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('projectForm', 'tabboxlevel2wrapper'); return false;"/>
        </caarray:actions>
    </div>
</caarray:tabPane>
