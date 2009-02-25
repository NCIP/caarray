<%@ tag display-name="protocolSelectorAjaxInit" description="Renders the the protocol selector ajax code." body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<script type="text/javascript">
    startProtocolLookup = function() {
        $("progressMsg").show();
    }

    formUpdated = false;
    finishProtocolLookup = function() {
        Effect.Fade("progressMsg");
        if (!formUpdated) {
            items = $('projectForm_protocol').getElementsBySelector('option[value=${protocol.id}]');
            if (items.length > 0) {
                items[0].selected = true;
            }
            TabUtils.updateSavedFormData();
            formUpdated = true;
        }
    }
</script>

<c:url var="getProtocolUrl" value="/ajax/project/tab/Samples/retrieveXmlProtocolList.action" />
<c:if test="${editMode}">
    <ajax:select baseUrl="${getProtocolUrl}"
        source="projectForm_protocolType" target="projectForm_protocol" executeOnLoad="true"
        parameters="protocolType={projectForm_protocolType}"
        preFunction="startProtocolLookup" postFunction="finishProtocolLookup" />
</c:if>