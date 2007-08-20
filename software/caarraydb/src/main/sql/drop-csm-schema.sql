-- This script is is a slightly modified version of the AuthSchemaMySQL.sql
-- file from the CSM 3.2 distribution.  It has been modified to use our replacements
-- and not to drop the db itself, and the order of the drops was reversed.

USE @database.name@;

DROP TABLE IF EXISTS CSM_USER_PE
;

DROP TABLE IF EXISTS CSM_USER_GROUP_ROLE_PG
;

DROP TABLE IF EXISTS CSM_USER_GROUP
;

DROP TABLE IF EXISTS CSM_USER
;

DROP TABLE IF EXISTS CSM_ROLE_PRIVILEGE
;

DROP TABLE IF EXISTS CSM_ROLE
;

DROP TABLE IF EXISTS CSM_PG_PE
;

DROP TABLE IF EXISTS CSM_PROTECTION_GROUP
;

DROP TABLE IF EXISTS CSM_PROTECTION_ELEMENT
;

DROP TABLE IF EXISTS CSM_PRIVILEGE
;

DROP TABLE IF EXISTS CSM_GROUP
;


DROP TABLE IF EXISTS CSM_APPLICATION
;

