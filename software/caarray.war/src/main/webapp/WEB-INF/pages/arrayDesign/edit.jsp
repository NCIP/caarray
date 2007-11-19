<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<s:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</s:if>
<html>
<head>
    <title>Import Array Designs</title>
</head>
<body>
    <h1>Import Array Designs</h1>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <a href="list.action">Array Designs</a> &gt;
                    <span class="dark">
                        <s:if test="${empty target}">
                            Import Array Design
                        </s:if><s:else>
                            ${target.name}
                        </s:else>
                    </span>
                </h3>
            </div>
            <div class="boxpad">
                <s:form action="/protected/arrayDesign/save.action" cssClass="form" enctype="multipart/form-data" method="post" id="arrayDesignForm">
                    <tbody>
                        <tr><th colspan="2">Array Design Details</th></tr>
                        <s:textfield required="true" name="target.name" label="Array Design Name" size="50" tabindex="1"/>
                        <s:select required="true" name="target.provider" label="Manufacturer" tabindex="2"
                                  list="manufacturers" listKey="id" listValue="name"
                                  headerKey="" headerValue="--Please select a Manufacturer--" value="target.provider.id"/>
                        <s:textfield required="true" name="target.version" label="Version Number" size="50" tabindex="3"/>
                        <s:select required="true" name="target.technologyType" label="Feature Type" tabindex="4"
                                  list="featureTypes" listKey="id" listValue="value" value="target.technologyType.id"
                                  headerKey="" headerValue="--Please select a Feature Type--"/>
                        <s:select required="true" name="target.organism" label="Organism" tabindex="5"
                                  list="organisms" listKey="id" listValue="commonName" value="target.organism.id"
                                  headerKey="" headerValue="--Please select an Organism--"/>
                        <s:hidden name="target.id"/>
                    </tbody>
                    <tbody>
                        <tr><th colspan="2">Upload Array Design File</th></tr>
                        <s:if test="${!empty target.id}">
                            <s:textfield theme="readonly" name="target.designFile.name" label="Current File"/>
                        </s:if>
                        <s:if test="${editMode}">
                            <s:file required="true" name="upload" label="Browse to File" tabindex="6"/>
                        </s:if>
                    </tbody>
                </s:form>
                <caarray:actions>
                    <c:url value="/protected/arrayDesign/list.action" var="listUrl"/>
                    <caarray:action url="${listUrl}" actionClass="cancel" text="Cancel" tabindex="7" />
                    <s:if test="${editMode}">
                        <caarray:action onclick="document.getElementById('arrayDesignForm').submit();" actionClass="save" text="Save" tabindex="8"/>
                    </s:if><s:else>
                        <c:url value="/protected/arrayDesign/edit.action" var="editUrl">
                            <c:param name="target.id" value="${target.id}"/>
                        </c:url>
                        <caarray:action url="${editUrl}" actionClass="edit" text="Edit" tabindex="8"/>
                    </s:else>
                </caarray:actions>
            </div>
        </div>
    </div>
</body>
</html>