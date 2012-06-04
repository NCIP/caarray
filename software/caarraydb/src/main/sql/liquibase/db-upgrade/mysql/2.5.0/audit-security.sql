create table audit_log_security (id bigint not null auto_increment, entity_name varchar(254) not null, privilege_id tinyint, entity_id bigint not null, record bigint not null, primary key (id)) type=InnoDB;
alter table audit_log_security add index audit_security_record_fk (record), add constraint audit_security_record_fk foreign key (record) references audit_log_record (id);

insert into csm_filter_clause (class_name, filter_chain, target_class_name, application_id, update_date,
    target_class_attribute_name, target_class_attribute_type, generated_sql_user, generated_sql_group)
values ('gov.nih.nci.caarray.domain.audit.AuditLogSecurity', 'gov.nih.nci.caarray.domain.audit.AuditLogSecurity',
        'gov.nih.nci.caarray.domain.audit.AuditLogSecurity - self', 2, sysdate(), 'id', 'java.lang.Long', '1=1',
        concat('ID in (select s.id from audit_log_security s inner join audit_log_record r on s.record = r.id where ',
               '(s.entity_name = ''project'' and (select count(pig.group_id) from csm_project_id_group pig where pig.group_id in (:GROUP_NAMES) ',
               'and pig.privilege_id = s.privilege_id and pig.attribute_value = s.entity_id) > 0) ',
               'or (s.entity_name = ''group'' and s.entity_id in (:GROUP_NAMES)))'));
