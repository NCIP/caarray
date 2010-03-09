alter table hybridization_data add column hybridization_index integer;
alter table hybridization_data modify column data_set bigint;
update hybridization_data hd, data_set_hybridization_data_list dshdl set hd.hybridization_index = dshdl.hybridization_index where hd.id = dshdl.hybridization_data_list;
-- drop indices, foreign keys?
drop table data_set_hybridization_data_list;

create table arraydata_hybridizations (arraydata_id bigint not null, hybridization_id bigint not null, primary key (arraydata_id, hybridization_id)) type=InnoDB;
alter table arraydata_hybridizations add index arraydata_hybridizations_arraydata_fk (hybridization_id), add constraint arraydata_hybridizations_arraydata_fk foreign key (hybridization_id) references hybridization (id);
alter table arraydata_hybridizations add index arraydata_hybridizations_hybridization_fk (arraydata_id), add constraint arraydata_hybridizations_hybridization_fk foreign key (arraydata_id) references arraydata (id);

insert into arraydata_hybridizations (arraydata_id, hybridization_id) select derivedarraydata_id, hybridization_id from derivedarraydata_hybridizations;
insert into arraydata_hybridizations (arraydata_id, hybridization_id) select rawarraydata_id, hybridization_id from rawarraydata_hybridizations;

-- drop indices, foreign keys?
drop table derivedarraydata_hybridizations;
drop table rawarraydata_hybridizations;

update csm_filter_clause 
set generated_sql_group='ID in (select ad.id from arraydata ad left join arraydata_hybridizations adh on ad.id = adh.arraydata_id left join hybridization h on adh.hybridization_id = h.id left join labeledextracthybridization leh on h.id = leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id where s.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3))',
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.data.AbstractArrayData';

update csm_filter_clause 
set generated_sql_group=concat('ID in (select f.id from caarrayfile f left join arraydata ad on f.id = ad.data_file left join project p on f.project = p.id left join arraydata_hybridizations adh on ad.id = adh.arraydata_id left join hybridization h on adh.hybridization_id = h.id left join labeledextracthybridization leh on h.id = leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id where ',
's.id is not null and s.id in (select t.attribute_value from csm_sample_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
'(f.status = ''SUPPLEMENTAL'' or f.status = ''IMPORTED'' or f.status = ''IMPORTED_NOT_PARSED'') and s.id is null and p.id in (select t.attribute_value from csm_project_id_group t where t.group_id IN (:GROUP_NAMES) and t.privilege_id=3) or ',
'p.id in  (select t.attribute_value from csm_project_id_group t join csm_group g on t.group_id = g.group_id where t.group_id IN (:GROUP_NAMES) and g.group_name like ''__selfgroup__%'' and t.privilege_id=3) or ',
's.id is null and p.id is null)'),
generated_sql_user='1=1'
where class_name='gov.nih.nci.caarray.domain.file.CaArrayFile';
