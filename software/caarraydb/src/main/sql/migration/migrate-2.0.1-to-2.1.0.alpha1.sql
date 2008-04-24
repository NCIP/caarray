-- defects 11716/12440
create table biomaterial_protocol_application (bio_material bigint not null, protocol_application bigint not null, protocol_order integer not null, primary key (bio_material, protocol_order)) type=InnoDB;
alter table biomaterial_protocol_application add index biomaterial_protocol_application_protocol_application_fk (protocol_application), add constraint biomaterial_protocol_application_protocol_application_fk foreign key (protocol_application) references protocol_application (id);
alter table biomaterial_protocol_application add index biomaterial_protocol_application_bio_material_fk (bio_material), add constraint biomaterial_protocol_application_bio_material_fk foreign key (bio_material) references biomaterial (id);
create table hybridization_protocol_application (hybridization bigint not null, protocol_application bigint not null, protocol_order integer not null, primary key (hybridization, protocol_order)) type=InnoDB;
alter table hybridization_protocol_application add index hybridization_protocol_application_protocol_application_fk (protocol_application), add constraint hybridization_protocol_application_protocol_application_fk foreign key (protocol_application) references protocol_application (id);
alter table hybridization_protocol_application add index hybridization_protocol_application_hybridization_fk (hybridization), add constraint hybridization_protocol_application_hybridization_fk foreign key (hybridization) references hybridization (id);
insert into hybridization_protocol_application (hybridization, protocol_application, protocol_order) select id, protocol_application, 0 from hybridization where protocol_application is not null;
insert into biomaterial_protocol_application (bio_material, protocol_application, protocol_order) select pa.bio_material, pa.id, (select count(*) from protocol_application pa2 where pa2.bio_material = pa.bio_material and pa2.id < pa.id) from protocol_application pa where pa.bio_material is not null;
alter table hybridization drop foreign key hybridization_protocolapp_fk;
alter table hybridization drop column protocol_application;
alter table protocol_application drop foreign key protocolapp_biomaterial;
alter table protocol_application drop column bio_material;

-- defect 10653
insert into term (value, source) select 'In Print', term_source.id from term_source where term_source.name='caArray';
insert into term_categories (term_id, category_id) select term.id, category.id from term, category where term.value = 'In Print' and category.name = 'PublicationStatus';

-- defect 13332
update access_profile set security_level = 'NO_VISIBLE' where id in (select public_profile from project where status = 'DRAFT');

-- defect 13010
create table rawarraydata_hybridizations (rawarraydata_id bigint not null, hybridization_id bigint not null, primary key (rawarraydata_id, hybridization_id)) type=InnoDB;
alter table derivedarraydata_derivedfrom drop foreign key derivedfrom_arraydata_fk;
alter table derivedarraydata_derivedfrom drop index derivedfrom_arraydata_fk;
alter table derivedarraydata_derivedfrom drop foreign key derivedfrom_derivedarraydata_fk;
alter table derivedarraydata_derivedfrom drop index derivedfrom_derivedarraydata_fk;
alter table derivedarraydata_derivedfrom add index derivedfrom_arraydata_fk (derivedarraydata_id), add constraint derivedfrom_arraydata_fk foreign key (derivedarraydata_id) references arraydata (id);
alter table derivedarraydata_derivedfrom add index derivedfrom_derivedarraydata_fk (derivedfrom_arraydata_id), add constraint derivedfrom_derivedarraydata_fk foreign key (derivedfrom_arraydata_id) references arraydata (id);
alter table rawarraydata_hybridizations add index rawarraydata_hybridizations_rawarraydata_fk (hybridization_id), add constraint rawarraydata_hybridizations_rawarraydata_fk foreign key (hybridization_id) references hybridization (id);
alter table rawarraydata_hybridizations add index rawarraydata_hybridizations_hybridization_fk (rawarraydata_id), add constraint rawarraydata_hybridizations_hybridization_fk foreign key (rawarraydata_id) references arraydata (id);
insert into rawarraydata_hybridizations (rawarraydata_id, hybridization_id) select id, hybridization from arraydata where hybridization is not null;
alter table arraydata drop foreign key rawdata_hybridization_fk;
alter table arraydata drop column hybridization;
