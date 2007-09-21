<%@ include file="/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content" class="homepage">
        <h1>Experiment Files</h1>
        <%@ include file="/common/messages.jsp" %>
        <c:if test="${not empty sessionScope.fileEntries}">
        <p>You are managing files for <c:out value="${sessionScope.projectName}" />.</p>
        <s:form action="doImport" method="post">
            <table>
                <tr>
                    <td>Current Files</td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Select Name</td>
                    <td>File Type</td>
                    <td>Status</td>
                </tr>
                <c:forEach items="${sessionScope.fileEntries}" var="fileEntry" varStatus="status">
                <tr>
                    <td><input type="checkbox" id="selected" name="fileEntry:<c:out value='${status.index}'/>:selected" value="<c:out value='${fileEntry.selected}'/>"/></td>
                    <td><c:out value="${fileEntry.caArrayFile.name}"/></td>
                    <td>
                        <select id="type" name="fileEntry:<c:out value='${status.index}'/>:fileType">
                            <c:forEach items="${sessionScope.fileTypes}" var="fileType">
                                <c:choose>
                                    <c:when test="${fileEntry.typeName == fileType.value}">
                                        <option value='<c:out value="${fileType.value}"/>' selected><c:out value="${fileType.label}"/></option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value='<c:out value="${fileType.value}"/>'><c:out value="${fileType.label}"/></option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </td>
                    <td>
                        <c:url var="url" value="viewMessages.action" ><c:param name="projectFile" value="${status.index}" /></c:url>
                        <a href="${url}"><c:out value="${fileEntry.caArrayFile.status}"/></a>
                    </td>
                </tr>
                </c:forEach>
                <tr>
                    <td>
                        <s:submit name="importFile" value="Import" method="importFile" id="importFile" />
                    </td>
                     <td>
                        <s:submit name="validateFile" value="Validate" method="validateFile" id="validateFile" />
                    </td>
                </tr>
            </table>
        </s:form>
        </c:if>
        <s:form action="doUpload" method="post" enctype="multipart/form-data">
            <s:file name="upload" label="File"/>
            <table>
                <tr>
                    <td>
                        <s:submit name="uploadFile" value="Upload" id="uploadFile"/>
                    </td>
                </tr>
            </table>
        </s:form>
    </div>
    <script type="text/javascript">
        Form.focusFirstElement($('uploadForm'));
    </script>
</body>

