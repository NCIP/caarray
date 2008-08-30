-- issue 11925
alter table design_element add column vendor_id varchar(255), add index vendor_id_indx (vendor_id);
alter table array_design_design_file drop index design_file;
