<%@ include file="/common/taglibs.jsp" %>

<div id="sidebar" class="homepage">
    <h1>Actions</h1>
    <table>
        <tr>
            <td>
                <a href='<c:out value="${requestScope.manageFiles.value}" />'>
                    <c:out value="${requestScope.manageFiles.label}" />
                </a>
            </td>
        </tr>
        <tr>
            <td>
                <a href='<c:out value="${requestScope.labelValue.value}" />'>
                    <c:out value="${requestScope.labelValue.label}" />
                </a>
            </td>
        </tr>
    </table>
    <h1 style="border-top:1px solid #fff;">What's New</h1>
    <p class="small">
        caArray 2.0 software is available for download now. This installation features a new interface and increased functionality.<br />
        <a href="#">Download caArray 2.0 &gt;&gt;</a><br />
        <a href="#">Release Notes &gt;&gt;</a>
    </p>
</div>
