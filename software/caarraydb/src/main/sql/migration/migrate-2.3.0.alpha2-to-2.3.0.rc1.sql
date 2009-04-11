-- make csm_filter_clause generated_sql column bigger
ALTER TABLE csm_filter_clause CHANGE generated_sql_user generated_sql_user VARCHAR (8000) NOT NULL;

-- update the file filter
update csm_filter_clause set generated_sql_user = concat('ID in (select f.id from caarrayfile f left join arraydata ad on f.id = ad.data_file left join project p on f.project = p.id left join rawarraydata_hybridizations radh on ad.id = radh.rawarraydata_id left join hybridization h on radh.hybridization_id = h.id left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id left join hybridization h2 on dadh.hybridization_id = h2.id left join labeledextracthybridization leh on h.id = leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where ',
's.id is not null and s.id in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__) or ',
's2.id is not null and s2.id in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__) or ',
'(f.status = ''SUPPLEMENTAL'' or f.status = ''IMPORTED'' or f.status = ''IMPORTED_NOT_PARSED'') and s.id is null and s2.id is null and p.id in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__) or ',
'p.id in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_user_pe upe, csm_protection_element pe, csm_user u where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and upe.protection_element_id = pe.protection_element_id and upe.user_id = u.user_id) __caarray_filter_alias__) or ',
's.id is null and s2.id is null and p.id is null)')
    where class_name = 'gov.nih.nci.caarray.domain.file.CaArrayFile';

-- Implementation Item 13238 - Column assay_type in table experiment is migrated to its own table experiment_assay_types so that multiple assay
--                types can be stored per experiment.
-- Manufacturer is no longer required if an assay type is entered.
alter table experiment modify manufacturer bigint null;
-- create table assay_type where the assay type names and IDs are stored
create table assay_type (id bigint not null auto_increment, bigid varchar(254), lsid_authority varchar(254), lsid_namespace varchar(254), lsid_object_id varchar(254), name varchar(254), primary key (id)) type=InnoDB;
insert into assay_type (name) value ('aCGH');
insert into assay_type (name) value ('Exon');
insert into assay_type (name) value ('Gene Expression');
insert into assay_type (name) value ('Methylation');
insert into assay_type (name) value ('microRNA');
insert into assay_type (name) value ('SNP');

create table experiment_assay_types (experiment bigint not null, assay_types bigint not null, primary key (experiment, assay_types)) type=InnoDB;
alter table experiment_assay_types add index experiment_assaytypes_at_fk (assay_types), add constraint experiment_assaytypes_at_fk foreign key (assay_types) references assay_type (id);
alter table experiment_assay_types add index experiment_assaytypes_exp_fk (experiment), add constraint experiment_assaytypes_exp_fk foreign key (experiment) references experiment (id);
insert into experiment_assay_types (experiment, assay_types) select id, '1' from experiment where assay_type="aCGH";
insert into experiment_assay_types (experiment, assay_types) select id, '2' from experiment where assay_type="exon";
insert into experiment_assay_types (experiment, assay_types) select id, '3' from experiment where assay_type="geneExpression";
insert into experiment_assay_types (experiment, assay_types) select id, '4' from experiment where assay_type="methylation";
insert into experiment_assay_types (experiment, assay_types) select id, '5' from experiment where assay_type="microRNA";
insert into experiment_assay_types (experiment, assay_types) select id, '6' from experiment where assay_type="SNP";
alter table experiment drop column assay_type;

-- Implementation Item 13238 - Column assay_type in table aarray_design is migrated to its own table array_design_assay_types so that multiple assay
--                types can be stored per array design.
create table array_design_assay_types (array_design bigint not null, assay_types bigint not null, primary key (array_design, assay_types)) type=InnoDB;
alter table array_design_assay_types add index array_design_assaytypes_ad_fk (array_design), add constraint array_design_assaytypes_ad_fk foreign key (array_design) references array_design (id);
alter table array_design_assay_types add index array_design_assaytypes_at_fk (assay_types), add constraint array_design_assaytypes_at_fk foreign key (assay_types) references assay_type (id);
insert into array_design_assay_types (array_design, assay_types) select id, '1' from array_design where assay_type="aCGH";
insert into array_design_assay_types (array_design, assay_types) select id, '2' from array_design where assay_type="exon";
insert into array_design_assay_types (array_design, assay_types) select id, '3' from array_design where assay_type="geneExpression";
insert into array_design_assay_types (array_design, assay_types) select id, '4' from array_design where assay_type="methylation";
insert into array_design_assay_types (array_design, assay_types) select id, '5' from array_design where assay_type="microRNA";
insert into array_design_assay_types (array_design, assay_types) select id, '6' from array_design where assay_type="SNP";
alter table array_design drop column assay_type;
