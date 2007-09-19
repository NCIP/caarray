<%@ include file="/common/taglibs.jsp" %>

<div id="sidebar" class="homepage">
    <h1>Actions</h1>
    <s:form id="mainMenu">
    <table>
        <tr>
            <td>
                <a id="manageFiles" href='<c:out value="${requestScope.manageFiles.value}" />'>
                    <c:out value="${requestScope.manageFiles.label}" />
                </a>
            </td>
        </tr>
        <tr>
            <td>
                <a id="workspace" href='<c:out value="${requestScope.workspace.value}" />'>
                    <c:out value="${requestScope.workspace.label}" />
                </a>
            </td>
        </tr>
         <tr>
            <td>
                <a id="proposeProject" href='<c:out value="${requestScope.proproseProject.value}" />'>
                    <c:out value="${requestScope.project.label}" />
                </a>
            </td>
        </tr>
    </table>
    </s:form>
    <h1 style="border-top:1px solid #fff;">What's New</h1>
    <p class="small">
        caArray 2.0 software is available for download now. This installation features a new interface and increased functionality.<br />
        <a href="#">Download caArray 2.0 &gt;&gt;</a><br />
        <a href="#">Release Notes &gt;&gt;</a>
    </p>
</div>
