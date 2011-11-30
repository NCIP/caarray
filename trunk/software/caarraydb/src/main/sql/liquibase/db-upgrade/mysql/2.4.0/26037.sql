-- GF 26037, upgrade to CSM 4.2

-- new CSM table

CREATE TABLE csm_mapping (
  mapping_id bigint(20) NOT NULL auto_increment,
  application_id bigint(20) NOT NULL,
  object_name varchar(100) NOT NULL,
  attribute_name varchar(100) NOT NULL,
  object_package_name varchar(100),
  table_name varchar(100),
  table_name_group varchar(100),
  table_name_user varchar(100),
  view_name_group varchar(100),
  view_name_user varchar(100),
  active_flag tinyint(1) NOT NULL default '0',
  maintained_flag tinyint(1) NOT NULL default '0',	
  update_date date default '0000-00-00',
  PRIMARY KEY(mapping_id)
)Type=InnoDB
;

ALTER TABLE csm_mapping ADD CONSTRAINT fk_pe_application_id 
  FOREIGN KEY (APPLICATION_ID) REFERENCES csm_application (APPLICATION_ID) ON DELETE CASCADE
;
ALTER TABLE csm_mapping
  ADD CONSTRAINT uq_mp_obj_name_attri_name_app_id UNIQUE (OBJECT_NAME,ATTRIBUTE_NAME,APPLICATION_ID)
;
ALTER TABLE csm_protection_element ADD INDEX idx_obj_attr_app (OBJECT_ID, ATTRIBUTE, APPLICATION_ID)
;

ALTER TABLE csm_application add column CSM_VERSION varchar(20);

-- our security caching tables

-- project 

insert into csm_mapping (application_id, active_flag, maintained_flag, object_package_name, object_name, attribute_name, table_name, view_name_user, view_name_group, table_name_user, table_name_group, update_date)
  values (2, 1, 1, 'gov.nih.nci.caarray.domain.project', 'Project', 'id', 'csm_pei_project_id', 'csm_vw_project_id_user', 'csm_vw_project_id_group', 'csm_project_id_user', 'csm_project_id_group', '2009-08-20');
  
CREATE TABLE IF NOT EXISTS  csm_pei_project_id (
   ATTRIBUTE_VALUE bigint(20) NOT NULL, 
   PROTECTION_ELEMENT_ID bigint(20) NOT NULL, 
   PRIMARY KEY  (PROTECTION_ELEMENT_ID),
   UNIQUE KEY UQ_MP_OBJ_NAME_ATTRI_VAL_APP_ID (PROTECTION_ELEMENT_ID,ATTRIBUTE_VALUE)
)Type=MyISAM;

CREATE TABLE IF NOT EXISTS csm_project_id_group (
  GROUP_ID int NOT NULL, 
  PRIVILEGE_ID tinyint NOT NULL,
  ATTRIBUTE_VALUE bigint(20) NOT NULL, 
  PRIMARY KEY GRPID_APID_PRIV (GROUP_ID,PRIVILEGE_ID,ATTRIBUTE_VALUE), 
  key idx_attribute_value(attribute_value)  
)Type=MyISAM;

create or replace view csm_vw_project_id_group as
  select ugrpg.group_id, crp.privilege_id, pe.attribute_value
  from csm_pei_project_id pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_role_privilege crp 
  where pe.protection_element_id = pgpe.protection_element_id and pgpe.protection_group_id = ugrpg.protection_group_id
    and ugrpg.role_id = crp.role_id;
					
-- sample

insert into csm_mapping (application_id, active_flag, maintained_flag, object_package_name, object_name, attribute_name, table_name, view_name_user, view_name_group, table_name_user, table_name_group, update_date)
  values (2, 1, 1, 'gov.nih.nci.caarray.domain.sample', 'Sample', 'id', 'csm_pei_sample_id', 'csm_vw_sample_id_user', 'csm_vw_sample_id_group', 'csm_sample_id_user', 'csm_sample_id_group', '2009-08-20');
  
CREATE TABLE IF NOT EXISTS csm_pei_sample_id (
   ATTRIBUTE_VALUE bigint(20) NOT NULL, 
   PROTECTION_ELEMENT_ID bigint(20) NOT NULL, 
   PRIMARY KEY  (PROTECTION_ELEMENT_ID),
   UNIQUE KEY UQ_MP_OBJ_NAME_ATTRI_VAL_APP_ID (PROTECTION_ELEMENT_ID,ATTRIBUTE_VALUE)
)Type=MyISAM;

CREATE TABLE IF NOT EXISTS csm_sample_id_group (
  GROUP_ID int NOT NULL, 
  PRIVILEGE_ID tinyint NOT NULL,
  ATTRIBUTE_VALUE bigint(20) NOT NULL, 
  PRIMARY KEY GRPID_APID_PRIV (GROUP_ID,PRIVILEGE_ID,ATTRIBUTE_VALUE),
  key idx_attribute_value(attribute_value)  
)Type=MyISAM;

create or replace view csm_vw_sample_id_group as
  select ugrpg.group_id, crp.privilege_id, pe.attribute_value
  from csm_pei_sample_id pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_role_privilege crp 
  where pe.protection_element_id = pgpe.protection_element_id and pgpe.protection_group_id = ugrpg.protection_group_id
    and ugrpg.role_id = crp.role_id;

-- update filters
  
update csm_filter_clause 
set generated_sql_group='ID in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=2)',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.project.Project';

update csm_filter_clause 
set generated_sql_group='ID in (select ex.ID from experiment ex inner join project p on ex.id = p.experiment where p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=2))',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.project.Experiment';

update csm_filter_clause 
set generated_sql_group='ID in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_group g, csm_role_privilege rp, csm_role r, csm_privilege p where pe.object_id= ''gov.nih.nci.caarray.domain.permissions.CollaboratorGroup'' and pe.attribute=''id'' and g.group_id in (:GROUP_NAMES) and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__)',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.permissions.CollaboratorGroup';

update csm_filter_clause 
set generated_sql_group='ID in (select h.ID from hybridization h inner join labeledextracthybridization lhe on h.id = lhe.hybridization_id inner join extractlabeledextract ele on lhe.labeledextract_id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = se.extract_id inner join biomaterial s on se.sample_id = s.id where s.ID in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3))',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.hybridization.Hybridization';

update csm_filter_clause 
set generated_sql_group='ID in (select f.ID from factor f inner join experiment ex on f.experiment = ex.id left join project p on ex.id = p.experiment where p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3))',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.project.Factor';

update csm_filter_clause 
set generated_sql_group='ID in (select ad.id from arraydata ad left join rawarraydata_hybridizations radh on ad.id = radh.rawarraydata_id left join hybridization h on radh.hybridization_id = h.id left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id left join hybridization h2 on dadh.hybridization_id = h2.id left join labeledextracthybridization leh on h.id = leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where s.id is not null and s.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or s2.id is not null and s2.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3))',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.data.AbstractArrayData';

update csm_filter_clause 
set generated_sql_group=concat('ID in (select b.ID from biomaterial b left join experiment ex on b.experiment_id = ex.id left join project p on ex.id = p.experiment left join sampleextract se on b.id = se.extract_id left join biomaterial b2 on b2.id = se.sample_id left join extractlabeledextract ele on b.id = ele.labeledextract_id left join sampleextract se2 on ele.extract_id = se2.extract_id left join biomaterial b3 on b3.id = se2.sample_id where ',
'b.discriminator=''SO'' and p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
'b.discriminator = ''SA'' and b.ID in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) OR ',
'b2.discriminator = ''SA'' and b2.ID in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) OR ',
'b3.discriminator=''SA'' and b3.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3))'),
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.sample.AbstractBioMaterial';

update csm_filter_clause 
set generated_sql_group=concat('ID in (select f.id from caarrayfile f left join arraydata ad on f.id = ad.data_file left join project p on f.project = p.id left join rawarraydata_hybridizations radh on ad.id = radh.rawarraydata_id left join hybridization h on radh.hybridization_id = h.id left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id left join hybridization h2 on dadh.hybridization_id = h2.id left join labeledextracthybridization leh on h.id = leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where ',
's.id is not null and s.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
's2.id is not null and s2.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
'(f.status = ''SUPPLEMENTAL'' or f.status = ''IMPORTED'' or f.status = ''IMPORTED_NOT_PARSED'') and s.id is null and s2.id is null and p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
'p.id in  (select t.attribute_value from csm_project_id_group t join csm_group g on t.group_id = g.group_id where t.group_id IN (:GROUP_NAMES) and g.group_name like ''__selfgroup__%'' and t.privilege_id=3) or ',
's.id is null and s2.id is null and p.id is null)'),
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.file.CaArrayFile';

-- initial population of temp tables

insert into csm_pei_project_id (protection_element_id, attribute_value) 
  select protection_element_id, attribute_value from csm_protection_element pe
    where  pe.object_id = 'gov.nih.nci.caarray.domain.project.Project' and  pe.attribute = 'id' and  pe.application_id = 2 and pe.attribute_value is not null;
							
insert into csm_project_id_group (group_id,privilege_id,attribute_value) 
  select distinct group_id,privilege_id,attribute_value from csm_vw_project_id_group;
	
insert into csm_pei_sample_id (protection_element_id, attribute_value) 
  select protection_element_id, attribute_value from csm_protection_element pe
    where  pe.object_id = 'gov.nih.nci.caarray.domain.sample.Sample' and  pe.attribute = 'id' and  pe.application_id = 2 and pe.attribute_value is not null;
							
insert into csm_sample_id_group (group_id,privilege_id,attribute_value) 
  select distinct group_id,privilege_id,attribute_value from csm_vw_sample_id_group where group_id is not null;
