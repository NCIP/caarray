-- Since older DBs might still have capital letters in older tables, we need to try to drop foreign keys
-- using either all lower-case or all upper-case, to handle both older and newer DBs.
-- This script should be run with 'ignoreErrors="true"' in db-migrations.xml.

-- feature request 11925
alter table array_design drop foreign key design_file_fk;
alter table array_design drop foreign key DESIGN_FILE_FK;

alter table characteristic drop foreign key characteristic_term_fk,
 drop foreign key characteristic_unit_fk;

alter table characteristic drop foreign key CHARACTERISTIC_TERM_FK,
 drop foreign key CHARACTERISTIC_UNIT_FK;
