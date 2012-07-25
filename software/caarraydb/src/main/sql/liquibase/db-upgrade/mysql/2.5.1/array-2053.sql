SET unique_checks=0;
alter table csm_pei_project_id ENGINE=InnoDB;
alter table csm_pei_sample_id ENGINE=InnoDB;
alter table csm_project_id_group ENGINE=InnoDB;
alter table csm_sample_id_group ENGINE=InnoDB;
SET unique_checks=1;