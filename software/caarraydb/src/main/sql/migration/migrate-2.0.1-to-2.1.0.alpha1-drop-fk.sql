-- Since older DBs might still have capital letters in older tables, we need to try to drop foreign keys
-- using either all lower-case or all upper-case, to handle both older and newer DBs.
-- This script should be run with 'ignoreErrors="true"' in db-migrations.xml.

-- defects 11716/12440
alter table hybridization drop foreign key hybridization_protocolapp_fk;
alter table hybridization drop foreign key HYBRIDIZATION_PROTOCOLAPP_FK;
alter table protocol_application drop foreign key protocolapp_biomaterial;
alter table protocol_application drop foreign key PROTOCOLAPP_BIOMATERIAL;

-- defect 13010
alter table derivedarraydata_derivedfrom drop foreign key derivedfrom_arraydata_fk;
alter table derivedarraydata_derivedfrom drop foreign key DERIVEDFROM_ARRAYDATA_FK;
alter table derivedarraydata_derivedfrom drop index derivedfrom_arraydata_fk;
alter table derivedarraydata_derivedfrom drop index DERIVEDFROM_ARRAYDATA_FK;
alter table derivedarraydata_derivedfrom drop foreign key derivedfrom_derivedarraydata_fk;
alter table derivedarraydata_derivedfrom drop foreign key DERIVEDFROM_DERIVEDARRAYDATA_FK;
alter table derivedarraydata_derivedfrom drop index derivedfrom_derivedarraydata_fk;
alter table derivedarraydata_derivedfrom drop index DERIVEDFROM_DERIVEDARRAYDATA_FK;
alter table arraydata drop foreign key rawdata_hybridization_fk;
alter table arraydata drop foreign key RAWDATA_HYBRIDIZATION_FK;
