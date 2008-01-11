-- BETA6 -> BETA7 PRE-migration script
-- v0.1
-- Todd C. Parnell

-- Run this script after to doing the AHP deployment.  This script restores information about
-- users and groups that have been provisioned in the application.

insert into csm_user
select * from csm_user_backup;

insert into csm_user_group
select * from csm_user_group_backup;

drop table csm_user_backup;
drop table csm_user_group_backup;
commit;