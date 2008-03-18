<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<caarray:tabPane subtab="true">
    <caarray:projectListTabItemForm entityName="Hybridization" item="${currentHybridization}" itemName="${currentHybridization.name}"
        isSubtab="true">
        <c:if test="${editMode}">
            <s:textfield name="currentHybridization.name" key="experiment.hybridizations.name" required="true" size="80" tabindex="1" />
        </c:if>
        <s:textarea name="currentHybridization.description" key="currentHybridization.description" rows="3" cols="80"
            tabindex="2" />
        <caarray:annotationAssociationPicker baseId="labeledExtractPicker" entityName="Hybridization" associatedEntityName="Labeled Extract" itemId="${currentHybridization.id}" tabIndex="3" />
        <s:if test="project.experiment.arrayDesigns.size > 1">
            <s:select required="true" key="currentHybridization.array.design" tabindex="4"
                      list="project.experiment.arrayDesigns" listValue="name" listKey="id" value="currentHybridization.array.design.id"
                      headerKey="" headerValue=""/>
        </s:if>
        <s:elseif test="project.experiment.arrayDesigns.size == 1 && !editMode">
            <s:textfield key="currentHybridization.array.design" value="%{currentHybridization.array.design.name}"
                readonly="true"/>
        </s:elseif>
        <s:else>
            <s:hidden key="currentHybridization.array.design"/>
        </s:else>

        <s:hidden name="currentHybridization.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>
</caarray:tabPane>
