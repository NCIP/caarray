<%@ tag display-name="focusFirstElement"
        description="Focus first element of form that is rendered before it is called."
        body-content="empty"%>
<%@ attribute name="formId" required="true"%>
<script type="text/javascript">
    Caarray.focusFirstElement("${formId}");
</script>
