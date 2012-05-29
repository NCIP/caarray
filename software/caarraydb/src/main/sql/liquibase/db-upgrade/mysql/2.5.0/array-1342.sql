INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
  VALUES('PARTIAL_READ','This privilege allows the user partial read access to data from an entity. The user may only have access to a subset of data associated with this entity', sysdate());
INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
  VALUES('PARTIAL_WRITE','This privilege allows the user partial write access to data from an entity. The user may only have access to a subset of data associated with this entity', sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date) VALUES('Partial_Read', 'Partial_Read', 2, 1, sysdate());
INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date) VALUES('Partial_Write', 'Partial_Write', 2, 1, sysdate());
INSERT INTO csm_role_privilege (role_id, privilege_id) VALUES(9, 9);
INSERT INTO csm_role_privilege (role_id, privilege_id) VALUES(10, 10);

update csm_filter_clause 
set generated_sql_group=concat('ID in (select f.id from caarrayfile f left join arraydata ad on f.id = ad.data_file ',
'left join project p on f.project = p.id left join arraydata_hybridizations adh on ad.id = adh.arraydata_id ',
'left join hybridization h on adh.hybridization_id = h.id left join labeledextracthybridization leh on h.id = leh.hybridization_id ',
'left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id ',
'left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id where ',
's.id is not null and s.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
's.id is null and p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
'(f.status = ''SUPPLEMENTAL'' or f.status = ''IMPORTED'' or f.status = ''IMPORTED_NOT_PARSED'') and s.id is null and p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id in (3,9)) or ',
'p.id in  (select t.attribute_value from csm_project_id_group t join csm_group g on t.group_id = g.group_id where t.group_id IN (:GROUP_NAMES) and g.group_name like ''__selfgroup__%'' and t.privilege_id=3) or ',
's.id is null and p.id is null)')
where class_name='gov.nih.nci.caarray.domain.file.CaArrayFile';

update csm_filter_clause 
set generated_sql_group='ID in (select f.ID from factor f inner join experiment ex on f.experiment = ex.id left join project p on ex.id = p.experiment where p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id in (3,9)))'
where class_name='gov.nih.nci.caarray.domain.project.Factor';

update csm_filter_clause 
set generated_sql_group=concat('ID in (select b.ID from biomaterial b left join experiment ex on b.experiment_id = ex.id left join project p on ex.id = p.experiment left join sampleextract se on b.id = se.extract_id left join biomaterial b2 on b2.id = se.sample_id left join extractlabeledextract ele on b.id = ele.labeledextract_id left join sampleextract se2 on ele.extract_id = se2.extract_id left join biomaterial b3 on b3.id = se2.sample_id where ',
'b.discriminator=''SO'' and p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id in (3,9)) or ',
'b.discriminator = ''SA'' and b.ID in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) OR ',
'b2.discriminator = ''SA'' and b2.ID in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) OR ',
'b3.discriminator=''SA'' and b3.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3))')
where class_name='gov.nih.nci.caarray.domain.sample.AbstractBioMaterial';

update csm_project_id_group cpig, collaborator_group cg, access_profile ap set cpig.privilege_id=9
where cpig.privilege_id=3 and cpig.group_id = cg.csm_group and cg.id = ap.group_id and cpig.attribute_value = ap.project_id and ap.security_level = 'READ_SELECTIVE';
update csm_project_id_group cpig, collaborator_group cg, access_profile ap set cpig.privilege_id=9
where cpig.privilege_id=3 and cpig.group_id = cg.csm_group and cg.id = ap.group_id and cpig.attribute_value = ap.project_id and ap.security_level = 'READ_WRITE_SELECTIVE';
update csm_project_id_group cpig, collaborator_group cg, access_profile ap set cpig.privilege_id=10
where cpig.privilege_id=4 and cpig.group_id = cg.csm_group and cg.id = ap.group_id and cpig.attribute_value = ap.project_id and ap.security_level = 'READ_WRITE_SELECTIVE';