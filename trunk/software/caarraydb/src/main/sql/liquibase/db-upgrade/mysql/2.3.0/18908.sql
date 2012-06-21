-- GF18908 - audit log
create table audit_log_detail (id bigint not null auto_increment, message text, attribute varchar(100) not null, old_value text, new_value text, record bigint not null, primary key (id)) type=InnoDB;
create table audit_log_record (id bigint not null auto_increment, type varchar(255) not null, username varchar(100) not null, entity_name varchar(254) not null, entity_id bigint not null, created_date datetime not null, transaction_id bigint not null, primary key (id)) type=InnoDB;
alter table audit_log_detail add index AUDIT_DEATIL_RECORD_FK (record), add constraint AUDIT_DEATIL_RECORD_FK foreign key (record) references audit_log_record (id);
