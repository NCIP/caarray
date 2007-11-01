<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:set var="newGroupRow">
    <caarray:projectAccessProfileOwner name="Collaboration Group: ${collaboratorGroup.group.groupName}" 
        description="Control access to the experiment by members of this group"/>
</c:set>
<script type="text/javascript">
    new Effect.Highlight('access_profile_owners');
</script>