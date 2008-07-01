-- defect 14678
create table  characteristic_measurement (
  characteristic_id bigint(20) not null,
  value float default null,
  unit bigint(20) not null,
  primary key  (characteristic_id),
  key fk_characteristic_measurement_term (unit),
  key (characteristic_id),
  constraint characteristic_measurement_unit_fk foreign key (unit) references term (id)
) engine=innodb default charset=latin1;

create table  characteristic_term (
  characteristic_id bigint(20) not null,
  term bigint(20) not null,
  primary key  (characteristic_id),
  key fk_characteristic_term_term (term),
  key (characteristic_id),
  constraint characteristic_term_term_fk foreign key (term) references term (id)
) engine=innodb default charset=latin1;

insert into characteristic_measurement(characteristic_id, unit, value) select id, unit, value from characteristic where discriminator='MEASUREMENT';
insert into characteristic_term (characteristic_id, term) select id, term from characteristic where discriminator='TERM';

alter table characteristic_measurement add constraint FKDCD02B18C776BCA4 foreign key (characteristic_id) references characteristic (id);
alter table characteristic_term add constraint FK76634490C776BCA4 foreign key (characteristic_id) references characteristic (id);
alter table characteristic drop column discriminator,
 drop column unit,
 drop column term,
 drop column value;

-- feature request 11925
create table array_design_design_file (array_design bigint not null, design_file bigint not null, primary key (array_design, design_file), unique (design_file)) type=InnoDB;
alter table array_design_design_file add index design_file_fk (design_file), add constraint design_file_fk foreign key (design_file) references caarrayfile (id);
alter table array_design_design_file add index array_design_fk (array_design), add constraint array_design_fk foreign key (array_design) references array_design (id);
insert into array_design_design_file (array_design, design_file) select id, design_file from array_design where design_file is not null;
alter table array_design drop column design_file;
