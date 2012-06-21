-- issue 13332
delete from csm_user_group_role_pg where protection_group_id in
(select alias_for_ugrpg.protection_group_id from (
select ugrpg.protection_group_id from
csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u,
csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug
where pe.object_id= 'gov.nih.nci.caarray.domain.project.Project' and pe.attribute='id'
and u.login_name='__anonymous__' and pe.application_id=2 and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id
and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id
and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id
and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name='ACCESS' AND pe.attribute_value not in
(select p0.id from project p0 left join access_profile app on p0.public_profile = app.id
where app.security_level in ('READ', 'READ_SELECTIVE', 'VISIBLE', 'WRITE', 'READ_WRITE_SELECTIVE')))
as alias_for_ugrpg)
and (user_id = 1 OR group_id = 1)
and role_id = 2;
