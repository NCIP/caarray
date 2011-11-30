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

insert into term (value, source) select pv.unit_text, ts.id from parameter_value pv left join term t on pv.unit_text = t.value, term_source ts where t.id is null and pv.unit_text is not null and ts.name='caArray' and ts.version='2.0';
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