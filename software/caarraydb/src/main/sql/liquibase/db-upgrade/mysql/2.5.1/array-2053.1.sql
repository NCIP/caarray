SET unique_checks=0;
alter table csm_pei_project_id ENGINE=MyISAM;
alter table csm_pei_sample_id ENGINE=MyISAM;
alter table csm_project_id_group ENGINE=MyISAM;
alter table csm_sample_id_group ENGINE=MyISAM;
SET unique_checks=1;