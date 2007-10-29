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
	        		<table class="form">
	        			<tr>
	        				<th colspan="2">Account Details</th>
	        			</tr>
	        			<tr>
	        				<td class="label">Username:</td>
	        				<td class="value">${targetUser.loginName}</td>
	        			</tr>
	        			<tr>
	        				<td class="label">First Name:</td>
	        				<td class="value">${targetUser.firstName}</td>
	        			</tr>
	        			<tr>
	        				<td class="label">Last Name:</td>
	        				<td class="value">${targetUser.lastName}</td>
	        			</tr>
	        			<tr>
	        				<td class="label">Email:</td>
	        				<td class="value">
	        					<c:if test="${!empty targetUser.emailId}">
			        				<a href="mailto:${targetUser.emailId}">${targetUser.emailId}</a>
			        			</c:if>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td class="label">Institution:</td>
	        				<td class="value">${targetUser.organization}</td>
	        			</tr>
	        			<tr>
	        				<td class="label">Phone:</td>
	        				<td class="value">${targetUser.phoneNumber}</td>
	        			</tr>
	        		</table>
	        	</div>
	        	
        	</div>
		</div>
    </div>
 </body>