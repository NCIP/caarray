<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<s:if test="${!editMode}">
    <c:set var="theme" value="readonly" scope="request"/>
</s:if>
<s:if test="${locked || !editMode}">
    <c:set var="lockedTheme" value="readonly"/>
</s:if>
<s:else>
    <c:set var="lockedTheme" value="xhtml"/>
</s:else>

<html>
<head>
    <title>Manage Antibodies</title>
</head>
<body>
    <h1>Manage Antibodies</h1>
    <caarray:helpPrint/>
    <div class="padme">
        <div id="tabboxwrapper_notabs">
            <div class="boxpad2">
                <h3>
                    <a href="list.action">Antibodies</a> &gt;
                    <span class="dark">
                        <s:if test="${empty antibody.id}">
                            New Antibody
                        </s:if>
                        <s:else>
                            show antibody name, validationid, validationtype, dilution, sample image, result 
                            in table where validation can be deleted see wire
                        </s:else>
                    </span>
                </h3>
                <caarray:successMessages />
            </div>
          
              
                <div id="theForm">
                   
                 
                </div>
            </div>
        </div>
    </div>
</body>
</html>