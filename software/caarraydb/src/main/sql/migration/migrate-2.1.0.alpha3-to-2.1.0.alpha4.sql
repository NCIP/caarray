-- defect 14678
create table  caarraydb.characteristic_measurement (
  characteristic_id bigint(20) not null,
  value float default null,
  unit bigint(20) not null,
  primary key  (characteristic_id),
  key fk_characteristic_measurement_term (unit),
  key (characteristic_id),
  constraint characteristic_measurement_unit_fk foreign key (unit) references term (id)
) engine=innodb default charset=latin1;

create table  caarraydb.characteristic_term (
  characteristic_id bigint(20) not null,
  term bigint(20) not null,
  primary key  (characteristic_id),
  key fk_characteristic_term_term (term),
  key (characteristic_id),
  constraint characteristic_term_term_fk foreign key (term) references term (id)
) engine=innodb default charset=latin1;

insert into characteristic_measurement(characteristic_id, unit, value) select id, unit, value from characteristic where discriminator='MEASUREMENT';
insert into characteristic_term (characteristic_id, term) select id, term from characteristic where discriminator='TERM';

alter table caarraydb.characteristic_measurement add constraint foreign key (characteristic_id) references characteristic (id);
alter table caarraydb.characteristic_term add constraint foreign key (characteristic_id) references characteristic (id);
alter table characteristic drop column discriminator,
 drop column unit,
 drop column term,
 drop column value,
 drop foreign key characteristic_term_fk,
 drop foreign key characteristic_unit_fk;
