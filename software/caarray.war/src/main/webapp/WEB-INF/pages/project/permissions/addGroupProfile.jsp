<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:url var="loadGroupProfileUrl" value="/protected/ajax/project/permissions/loadGroupProfile.action">
    <c:param name="project.id" value="${project.id}"/>
    <c:param name="collaboratorGroup.id" value="${collaboratorGroup.id}"/>
</c:url>
<c:set var="newGroupRow">
    <caarray:projectAccessProfileOwner loadProfileUrl=""
        name="Collaboration Group: ${collaboratorGroup.group.groupName}" 
        description="Control access to the experiment by members of this group"/>
</c:set>
<script type="text/javascript">
    new Effect.Highlight('access_profile_owners');
</script>