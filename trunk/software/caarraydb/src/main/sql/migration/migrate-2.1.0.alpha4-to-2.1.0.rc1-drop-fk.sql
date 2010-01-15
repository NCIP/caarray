-- Since older DBs might still have capital letters in older tables, we need to try to drop foreign keys
-- using either all lower-case or all upper-case, to handle both older and newer DBs.
-- This script should be run with 'ignoreErrors="true"' in db-migrations.xml.

-- silver compliance updates
alter table arrayreporter drop foreign key arrayreporter_microarray_fk;
alter table arrayreporter drop foreign key ARRAYREPORTER_MICROARRAY_FK;
alter table array_design drop foreign key arraydesign_microarray_fk;
alter table array_design drop foreign key ARRAYDESIGN_MICROARRAY_FK;

-- there are actually only 2 foreign keys here (on biomaterial.specimen and biomaterial.molecular_specimen)
-- but because all table and column names were changed to all-lowercase, older databases might have slightly
-- different foreign key names
alter table biomaterial drop foreign key FKD7EEE0FB4DF5007;
alter table biomaterial drop foreign key FKD7EEE0F5A375D78;
alter table biomaterial drop foreign key FK8D2B5A2FB4DF5007;
alter table biomaterial drop foreign key FK8D2B5A2F5A375D78;

-- because there's a chance the foreign keys didn't get dropped in the previous lines, drop the columns and table
-- in this script, so the upgrade doesn't fail if these lines fail
alter table biomaterial drop column specimen;
alter table biomaterial drop column molecular_specimen;
drop table specimen;
