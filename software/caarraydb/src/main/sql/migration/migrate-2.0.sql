update term_source set name='caArray' where name='Caarray';

delete from designelementlist_designelement where designelementlist_id in (select id from design_element_list where lsid_authority = 'Affymetrix.com' and lsid_namespace = 'DesignElementList');
update data_set set design_element_list = null where design_element_list in (select id from design_element_list where lsid_authority = 'Affymetrix.com' and lsid_namespace = 'DesignElementList');
delete from design_element_list where lsid_authority = 'Affymetrix.com' and lsid_namespace = 'DesignElementList';