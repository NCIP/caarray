
insert into csm_pg_pe (
    protection_group_id, protection_element_id, update_date
) select
    (select protection_group_id from csm_protection_group where protection_group_name = 'UPT_UI_GROUPS_LINK'), 
    (select protection_element_id from csm_protection_element where protection_element_name = 'Group Element CAA'), 
    curdate()
from dual 
where exists (select protection_group_id from csm_protection_group where protection_group_name = 'UPT_UI_GROUPS_LINK')    
on duplicate key update pg_pe_id = pg_pe_id;
