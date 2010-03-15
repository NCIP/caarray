<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="thisUrl" value="/ajax/project/listTab/Hybridizations/edit.action">
    <c:param name="project.id" value="${project.id}" />
    <c:param name="currentHybridization.id" value="${currentHybridization.id}" />
</c:url>

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
        <caarray:protocolSelector returnInitialTab1="annotations" returnInitialTab2="hybridizations" returnInitialTab2Url="${thisUrl}" tabIndex1="5" tabIndex2="6" multiple="true"/>
        <s:hidden name="currentHybridization.id" />
        <s:hidden name="project.id" />
        <s:hidden name="editMode" />
        <input type="submit" class="enableEnterSubmit"/>
    </caarray:projectListTabItemForm>

    <c:if test="${!editMode}">
        <%@ include file="/WEB-INF/pages/project/tabHybridizations/factorValuesList.jsp" %>
        <div id="tabHeader" class="boxpad2"><h3>Download Data</h3></div>
        <%@ include file="/WEB-INF/pages/project/tabCommon/downloadFiles.jsp" %>
    </c:if>
</caarray:tabPane>
