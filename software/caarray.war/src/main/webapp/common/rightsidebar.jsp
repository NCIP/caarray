<%@ include file="/common/taglibs.jsp" %>

<div id="sidebar" class="homepage">
    <h1>Actions</h1>
    <menu:useMenuDisplayer name="Velocity" config="/template/velocity/rightbarMenu.vm">
        <s:if test="menu == 'ProjectListLinks'">
            <menu:displayMenu name="ProjectListLinks" />
        </s:if>
        <s:elseif test="menu == 'ProjectCreateLinks'">
            <menu:displayMenu name="ProjectCreateLinks" />
        </s:elseif>
        <s:elseif test="menu == 'ProjectSaveLinks'">
            <menu:displayMenu name="ProjectSaveLinks" />
        </s:elseif>
        <s:elseif test="menu == 'FileEditLinks'">
            <menu:displayMenu name="FileEditLinks" />
        </s:elseif>
        <s:elseif test="menu == 'FileManageLinks'">
            <menu:displayMenu name="FileManageLinks" />
        </s:elseif>
        <s:elseif test="menu == 'FileMessagesLinks'">
            <menu:displayMenu name="FileMessagesLinks" />
        </s:elseif>
    </menu:useMenuDisplayer>
    <h1 style="border-top:1px solid #fff;">What's New</h1>
    <p class="small">
        caArray 2.0 software is available for download now. This installation features a new interface and increased functionality.<br />
        <a href="#">Download caArray 2.0 &gt;&gt;</a><br />
        <a href="#">Release Notes &gt;&gt;</a>
    </p>
</div>
