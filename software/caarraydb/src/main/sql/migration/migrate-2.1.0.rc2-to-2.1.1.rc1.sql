-- issue 11925
alter table design_element add column vendor_id varchar(255);
create index vendor_id_indx on design_element (vendor_id);
alter table array_design_design_file drop index design_file;
