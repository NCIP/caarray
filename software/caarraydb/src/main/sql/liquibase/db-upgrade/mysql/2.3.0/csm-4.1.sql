-- Upgrade CSM to 4.1
-- This script is is a slightly modified version of the MigrationScript4.1MySQL.sql
-- file from the CSM 4.1 distribution.  It has been modified:
-- (1) to implicitly use the current database, instead of using "USE <<database_name>>;"
-- (2) table/column names change to lowercase
-- (3) generated_sql_group column set to a dummy value

ALTER TABLE csm_user MODIFY COLUMN login_name VARCHAR(500);
ALTER TABLE csm_user ADD COLUMN migrated_flag BOOL NOT NULL DEFAULT 0;
ALTER TABLE csm_user ADD COLUMN premgrt_login_name VARCHAR(100) ;

ALTER TABLE csm_role_privilege DROP COLUMN update_date;
ALTER TABLE csm_user_pe DROP COLUMN update_date;

ALTER TABLE csm_filter_clause CHANGE generated_sql generated_sql_user VARCHAR (4000) NOT NULL;
ALTER TABLE csm_filter_clause ADD COLUMN generated_sql_group VARCHAR (4000);
update csm_filter_clause set generated_sql_group = '1=1';
ALTER TABLE csm_filter_clause MODIFY generated_sql_group VARCHAR (4000) NOT NULL;
