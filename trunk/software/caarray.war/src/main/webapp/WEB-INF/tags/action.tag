<%@ tag display-name="actions" description="Renders the actions at the bottom of a tab." body-content="empty"%>

<%@ attribute name="onclick" required="false"%>
<%@ attribute name="url" required="false"%>
<%@ attribute name="actionClass" required="true"%>
<%@ attribute name="text" required="true"%>
<%@ attribute name="tabindex" required="false"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="caarray" %>

<li><caarray:linkButton actionClass="${actionClass}" text="${text}" url="${url}" tabindex="${tabindex}" onclick="${onclick}"  /></li>