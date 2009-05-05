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

-- Implementation of Items 18907 and 19034 - separate project locking, and publication.
alter table project change column public_id_locked locked bit not null;
update project set locked = (status = 'PUBLIC');
alter table project drop column status;

-- GF 19061 - fixing factor value, parameter value, and characterstic class models

-- factor values
-- basic algorithm: values with units that match the regexp for decimals become measurement_values
-- values with units that do not match the regexp for decimals become userdef_values
-- values without units whose value matches an existing term become term_values
-- values without units whose value does not match an existing term become userdef_values

create table measurement_factor_value (
  factor_value_id bigint(20) not null,
  value float default null,
  primary key  (factor_value_id)
) engine=innodb;

create table term_factor_value (
  factor_value_id bigint(20) not null,
  term bigint(20) not null,
  primary key  (factor_value_id)
) engine=innodb;

create table userdef_factor_value (
  factor_value_id bigint(20) not null,
  value text default null,
  primary key  (factor_value_id)
) engine=innodb;

alter table measurement_factor_value add index FK6712F6245E52D528 (factor_value_id), add constraint FK6712F6245E52D528 foreign key (factor_value_id) references factor_value (id);
alter table term_factor_value add index term_fv_term_fk (term), add constraint term_fv_term_fk foreign key (term) references term (id);
alter table term_factor_value add index FKA2132B745E52D528 (factor_value_id), add constraint FKA2132B745E52D528 foreign key (factor_value_id) references factor_value (id);
alter table userdef_factor_value add index FK3007C2465E52D528 (factor_value_id), add constraint FK3007C2465E52D528 foreign key (factor_value_id) references factor_value (id);

insert into measurement_factor_value (factor_value_id, value) select id, value from factor_value where unit is not null and value regexp '^[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?$';
insert into userdef_factor_value (factor_value_id, value) select id, value from factor_value where unit is not null and value not regexp '^[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?$';
insert into term_factor_value (factor_value_id, term) select fv.id, t.id from factor_value fv join term t on fv.value = t.value where fv.unit is null group by fv.id;
insert into userdef_factor_value (factor_value_id, value) select fv.id, fv.value from factor_value fv left join term t on fv.value = t.value where fv.unit is null and t.id is null;

alter table factor_value drop column value;

-- parameter values
-- basic algorithm is same as for factor values, except the unit in parameter_value must be changed from a text field
-- to a foreign key to a term. for any units that do not match any existing term, new terms are created in the caArray term source

create table measurement_parameter_value (
  parameter_value_id bigint(20) not null,
  value float default null,
  primary key  (parameter_value_id)
) engine=innodb;

create table term_parameter_value (
  parameter_value_id bigint(20) not null,
  term bigint(20) not null,
  primary key  (parameter_value_id)
) engine=innodb;

create table userdef_parameter_value (
  parameter_value_id bigint(20) not null,
  value text default null,
  primary key  (parameter_value_id)
) engine=innodb;

alter table parameter_value change unit unit_text VARCHAR (4000);
alter table parameter_value add column unit bigint(20);
alter table parameter_value add index pv_unit_fk (unit), add constraint pv_unit_fk foreign key (unit) references term (id);
alter table measurement_parameter_value add index FKE0EFBED87518F0E3 (parameter_value_id), add constraint FKE0EFBED87518F0E3 foreign key (parameter_value_id) references parameter_value (id);
alter table term_parameter_value add index term_pv_term_fk (term), add constraint term_pv_term_fk foreign key (term) references term (id);
alter table term_parameter_value add index FKDE2BC7887518F0E3 (parameter_value_id), add constraint FKDE2BC7887518F0E3 foreign key (parameter_value_id) references parameter_value (id);
alter table userdef_parameter_value add index FK6046E7767518F0E3 (parameter_value_id), add constraint FK6046E7767518F0E3 foreign key (parameter_value_id) references parameter_value (id);

insert into term (value, source) select pv.unit, ts.id from parameter_value pv left join term t on pv.unit = t.value, term_source ts where t.id is null and pv.unit is not null and ts.name='caArray' and ts.version='2.0';
update parameter_value pv, term t set pv.unit = t.id where pv.unit_text = t.value and pv.unit_text is not null;
insert into measurement_parameter_value (parameter_value_id, value) select id, value from parameter_value where unit is not null and value regexp '^[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?$';
insert into userdef_parameter_value (parameter_value_id, value) select id, value from parameter_value where unit is not null and value not regexp '^[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?$';
insert into term_parameter_value (parameter_value_id, term) select pv.id, t.id from parameter_value pv join term t on pv.value = t.value where pv.unit is null group by pv.id;
insert into userdef_parameter_value (parameter_value_id, value) select pv.id, pv.value from parameter_value pv left join term t on pv.value = t.value where pv.unit is null and t.id is null;

alter table parameter_value drop column unit_text,
drop column value;

-- characteristics
-- here we are only adding the userdef characteristic type (which will not have any rows initially), and moving the unit column
-- from characteristic_measurement to characteristic

create table characteristic_userdef (
  characteristic_id bigint(20) not null,
  value text default null,
  primary key  (characteristic_id)
) engine=innodb;

alter table characteristic add column unit bigint(20);
alter table characteristic add index characteristic_unit_fk (unit), add constraint characteristic_unit_fk foreign key (unit) references term (id);
alter table characteristic_userdef add index FK35FE5DB6C776BCA4 (characteristic_id), add constraint FK35FE5DB6C776BCA4 foreign key (characteristic_id) references characteristic (id);

update characteristic, characteristic_measurement set characteristic.unit = characteristic_measurement.unit where characteristic.id = characteristic_measurement.characteristic_id;

alter table characteristic_measurement drop column unit,
drop foreign key characteristic_measurement_unit_fk;