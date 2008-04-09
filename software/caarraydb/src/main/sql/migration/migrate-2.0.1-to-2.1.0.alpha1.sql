-- defects 11716/12440
create table biomaterial_protocol_application (bio_material bigint not null, protocol_application bigint not null, protocol_order integer not null, primary key (bio_material, protocol_order), unique (protocol_application)) type=InnoDB;
alter table biomaterial_protocol_application add index biomaterial_protocol_application_protocol_application_fk (protocol_application), add constraint biomaterial_protocol_application_protocol_application_fk foreign key (protocol_application) references protocol_application (id);
alter table biomaterial_protocol_application add index biomaterial_protocol_application_bio_material_fk (bio_material), add constraint biomaterial_protocol_application_bio_material_fk foreign key (bio_material) references biomaterial (id);
create table hybridization_protocol_application (hybridization bigint not null, protocol_application bigint not null, protocol_order integer not null, primary key (hybridization, protocol_order), unique (protocol_application)) type=InnoDB;
alter table hybridization_protocol_application add index hybridization_protocol_application_protocol_application_fk (protocol_application), add constraint hybridization_protocol_application_protocol_application_fk foreign key (protocol_application) references protocol_application (id);
alter table hybridization_protocol_application add index hybridization_protocol_application_hybridization_fk (hybridization), add constraint hybridization_protocol_application_hybridization_fk foreign key (hybridization) references hybridization (id);
insert into hybridization_protocol_application (hybridization, protocol_application, protocol_order) select id, protocol_application, 0 from hybridization where protocol_application is not null;
insert into biomaterial_protocol_application (bio_material, protocol_application, protocol_order) select pa.bio_material, pa.id, (select count(*) from protocol_application pa2 where pa2.bio_material = pa.bio_material and pa2.id < pa.id) from protocol_application pa where pa.bio_material is not null;
alter table hybridization drop foreign key hybridization_protocolapp_fk;
alter table hybridization drop column protocol_application;
alter table protocol_application drop foreign key protocolapp_biomaterial;
alter table protocol_application drop column bio_material;
