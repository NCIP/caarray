CREATE TABLE TERM
(
  ID                          NUMBER            NOT NULL,
  DESCRIPTION                 VARCHAR2(1000),
  VALUE                       VARCHAR2(1000),
  CATEGORY_ID                 NUMBER            NOT NULL,
  ACCESSION_ID                NUMBER,
  SOURCE_ID                   NUMBER
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE CATEGORY
(
  ID                          NUMBER            NOT NULL,
  NAME                        VARCHAR2(1000),
  PARENT_ID                   NUMBER            NOT NULL
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE CATEGORYCHILDREN
(
  CATEGORY_ID           NUMBER                  NOT NULL,
  CHILD_CATEGORY_ID     NUMBER                  NOT NULL
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE ACCESSION
(
  ID                          NUMBER            NOT NULL,
  URL                         VARCHAR2(1000),
  VALUE                       VARCHAR2(1000),
  SOURCE_ID                   NUMBER            NOT NULL
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE SOURCE
(
  ID                          NUMBER            NOT NULL,
  NAME                        VARCHAR2(1000),
  URL                         VARCHAR2(1000),
  VERSION                     VARCHAR2(1000)
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE PROTOCOLAPPLICATION
(
  ID                          NUMBER            NOT NULL,
  PROTOCOL_ID                 NUMBER            NOT NULL
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE PARAMETERVALUE
(
  ID                          NUMBER            NOT NULL,
  UNIT                        VARCHAR2(1000),
  VALUE                       VARCHAR2(1000),
  PROTOCOL_APPLICATION_ID     NUMBER            NOT NULL,
  PARAMETER_ID                NUMBER            NOT NULL
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE PARAMETER
(
  ID                          NUMBER            NOT NULL,
  NAME                        VARCHAR2(1000),
  PROTOCOL_ID                 NUMBER            NOT NULL,
  DEFAULT_VALUE_ID            NUMBER
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE TABLE PROTOCOL
(
  ID                          NUMBER            NOT NULL,
  ACCESSION                   VARCHAR2(1000),
  NAME                        VARCHAR2(1000),
  TEXT                        VARCHAR2(1000),
  TITLE                       VARCHAR2(1000),
  URL                         VARCHAR2(1000),
  TYPE_ID                     NUMBER            NOT NULL
)
NOLOGGING
NOCACHE
NOPARALLEL;

CREATE UNIQUE INDEX TERM_PK ON TERM
(ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX CATEGORY_PK ON CATEGORY
(ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX CATEGORYCHILDREN_PK ON CATEGORYCHILDREN
(CATEGORY_ID, CHILD_CATEGORY_ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX ACCESSION_PK ON ACCESSION
(ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX SOURCE_PK ON SOURCE
(ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX TERM_CATEGORY_IDX ON TERM
(CATEGORY_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX TERM_ACCESSION_IDX ON TERM
(ACCESSION_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX TERM_SOURCE_IDX ON TERM
(SOURCE_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX CATEGORY_PARENT_IDX ON CATEGORY
(PARENT_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX ACCESSION_SOURCE_IDX ON ACCESSION
(SOURCE_ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX PROTOCOLAPPLICATION_PK ON PROTOCOLAPPLICATION
(ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX PARAMETERVALUE_PK ON PARAMETERVALUE
(ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX PARAMETER_PK ON PARAMETER
(ID)
NOLOGGING
NOPARALLEL;

CREATE UNIQUE INDEX PROTOCOL_PK ON PROTOCOL
(ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX PROTOCOLAPP_PROTOCOL ON PROTOCOLAPPLICATION
(PROTOCOL_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX PARAMVALUE_PROTOCOLAPP_IDX ON PARAMETERVALUE
(PROTOCOL_APPLICATION_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX PARAMVALUE_PARAMETER_IDX ON PARAMETERVALUE
(PARAMETER_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX PARAMETER_PROTOCOL_IDX ON PARAMETER
(PROTOCOL_ID)
NOLOGGING
NOPARALLEL;

CREATE INDEX PROTOCOL_TYPE_IDX ON PROTOCOL
(TYPE_ID)
NOLOGGING
NOPARALLEL;

