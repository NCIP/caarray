-- GF 26323, workspace performance fix

-- give all privileges to sysadmin for all protection groups

insert into csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
  select g.group_id, r.role_id, pg.protection_group_id, now() from csm_group g, csm_role r, csm_protection_group pg 
    where g.group_name='SystemAdministrator' and r.role_name in ('Read', 'Write', 'Access', 'Permissions');

-- and refresh the temp tables

truncate csm_pei_project_id;
insert into csm_pei_project_id (protection_element_id, attribute_value) 
  select protection_element_id, attribute_value from csm_protection_element pe
    where  pe.object_id = 'gov.nih.nci.caarray.domain.project.Project' and  pe.attribute = 'id' and  pe.application_id = 2 and pe.attribute_value is not null;
							
truncate csm_project_id_group;
insert into csm_project_id_group (group_id,privilege_id,attribute_value) 
  select distinct group_id,privilege_id,attribute_value from csm_vw_project_id_group;

truncate csm_pei_sample_id;
insert into csm_pei_sample_id (protection_element_id, attribute_value) 
  select protection_element_id, attribute_value from csm_protection_element pe
    where  pe.object_id = 'gov.nih.nci.caarray.domain.sample.Sample' and  pe.attribute = 'id' and  pe.application_id = 2 and pe.attribute_value is not null;
	
truncate csm_sample_id_group;
insert into csm_sample_id_group (group_id,privilege_id,attribute_value) 
  select distinct group_id,privilege_id,attribute_value from csm_vw_sample_id_group;
