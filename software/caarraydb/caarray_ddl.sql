use caarray2;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS TERM;
DROP TABLE IF EXISTS CATEGORY;
DROP TABLE IF EXISTS ACCESSION;
DROP TABLE IF EXISTS SOURCE;
DROP TABLE IF EXISTS PROTOCOLAPPLICATION;
DROP TABLE IF EXISTS PARAMETERVALUE;
DROP TABLE IF EXISTS PARAMETER;
DROP TABLE IF EXISTS PROTOCOL;
DROP TABLE IF EXISTS CONTACT;
DROP TABLE IF EXISTS PERSONORGANIZATION;
DROP TABLE IF EXISTS ADDRESS;
DROP TABLE IF EXISTS MEASUREMENT;
DROP TABLE IF EXISTS ARRAY;
DROP TABLE IF EXISTS ARRAYDESIGN;
DROP TABLE IF EXISTS REPORTERGROUP;
DROP TABLE IF EXISTS FEATUREGROUP;
DROP TABLE IF EXISTS CAARRAYFILE;
DROP TABLE IF EXISTS PUBLICATION;
DROP TABLE IF EXISTS INVESTIGATIONCONTACT;
DROP TABLE IF EXISTS INVESTIGATIONCONTACTROLE;
DROP TABLE IF EXISTS FACTOR;
DROP TABLE IF EXISTS FACTORVALUE;
DROP TABLE IF EXISTS PROPOSAL;
DROP TABLE IF EXISTS PROJECT;
DROP TABLE IF EXISTS INVESTIGATION;
DROP TABLE IF EXISTS EXPERIMENT;
DROP TABLE IF EXISTS INVESTIGATIONARRAYDESIGN;
DROP TABLE IF EXISTS INVESTIGATIONARRAY;
DROP TABLE IF EXISTS INVESTIGATIONSOURCE;
DROP TABLE IF EXISTS INVESTIGATIONSAMPLE;
DROP TABLE IF EXISTS NORMALIZATIONTYPE;
DROP TABLE IF EXISTS REPLICATETYPE;
DROP TABLE IF EXISTS QUALITYCONTROLTYPE;
DROP TABLE IF EXISTS CHARACTERISTIC;
DROP TABLE IF EXISTS COMPOUND;
DROP TABLE IF EXISTS BIOMATERIAL;
DROP TABLE IF EXISTS SOURCEPROVIDER;
DROP TABLE IF EXISTS SOURCESAMPLE;
DROP TABLE IF EXISTS SAMPLEEXTRACT;
DROP TABLE IF EXISTS EXTRACTLABELEDEXTRACT;

CREATE TABLE TERM
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  DESCRIPTION                 VARCHAR(1000),
  VALUE                       VARCHAR(1000),
  CATEGORY_ID                 BIGINT      NOT NULL,
  ACCESSION_ID                BIGINT,
  SOURCE_ID                   BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE CATEGORY
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  PARENT_ID                   BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE ACCESSION
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  URL                         VARCHAR(1000),
  VALUE                       VARCHAR(1000),
  SOURCE_ID                   BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE SOURCE
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  URL                         VARCHAR(1000),
  VERSION                     VARCHAR(1000),
  PRIMARY KEY                 (ID)
);

CREATE TABLE PROTOCOLAPPLICATION
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  PROTOCOL_ID                 BIGINT      NOT NULL,
  BIOMATERIAL_ID              BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE PARAMETERVALUE
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  UNIT                        VARCHAR(1000),
  VALUE                       VARCHAR(1000),
  PROTOCOL_APPLICATION_ID     BIGINT,
  PARAMETER_ID                BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE PARAMETER
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  PROTOCOL_ID                 BIGINT      NOT NULL,
  DEFAULT_VALUE_ID            BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE PROTOCOL
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  DESCRIPTION                 VARCHAR(1000),
  CONTACT                     VARCHAR(1000),
  HARDWARE                    VARCHAR(1000),
  SOFTWARE                    VARCHAR(1000),
  URL                         VARCHAR(1000),
  TYPE_ID                     BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE CONTACT
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  EMAIL                       VARCHAR(1000),
  FAX                         VARCHAR(1000),
  PHONE                       VARCHAR(1000),
  URL                         VARCHAR(1000),
  ADDRESS_ID                  BIGINT,
  DISCRIMINATOR               VARCHAR(5),
  NAME                        VARCHAR(1000),
  FIRSTNAME                   VARCHAR(1000),
  LASTNAME                    VARCHAR(1000),
  MIDDLEINITIAL               VARCHAR(20),
  PRIMARY KEY                 (ID)
);

CREATE TABLE PERSONORGANIZATION
(
  PERSON_ID                   BIGINT      NOT NULL,
  ORGANIZATION_ID             BIGINT      NOT NULL,
  PRIMARY KEY                 (PERSON_ID, ORGANIZATION_ID)
);

CREATE TABLE ADDRESS
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  CITY                        VARCHAR(1000),
  STATE                       VARCHAR(1000),
  STREETADDRESS1              VARCHAR(1000),
  STREETADDRESS2              VARCHAR(1000),
  ZIPCODE                     VARCHAR(20),
  PRIMARY KEY                 (ID)
);

CREATE TABLE ARRAY
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  BATCH                       VARCHAR(1000),
  SERIALNUMBER                VARCHAR(1000),
  DESIGN_ID                   BIGINT      NOT NULL,
  PRODUCTION_ID               BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE ARRAYDESIGN
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  POLYMERTYPE_ID      BIGINT,
  SUBSTRATETYPE_ID            BIGINT,
  SURFACETYPE_ID              BIGINT,
  TECHNOLOGYTYPE_ID           BIGINT,
  VERSION                     VARCHAR(1000),
  PRINTING_ID                 BIGINT,
  PROVIDER_ID                 BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE CAARRAYFILE
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  PATH                        VARCHAR(1000),
  TYPE                        VARCHAR(1000),
  PROJECT_ID                  BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE PUBLICATION
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  TITLE                       VARCHAR(1000),
  AUTHORS                     VARCHAR(1000),
  DOI                         VARCHAR(1000),
  PUBMEDID                    VARCHAR(1000),
  STATUS_ID                   BIGINT,
  INVESTIGATION_ID            BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE INVESTIGATIONCONTACT
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  CONTACT_ID                  BIGINT,
  INVESTIGATION_ID            BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE INVESTIGATIONCONTACTROLE
(
  INVESTIGATIONCONTACT_ID     BIGINT      NOT NULL,
  ROLE_ID                     BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATIONCONTACT_ID, ROLE_ID)
);

CREATE TABLE FACTOR
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  TYPE_ID                     BIGINT,
  INVESTIGATION_ID            BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE FACTORVALUE
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  FACTOR_ID                   BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE PROPOSAL
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  PROJECT_ID                  BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE PROJECT
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  INVESTIGATION_ID            BIGINT      NOT NULL,
  PRIMARY KEY                 (ID)
);

CREATE TABLE INVESTIGATION
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  TITLE                       VARCHAR(1000),
  DESCRIPTION                 VARCHAR(1000),
  DATEOFEXPERIMENT            DATETIME,
  PUBLICRELEASEDATE           DATETIME,
  PRIMARY KEY                 (ID)
);

CREATE TABLE INVESTIGATIONARRAYDESIGN
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  ARRAYDESIGN_ID              BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, ARRAYDESIGN_ID)
);

CREATE TABLE INVESTIGATIONARRAY
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  ARRAY_ID                    BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, ARRAY_ID)
);

CREATE TABLE INVESTIGATIONSOURCE
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  SOURCE_ID                   BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, SOURCE_ID)
);

CREATE TABLE INVESTIGATIONSAMPLE
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  SAMPLE_ID                   BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, SAMPLE_ID)
);

CREATE TABLE NORMALIZATIONTYPE
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  TERM_ID                     BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, TERM_ID)
);

CREATE TABLE REPLICATETYPE
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  TERM_ID                     BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, TERM_ID)
);

CREATE TABLE QUALITYCONTROLTYPE
(
  INVESTIGATION_ID            BIGINT      NOT NULL,
  TERM_ID                     BIGINT      NOT NULL,
  PRIMARY KEY                 (INVESTIGATION_ID, TERM_ID)
);

CREATE TABLE CHARACTERISTIC
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  VALUE                       VARCHAR(1000),
  TERM_ID                     BIGINT,
  UNIT_ID                     BIGINT,
  BIOMATERIAL_ID              BIGINT,
  PRIMARY KEY                 (ID)
);

CREATE TABLE COMPOUND
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  PRIMARY KEY                 (ID)
);

CREATE TABLE BIOMATERIAL
(
  ID                          BIGINT      NOT NULL,
  LSIDAUTHORITY               VARCHAR(1000),
  LSIDNAMESPACE               VARCHAR(1000),
  LSIDOBJECTID                VARCHAR(1000),
  NAME                        VARCHAR(1000),
  DESCRIPTION                 VARCHAR(1000),
  MATERIAL_TYPE_ID            BIGINT,
  LABEL_ID                    BIGINT,
  DISCRIMINATOR               VARCHAR(5),
  PRIMARY KEY                 (ID)
);

CREATE TABLE SOURCEPROVIDER
(
  SOURCE_ID                   BIGINT      NOT NULL,
  CONTACT_ID                  BIGINT      NOT NULL,
  PRIMARY KEY                 (SOURCE_ID, CONTACT_ID)
);

CREATE TABLE SOURCESAMPLE
(
  SOURCE_ID                   BIGINT      NOT NULL,
  SAMPLE_ID                   BIGINT      NOT NULL,
  PRIMARY KEY                 (SOURCE_ID, SAMPLE_ID)
);

CREATE TABLE SAMPLEEXTRACT
(
  SAMPLE_ID                   BIGINT      NOT NULL,
  EXTRACT_ID                  BIGINT      NOT NULL,
  PRIMARY KEY                 (SAMPLE_ID, EXTRACT_ID)
);

CREATE TABLE EXTRACTLABELEDEXTRACT
(
  EXTRACT_ID                  BIGINT      NOT NULL,
  LABELEDEXTRACT_ID           BIGINT      NOT NULL,
  PRIMARY KEY                 (EXTRACT_ID, LABELEDEXTRACT_ID)
);

ALTER TABLE TERM
  ADD FOREIGN KEY(CATEGORY_ID)
  REFERENCES CATEGORY(ID);

ALTER TABLE TERM
  ADD FOREIGN KEY(ACCESSION_ID)
  REFERENCES ACCESSION(ID);

ALTER TABLE TERM
  ADD FOREIGN KEY(SOURCE_ID)
  REFERENCES SOURCE(ID);

ALTER TABLE CATEGORY
  ADD FOREIGN KEY(PARENT_ID)
  REFERENCES CATEGORY(ID);

ALTER TABLE ACCESSION
  ADD FOREIGN KEY(SOURCE_ID)
  REFERENCES SOURCE(ID);

ALTER TABLE PROTOCOLAPPLICATION
  ADD FOREIGN KEY(PROTOCOL_ID)
  REFERENCES PROTOCOL(ID);

ALTER TABLE PROTOCOLAPPLICATION
  ADD FOREIGN KEY(BIOMATERIAL_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE PARAMETERVALUE
  ADD FOREIGN KEY(PROTOCOL_APPLICATION_ID)
  REFERENCES PROTOCOLAPPLICATION(ID);

ALTER TABLE PARAMETERVALUE
  ADD FOREIGN KEY(PARAMETER_ID)
  REFERENCES PARAMETER(ID);

ALTER TABLE PARAMETER
  ADD FOREIGN KEY(PROTOCOL_ID)
  REFERENCES PROTOCOL(ID);

ALTER TABLE PROTOCOL
  ADD FOREIGN KEY(TYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE CONTACT
  ADD FOREIGN KEY(ADDRESS_ID)
  REFERENCES ADDRESS(ID);

ALTER TABLE PERSONORGANIZATION
  ADD FOREIGN KEY(PERSON_ID)
  REFERENCES CONTACT(ID);

ALTER TABLE PERSONORGANIZATION
  ADD FOREIGN KEY(ORGANIZATION_ID)
  REFERENCES CONTACT(ID);

ALTER TABLE ARRAY
  ADD FOREIGN KEY(DESIGN_ID)
  REFERENCES ARRAYDESIGN(ID);

ALTER TABLE ARRAY
  ADD FOREIGN KEY(PRODUCTION_ID)
  REFERENCES PROTOCOLAPPLICATION(ID);

ALTER TABLE ARRAYDESIGN
  ADD FOREIGN KEY(POLYMERTYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE ARRAYDESIGN
  ADD FOREIGN KEY(SUBSTRATETYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE ARRAYDESIGN
  ADD FOREIGN KEY(SURFACETYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE ARRAYDESIGN
  ADD FOREIGN KEY(TECHNOLOGYTYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE ARRAYDESIGN
  ADD FOREIGN KEY(PRINTING_ID)
  REFERENCES PROTOCOLAPPLICATION(ID);

ALTER TABLE ARRAYDESIGN
  ADD FOREIGN KEY(PROVIDER_ID)
  REFERENCES CONTACT(ID);

ALTER TABLE CAARRAYFILE
  ADD FOREIGN KEY(PROJECT_ID)
  REFERENCES PROJECT(ID);

ALTER TABLE PUBLICATION
  ADD FOREIGN KEY(STATUS_ID)
  REFERENCES TERM(ID);

ALTER TABLE PUBLICATION
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONCONTACT
  ADD FOREIGN KEY(CONTACT_ID)
  REFERENCES CONTACT(ID);

ALTER TABLE INVESTIGATIONCONTACT
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONCONTACTROLE
  ADD FOREIGN KEY(INVESTIGATIONCONTACT_ID)
  REFERENCES INVESTIGATIONCONTACT(ID);

ALTER TABLE INVESTIGATIONCONTACTROLE
  ADD FOREIGN KEY(ROLE_ID)
  REFERENCES TERM(ID);

ALTER TABLE FACTOR
  ADD FOREIGN KEY(TYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE FACTOR
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE FACTORVALUE
  ADD FOREIGN KEY(FACTOR_ID)
  REFERENCES FACTOR(ID);

ALTER TABLE PROPOSAL
  ADD FOREIGN KEY(PROJECT_ID)
  REFERENCES PROJECT(ID);

ALTER TABLE PROJECT
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONARRAYDESIGN
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONARRAYDESIGN
  ADD FOREIGN KEY(ARRAYDESIGN_ID)
  REFERENCES ARRAYDESIGN(ID);

ALTER TABLE INVESTIGATIONARRAY
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONARRAY
  ADD FOREIGN KEY(ARRAY_ID)
  REFERENCES ARRAY(ID);

ALTER TABLE INVESTIGATIONSOURCE
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONSOURCE
  ADD FOREIGN KEY(SOURCE_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE INVESTIGATIONSAMPLE
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE INVESTIGATIONSAMPLE
  ADD FOREIGN KEY(SAMPLE_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE NORMALIZATIONTYPE
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE NORMALIZATIONTYPE
  ADD FOREIGN KEY(TERM_ID)
  REFERENCES TERM(ID);

ALTER TABLE REPLICATETYPE
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE REPLICATETYPE
  ADD FOREIGN KEY(TERM_ID)
  REFERENCES TERM(ID);

ALTER TABLE QUALITYCONTROLTYPE
  ADD FOREIGN KEY(INVESTIGATION_ID)
  REFERENCES INVESTIGATION(ID);

ALTER TABLE QUALITYCONTROLTYPE
  ADD FOREIGN KEY(TERM_ID)
  REFERENCES TERM(ID);

ALTER TABLE CHARACTERISTIC
  ADD FOREIGN KEY(TERM_ID)
  REFERENCES TERM(ID);

ALTER TABLE CHARACTERISTIC
  ADD FOREIGN KEY(UNIT_ID)
  REFERENCES TERM(ID);

ALTER TABLE CHARACTERISTIC
  ADD FOREIGN KEY(BIOMATERIAL_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE BIOMATERIAL
  ADD FOREIGN KEY(MATERIAL_TYPE_ID)
  REFERENCES TERM(ID);

ALTER TABLE BIOMATERIAL
  ADD FOREIGN KEY(LABEL_ID)
  REFERENCES COMPOUND(ID);

ALTER TABLE SOURCEPROVIDER
  ADD FOREIGN KEY(SOURCE_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE SOURCEPROVIDER
  ADD FOREIGN KEY(CONTACT_ID)
  REFERENCES CONTACT(ID);

ALTER TABLE SOURCESAMPLE
  ADD FOREIGN KEY(SOURCE_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE SOURCESAMPLE
  ADD FOREIGN KEY(SAMPLE_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE SAMPLEEXTRACT
  ADD FOREIGN KEY(SAMPLE_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE SAMPLEEXTRACT
  ADD FOREIGN KEY(EXTRACT_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE EXTRACTLABELEDEXTRACT
  ADD FOREIGN KEY(EXTRACT_ID)
  REFERENCES BIOMATERIAL(ID);

ALTER TABLE EXTRACTLABELEDEXTRACT
  ADD FOREIGN KEY(LABELEDEXTRACT_ID)
  REFERENCES BIOMATERIAL(ID);

CREATE INDEX TERM_CATEGORY_IDX ON TERM(CATEGORY_ID);

CREATE INDEX TERM_ACCESSION_IDX ON TERM(ACCESSION_ID);

CREATE INDEX TERM_SOURCE_IDX ON TERM(SOURCE_ID);

CREATE INDEX CATEGORY_PARENT_IDX ON CATEGORY(PARENT_ID);

CREATE INDEX ACCESSION_SOURCE_IDX ON ACCESSION(SOURCE_ID);

CREATE INDEX PROTOCOLAPP_PROTOCOL ON PROTOCOLAPPLICATION(PROTOCOL_ID);

CREATE INDEX PROTOCOLAPP_BIOMATERIAL ON PROTOCOLAPPLICATION(BIOMATERIAL_ID);

CREATE INDEX PARAMVALUE_PROTOCOLAPP_IDX ON PARAMETERVALUE(PROTOCOL_APPLICATION_ID);

CREATE INDEX PARAMVALUE_PARAMETER_IDX ON PARAMETERVALUE(PARAMETER_ID);

CREATE INDEX PARAMETER_PROTOCOL_IDX ON PARAMETER(PROTOCOL_ID);

CREATE INDEX PROTOCOL_TYPE_IDX ON PROTOCOL(TYPE_ID);

CREATE INDEX CONTACT_ADDRESS_IDX ON CONTACT(ADDRESS_ID);

CREATE INDEX PERSONORG_PERSON_IDX ON PERSONORGANIZATION(PERSON_ID);

CREATE INDEX PERSONORG_ORG_IDX ON PERSONORGANIZATION(ORGANIZATION_ID);

CREATE INDEX ARRAY_DESIGN_IDX ON ARRAY(DESIGN_ID);

CREATE INDEX ARRAY_PRODUCTION_IDX ON ARRAY(PRODUCTION_ID);

CREATE INDEX ARRAYDESIGN_POLYMER_IDX ON ARRAYDESIGN(POLYMERTYPE_ID);

CREATE INDEX ARRAYDESIGN_SUBSTRATE_IDX ON ARRAYDESIGN(SUBSTRATETYPE_ID);

CREATE INDEX ARRAYDESIGN_SURFACE_IDX ON ARRAYDESIGN(SURFACETYPE_ID);

CREATE INDEX ARRAYDESIGN_TECHNOLOGY_IDX ON ARRAYDESIGN(TECHNOLOGYTYPE_ID);

CREATE INDEX ARRAYDESIGN_PRINTING_IDX ON ARRAYDESIGN(PRINTING_ID);

CREATE INDEX ARRAYDESIGN_PROVIDER_IDX ON ARRAYDESIGN(PROVIDER_ID);

CREATE INDEX CAARRAYFILE_PROJECT_IDX ON CAARRAYFILE(PROJECT_ID);

CREATE INDEX PUBLICATION_STATUS_IDX ON PUBLICATION(STATUS_ID);

CREATE INDEX PUBLICATION_INVEST ON PUBLICATION(INVESTIGATION_ID);

CREATE INDEX INVESTIGATIONCONTACT_CONTACT_IDX ON INVESTIGATIONCONTACT(CONTACT_ID);

CREATE INDEX INVCONTACT_INVEST_IDX ON INVESTIGATIONCONTACT(INVESTIGATION_ID);

CREATE INDEX INVCONTACTROLE_INVCONTACT_IDX ON INVESTIGATIONCONTACTROLE(INVESTIGATIONCONTACT_ID);

CREATE INDEX INVCONTACTROLE_ROLE_IDX ON INVESTIGATIONCONTACTROLE(ROLE_ID);

CREATE INDEX FACTOR_TYPE_IDX ON FACTOR(TYPE_ID);

CREATE INDEX FACTOR_INVEST_IDX ON FACTOR(INVESTIGATION_ID);

CREATE INDEX FACTORVALUE_FACTOR_IDX ON FACTORVALUE(FACTOR_ID);

CREATE INDEX PROPOSAL_PROJECT_IDX ON PROPOSAL(PROJECT_ID);

CREATE INDEX PROJECT_INVESTIGATION_IDX ON PROJECT(INVESTIGATION_ID);

CREATE INDEX INVESTARRAYDESIGN_INVEST_IDX ON INVESTIGATIONARRAYDESIGN(INVESTIGATION_ID);

CREATE INDEX INVESTARRAYDESIGN_ARRAYDESIGN_IDX ON INVESTIGATIONARRAYDESIGN(ARRAYDESIGN_ID);

CREATE INDEX INVESTARRAY_INVEST_IDX ON INVESTIGATIONARRAY(INVESTIGATION_ID);

CREATE INDEX INVESTARRAY_ARRAY_IDX ON INVESTIGATIONARRAY(ARRAY_ID);

CREATE INDEX INVESTIGATIONSRC_INVEST_IDX ON INVESTIGATIONSOURCE(INVESTIGATION_ID);

CREATE INDEX INVESTIGATIONSRC_SRC_IDX ON INVESTIGATIONSOURCE(SOURCE_ID);

CREATE INDEX INVESTIGATIONSAMPLE_INVEST_IDX ON INVESTIGATIONSAMPLE(INVESTIGATION_ID);

CREATE INDEX INVESTIGATIONSAMPLE_SAMPLE_IDX ON INVESTIGATIONSAMPLE(SAMPLE_ID);

CREATE INDEX NORMTYPE_INVEST_IDX ON NORMALIZATIONTYPE(INVESTIGATION_ID);

CREATE INDEX NORMTYPE_TERM_IDX ON NORMALIZATIONTYPE(TERM_ID);

CREATE INDEX REPLTYPE_INVEST_IDX ON REPLICATETYPE(INVESTIGATION_ID);

CREATE INDEX REPLTYPE_TERM_IDX ON REPLICATETYPE(TERM_ID);

CREATE INDEX QUALCTRLTYPE_INVEST_IDX ON QUALITYCONTROLTYPE(INVESTIGATION_ID);

CREATE INDEX QUALCTRLTYPE_TERM_IDX ON QUALITYCONTROLTYPE(TERM_ID);

CREATE INDEX CHARACTERISTIC_TERM_IDX ON CHARACTERISTIC(TERM_ID);

CREATE INDEX CHARACTERISTIC_UNIT_IDX ON CHARACTERISTIC(UNIT_ID);

CREATE INDEX CHARACTERISTIC_BIOMATERIAL_IDX ON CHARACTERISTIC(BIOMATERIAL_ID);

CREATE INDEX BIOMATERIAL_TYPE_IDX ON BIOMATERIAL(MATERIAL_TYPE_ID);

CREATE INDEX BIOMATERIAL_LABEL_IDX ON BIOMATERIAL(LABEL_ID);

CREATE INDEX SOURCEPROVIDER_SOURCE_IDX ON SOURCEPROVIDER(SOURCE_ID);

CREATE INDEX SOURCEPROVIDER_CONTACT_IDX ON SOURCEPROVIDER(CONTACT_ID);

CREATE INDEX SOURCESAMPLE_SOURCE_IDX ON SOURCESAMPLE(SOURCE_ID);

CREATE INDEX SOURCESAMPLE_SAMPLE_IDX ON SOURCESAMPLE(SAMPLE_ID);

CREATE INDEX SAMPLEEXTRACT_SAMPLE_IDX ON SAMPLEEXTRACT(SAMPLE_ID);

CREATE INDEX SAMPLEEXTRACT_EXTRACT_IDX ON SAMPLEEXTRACT(EXTRACT_ID);

CREATE INDEX EXTRACTLABELEDEXTRACT_E_IDX ON EXTRACTLABELEDEXTRACT(EXTRACT_ID);

CREATE INDEX EXTRACTLABELEDEXTRACT_LE_IDX ON EXTRACTLABELEDEXTRACT(LABELEDEXTRACT_ID);

SET FOREIGN_KEY_CHECKS = 1;
