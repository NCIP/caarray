<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Plugins</title>
</head>
<body>
    <h1>Installed by me plugins</h1>
    <div class="padme">
    	<table>
    		<tr>
    			<th>Plugin</th><th>Modules</th>
    		</tr>
            <c:forEach var="plugin" items="${plugins}" varStatus="myStatus">
            	<tr>
            	<td>${plugin.key}: ${plugin.name} : ${plugin.class}</td>
            	<td>
	            <c:forEach var="md" items="${plugin.moduleDescriptors}" varStatus="myStatus">            	
					${md.key} - ${md.name} 	            	
	            </c:forEach>
            	</td>
            	</tr>
            </c:forEach>
    	</table>
    </div>
    <h1>Content from plugins</h1>
    <div>
    	<c:forEach items="${panelContents}" var="panelContent">
    		${panelContent}
    		<br/>
    		<hr/>
    	</c:forEach>
    </div>
    <h1>Menus from plugins</h1>
    <div>
                	<c:forEach items="${menuItemContents}" var="menuItemContent">
		                <a href="${menuItemContent.value}">${menuItemContent.label}</a>                	
                	</c:forEach>
    </div>    
</body>
</html>