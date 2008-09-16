<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Factor" item="${currentFactor}" itemName="${currentFactor.name}"
        isSubtab="true">
            <c:if test="${editMode}">
                <s:textfield required="true" name="currentFactor.name" key="experiment.factors.name" size="80" tabindex="1"/>
            </c:if>
            <s:textarea name="currentFactor.description" key="experiment.factors.description" cols="80" rows="8" tabindex="2"/>
            <s:select name="currentFactor.type" key="experiment.factors.type" tabindex="3" required="true"
                      list="categories" listKey="id" listValue="value" value="%{currentFactor.type.id}"
                      headerKey="" headerValue="--Select a Category--"/>
            <s:hidden name="currentFactor.id" />
            <s:hidden name="editMode" />
            <s:hidden name="project.id" />
            <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>
    <c:if test="${!editMode}">
        <%@ include file="/WEB-INF/pages/project/tabFactors/factorValuesList.jsp" %>
    </c:if>
</caarray:tabPane>