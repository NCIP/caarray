-- create temp table
create temporary table temp_del_tbl
(id bigint(20))
(select id from design_element_list
where id not in (select min(id) from design_element_list group by lsid_object_id) and
id not in (select data_set.design_element_list from data_set where data_set.DESIGN_ELEMENT_LIST is not NULL));

-- delete main group
delete from designelementlist_designelement where designelementlist_id in (select id from temp_del_tbl);
delete from design_element_list where id in (select id from temp_del_tbl);

-- create another temp table to get the non-min duplicates
create temporary table temp_del_nonmin_tbl
(id bigint(20))
(
select id from
(
select id from design_element_list where lsid_object_id in
(
select lsid_object_id lsid from design_element_list group by lsid_object_id having count(*) > 1
)
) multi_lsids where
id not in (select design_element_list from data_set where design_element_list is not NULL)
);

-- delete nonmin duplicates
delete from designelementlist_designelement where designelementlist_id in (select id from temp_del_nonmin_tbl);
delete from design_element_list where id in (select id from temp_del_nonmin_tbl);

-- add constraint
alter table design_element_list add constraint design_element_list_uq unique (lsid_authority, lsid_namespace, lsid_object_id);
