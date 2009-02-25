-- BETA6 -> BETA7 PRE-migration script
-- v0.1
-- Todd C. Parnell

-- Run this script prior to doing the AHP deployment.  This script preserves information about
-- users and groups that have been provisioned in the application.

-- Strategy:
-- - for users, we'll let the default populate script take care of the db users like caarrayadmin
-- - for groups, we will not migrate collaborator groups.  thus, we don't need to create a group backup table
--
-- This means we'll save off all registered users & the **default** groups that they belong to
-- No need to save off other tables like protection groups or roles; or user relations to those tables

-- Notes:
-- user_id > 9 are new users
-- group_id < 9 are prepopulate groups

alter table designelementlist_designelement drop foreign key DELDEDESIGNELEMENT_FK;
alter table probefeature drop foreign key FKA5329546AF48BA;
alter table probefeature drop foreign key FKA53295463C971A69;
alter table logicalprobe_physicalprobe drop foreign key FKF7B2A821AF48BA;
alter table logicalprobe_physicalprobe drop foreign key FKF7B2A821C0C30D98;

DROP TABLE IF EXISTS dataset_designelement;
DROP TABLE IF EXISTS abstract_design_element;
DROP TABLE IF EXISTS feature;
DROP TABLE IF EXISTS logical_probe;
DROP TABLE IF EXISTS physical_probe;
DROP TABLE IF EXISTS abstract_probe;

DROP TABLE IF EXISTS csm_user_backup;

CREATE TABLE csm_user_backup (
  user_id BIGINT AUTO_INCREMENT  NOT NULL,
  login_name VARCHAR(100) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  organization VARCHAR(100),
  department VARCHAR(100),
  title VARCHAR(100),
  phone_number VARCHAR(15),
  password VARCHAR(100),
  email_id VARCHAR(100),
  start_date DATE,
  end_date DATE,
  update_date DATE,
  PRIMARY KEY(user_id)
)Type=InnoDB
;

insert into csm_user_backup
select * from csm_user where user_id > 9;

DROP TABLE IF EXISTS csm_user_group_backup;

CREATE TABLE csm_user_group_backup (
  user_group_id BIGINT AUTO_INCREMENT  NOT NULL,
  user_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  PRIMARY KEY(user_group_id)
)Type=InnoDB
;

insert into csm_user_group_backup
select * from csm_user_group where group_id < 9 and user_id > 9;

commit;
