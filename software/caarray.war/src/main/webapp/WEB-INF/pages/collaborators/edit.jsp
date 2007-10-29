<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content">
        <h1>Manage Collaboration Group</h1>
        <div class="padme">
        	<div class="boxpad2">
        		<h3>
        			<a href="listGroups.action">Collaboration Groups</a> &gt;
        			<span class="dark">Add a New Collaboration Group</span>
        		</h3>
        	</div>
        	<div class="boxpad" style="padding-bottom: 10px;">
        		<p class="instructions" style="margin-bottom: 10px;">Choose a name from the new group.</p>
        		<s:form action="/protected/collaborators/create.action" cssClass="form" id="newGroupForm">
        			<s:textfield required="true" name="groupName" key="collaboration.group.name" size="50" tabindex="1"/>
        		</s:form>
       			<a href="#" onclick="javascript:document.getElementById('newGroupForm').submit();"><span class="btn_img, save">Save</span></a>
        	</div>
		</div>
    </div>
 </body>