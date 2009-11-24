<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<page:applyDecorator name="popup">
<c:url value="/protected/arrayDesign/list.action" var="arrayDesignUrl" />
<script type="text/javascript">
    closeAndUpdate = function() {
    window.opener.location = '${arrayDesignUrl}';
    window.close();
  }
</script>
<html>
<head>
    <title>Manage Array Designs</title>
</head>
<body>
    <h1>Manage Array Designs</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <span class="dark">
                        <s:if test="${empty arrayDesign.id}">
                            New Array Design
                        </s:if>
                        <s:else>
                            ${arrayDesign.name}
                        </s:else>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
            <div class="boxpad">


                    <div class="instructions">
                       The Array Design ${arrayDesign.name} has been successfully uploaded.
                    </div>

                <div id="theForm">
                    <caarray:actions>
                        <caarray:action actionClass="cancel" text="Close Window" onclick="window.close()" />
                        <caarray:action actionClass="import" text="Close Window and go to Manage Design Array" onclick="closeAndUpdate()" />
                    </caarray:actions>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
</page:applyDecorator>
