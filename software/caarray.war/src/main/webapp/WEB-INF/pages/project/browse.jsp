<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<html>
<head>
    <title>Experiment Details</title>
</head>
<body>
    <h1>
        Experiment Details
    </h1>
	<div class="pagehelp">
		<a href="javascript:openHelpWindow('')" class="help">Help</a>
		<a href="javascript:printpage()" class="print">Print</a>
	</div>
    <div class="padme">
        <h2>
            <span class="dark">Experiment:</span>
            <span id="experimentTitleHeader"><c:out value="${project.experiment.title}" default="New Experiment"/></span>
        </h2>
        <div id="tabboxwrapper_notabs">
<caarray:tabPane paneTitleKey="experiment.overview">
    <div class="boxpad">
        <s:form action="ajax/project/tab/Overview/save" cssClass="form" id="projectForm" theme="readonly" onsubmit="return false;">
            <s:textfield required="true" name="project.experiment.title" label="Experiment Title" size="80" tabindex="1"/>
            <s:textfield label="Status" value="%{getText(project.status.resourceKey)}"/>
            <s:textfield name="project.experiment.publicIdentifier" label="Experiment Identifier"/>
            <s:select required="true" name="project.experiment.serviceType" label="Service Type" tabindex="4"
                      list="@gov.nih.nci.caarray.domain.project.ServiceType@values()" listValue="%{getText(resourceKey)}"
                      headerKey="" headerValue="--Select a Service Type--"/>
            <s:select required="true" name="project.experiment.assayType" label="Assay Type" tabindex="5"
                      list="@gov.nih.nci.caarray.domain.project.AssayType@values()" listValue="%{getText(resourceKey)}"
                      headerKey="" headerValue="--Select an Assay Type--"/>
        </s:form>
    </div>
</caarray:tabPane>
      </div>
    </div>
</body>
</html>