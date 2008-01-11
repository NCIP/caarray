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

CREATE TABLE CSM_USER_BACKUP (
  USER_ID BIGINT AUTO_INCREMENT  NOT NULL,
  LOGIN_NAME VARCHAR(100) NOT NULL,
  FIRST_NAME VARCHAR(100) NOT NULL,
  LAST_NAME VARCHAR(100) NOT NULL,
  ORGANIZATION VARCHAR(100),
  DEPARTMENT VARCHAR(100),
  TITLE VARCHAR(100),
  PHONE_NUMBER VARCHAR(15),
  PASSWORD VARCHAR(100),
  EMAIL_ID VARCHAR(100),
  START_DATE DATE,
  END_DATE DATE,
  UPDATE_DATE DATE,
  PRIMARY KEY(USER_ID)
)Type=InnoDB
;

insert into csm_user_backup
select * from csm_user where user_id > 9;

CREATE TABLE CSM_USER_GROUP_BACKUP (
  USER_GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,
  USER_ID BIGINT NOT NULL,
  GROUP_ID BIGINT NOT NULL,
  PRIMARY KEY(USER_GROUP_ID)
)Type=InnoDB
;

insert into csm_user_group_backup
select * from csm_user_group where group_id < 9 and user_id > 9;

commit;
