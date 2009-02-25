<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true"  paneTitleKey="file.manage">
    <div class="boxpad">
        <p class="instructions">Required fields are marked with <span class="required">*asterisks*</span>.</p>
        <s:form action="ajax/project/files/saveFiles" cssClass="form" id="projectForm" onsubmit="TabUtils.submitTabForm('projectForm', 'tabboxwrapper'); return false;">
            <c:forEach var="file" items="${selectedFiles}" varStatus="status">
                <s:hidden name="selectedFiles[${status.index}]" value="${file.id}" />
                <s:select required="true" name="selectedFiles[${status.index}].fileType" label="${file.name} File Type" tabindex="${status.index}"
                      list="@gov.nih.nci.caarray.domain.file.FileType@values()" listValue="%{getText('experiment.files.filetype.' + name)}"
                      listKey="name" value="selectedFiles[${status.index}].fileType.name"/>
            </c:forEach>
            <s:hidden name="project.id" />
            <input type="submit" class="enableEnterSubmit"/>
        </s:form>
        <caarray:actions>
            <caarray:action actionClass="save" text="Save" onclick="TabUtils.submitTabForm('projectForm', 'tabboxlevel2wrapper'); return false;"/>
        </caarray:actions>
    </div>
</caarray:tabPane>
