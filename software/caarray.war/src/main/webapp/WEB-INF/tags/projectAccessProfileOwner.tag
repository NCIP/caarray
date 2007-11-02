<%@ tag display-name="projectAccessProfileOwner" 
    description="renders an entry for an owner of an access profile (ie public or a collab group)" 
    body-content="empty"%>

<%@ attribute name="name" required="true"%>
<%@ attribute name="description" required="true"%>
<%@ attribute name="loadProfileUrl" required="true"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<tr>
    <td style="border-left: 0; padding-left: 0">
        <div class="bigbold">${name}</div>
        <p class="nopad">${description}</p>
        <caarray:linkButton actionClass="edit" text="Edit" onclick="loadProfile('${loadProfileUrl}');"/>
    </td>
</tr>
