-- This script is is a slightly modified version of the AuthSchemaMySQL.sql
-- file from the CSM 4.1 distribution.  It has been modified to use our replacements
-- and not to drop the db itself, and the order of the drops was reversed.

USE @database.name@;

DROP TABLE IF EXISTS csm_user_pe
;

DROP TABLE IF EXISTS csm_user_group_role_pg
;

DROP TABLE IF EXISTS csm_user_group
;

DROP TABLE IF EXISTS csm_user
;

DROP TABLE IF EXISTS csm_role_privilege
;

DROP TABLE IF EXISTS csm_role
;

DROP TABLE IF EXISTS csm_pg_pe
;

DROP TABLE IF EXISTS csm_protection_group
;

DROP TABLE IF EXISTS csm_protection_element
;

DROP TABLE IF EXISTS csm_filter_clause
;

DROP TABLE IF EXISTS csm_privilege
;

DROP TABLE IF EXISTS csm_group
;

DROP TABLE IF EXISTS csm_application
;

