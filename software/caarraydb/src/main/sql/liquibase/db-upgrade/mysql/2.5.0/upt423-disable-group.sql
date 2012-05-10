
insert into csm_protection_element (
    protection_element_name, protection_element_description, object_id, attribute,
    attribute_value, protection_element_type, application_id, update_date
) select
    'Group Element CAA', 'Protection element for Group', 'Group Element', null, null, null, 
    (select application_id from csm_application where application_name = 'caarray'), 
    curdate()
from dual 
where not exists (select protection_element_id from csm_protection_element where protection_element_name = 'Group Element CAA')    
on duplicate key update protection_element_id = protection_element_id;


insert into csm_pg_pe (
    protection_group_id, protection_element_id, update_date
) select
    (select protection_group_id from csm_protection_group where protection_group_name = 'UPT_UI_GROUPS_LINK'), 
    (select protection_element_id from csm_protection_element where protection_element_name = 'Group Element CAA'), 
    curdate()
from dual 
where exists (select protection_group_id from csm_protection_group where protection_group_name = 'UPT_UI_GROUPS_LINK')    
on duplicate key update pg_pe_id = pg_pe_id;
