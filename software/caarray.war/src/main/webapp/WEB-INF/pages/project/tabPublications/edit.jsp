<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane>
    <caarray:projectListTabItemForm entityName="Publication" item="${currentPublication}"
        itemName="${currentPublication.title}">
        <c:if test="${editMode}">
            <s:textfield required="true" name="currentPublication.title" key="experiment.publications.title" size="80"
                tabindex="1" maxlength="254"/>
        </c:if>
        <s:textarea name="currentPublication.authors" key="experiment.publications.authors" rows="3" cols="75"
            tabindex="2" />
        <s:textfield name="currentPublication.uri" key="experiment.publications.uri" size="80" tabindex="3"
            maxlength="254">
            <s:param name="url">true</s:param>
            <s:param name="target">_blank</s:param>
        </s:textfield>
        <s:textfield name="currentPublication.editor" key="experiment.publications.editor" size="50" tabindex="4"
            maxlength="254"/>
        <s:textfield name="currentPublication.pages" key="experiment.publications.pages" size="20" tabindex="5"
            maxlength="254"/>
        <s:textfield name="currentPublication.publisher" key="experiment.publications.publisher" size="50" tabindex="6"
            maxlength="254"/>
        <s:textfield name="currentPublication.pubMedId" key="experiment.publications.pubMedId" size="50" tabindex="7"
            maxlength="254"/>
        <s:textfield name="currentPublication.volume" key="experiment.publications.volume" size="20" tabindex="8"
            maxlength="254"/>
        <s:textfield name="currentPublication.year" key="experiment.publications.year" size="10" tabindex="9"
            maxlength="254"/>
        <s:textfield name="currentPublication.publication" key="experiment.publications.publication" size="80"
            tabindex="10" maxlength="254"/>
        <s:select name="currentPublication.type" label="Type" tabindex="11" list="publicationTypes" listKey="id"
            listValue="value" value="currentPublication.type.id" headerKey=""
            headerValue="--Select a Publication Type--" />
        <s:select name="currentPublication.status" label="Status" tabindex="12" list="publicationStatuses" listKey="id"
            listValue="value" value="currentPublication.status.id" headerKey=""
            headerValue="--Select a Publication Status--" />
        <s:hidden name="currentPublication.id" />
        <s:hidden name="project.id" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>
</caarray:tabPane>
