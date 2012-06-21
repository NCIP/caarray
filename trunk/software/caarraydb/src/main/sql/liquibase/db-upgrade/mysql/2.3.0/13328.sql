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
