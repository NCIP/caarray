<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<head>
</head>
<body>
    <div id="content">
        <h1>View User Information</h1>
        <div class="padme">
        	<div id="tabboxwrapper_notabs">
	        	<div class="boxpad2">
	        		<h3>
	        			<a href="listGroups.action">Collaboration Groups</a> &gt;
	        			<span class="dark">${targetUser.firstName} ${targetUser.lastName}</span>
	        		</h3>
	        	</div>
	        	<div class="boxpad">
	        		<s:form theme="readonly" cssClass="form">
	        			<s:textfield name="targetUser.loginName" key="username"/>
	        			<s:textfield name="targetUser.firstName" key="firstName"/>
	        			<s:textfield name="targetUser.lastName" key="lastName"/>
	        			<s:textfield name="targetUser.emailId" key="email"/> <%-- TODO: is it possible to make this a mailto link? --%>
	        			<s:textfield name="targetUser.organization" key="institution"/>
	        			<s:textfield name="targetUser.phoneNumber" key="phone"/>
	        		</s:form>
	        	</div>
	        	
        	</div>
		</div>
    </div>
 </body>