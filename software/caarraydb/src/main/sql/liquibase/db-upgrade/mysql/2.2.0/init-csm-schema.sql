-- This script is is a slightly modified version of the AuthSchemaMySQL.sql
-- file from the CSM 4.0 distribution.  It has been modified:
-- (1) to use our replacements
-- (2) not to drop the db, nor the tables
-- (3) to allow UPDATE_DATE to be null everywhere

CREATE TABLE csm_application (
  application_id BIGINT AUTO_INCREMENT  NOT NULL,
  application_name VARCHAR(255) NOT NULL,
  application_description VARCHAR(200) NOT NULL,
  declarative_flag BOOL NOT NULL DEFAULT 0,
  active_flag BOOL NOT NULL DEFAULT 0,
  update_date DATE,
  database_url VARCHAR(100),
  database_user_name VARCHAR(100),
  database_password VARCHAR(100),
  database_dialect VARCHAR(100),
  database_driver VARCHAR(100),
  PRIMARY KEY(application_id)
)Type=InnoDB
;

CREATE TABLE csm_group (
  group_id BIGINT AUTO_INCREMENT  NOT NULL,
  group_name VARCHAR(255) NOT NULL,
  group_desc VARCHAR(200),
  update_date DATE,
  application_id BIGINT NOT NULL,
  PRIMARY KEY(group_id)
)Type=InnoDB
;

CREATE TABLE csm_privilege (
  privilege_id BIGINT AUTO_INCREMENT  NOT NULL,
  privilege_name VARCHAR(100) NOT NULL,
  privilege_description VARCHAR(200),
  update_date DATE,
  PRIMARY KEY(privilege_id)
)Type=InnoDB
;

CREATE TABLE csm_filter_clause (
  filter_clause_id BIGINT AUTO_INCREMENT  NOT NULL,
  class_name VARCHAR(100) NOT NULL,
  filter_chain VARCHAR(2000) NOT NULL,
  target_class_name VARCHAR (100) NOT NULL,
  target_class_attribute_name VARCHAR (100) NOT NULL,
  target_class_attribute_type VARCHAR (100) NOT NULL,
  target_class_alias VARCHAR (100),
  target_class_attribute_alias VARCHAR (100),
  generated_sql VARCHAR (5500) NOT NULL,
  application_id BIGINT NOT NULL,
  update_date DATE,
  PRIMARY KEY(filter_clause_id)
)Type=InnoDB
;

CREATE TABLE csm_protection_element (
  protection_element_id BIGINT AUTO_INCREMENT  NOT NULL,
  protection_element_name VARCHAR(100) NOT NULL,
  protection_element_description VARCHAR(200),
  object_id VARCHAR(100) NOT NULL,
  attribute VARCHAR(100) ,
  protection_element_type VARCHAR(100),
  application_id BIGINT NOT NULL,
  update_date DATE,
  attribute_value VARCHAR(100),
  PRIMARY KEY(protection_element_id)
)Type=InnoDB
;

CREATE TABLE csm_protection_group (
  protection_group_id BIGINT AUTO_INCREMENT  NOT NULL,
  protection_group_name VARCHAR(100) NOT NULL,
  protection_group_description VARCHAR(200),
  application_id BIGINT NOT NULL,
  large_element_count_flag BOOL NOT NULL,
  update_date DATE,
  parent_protection_group_id BIGINT,
  PRIMARY KEY(protection_group_id)
)Type=InnoDB
;

CREATE TABLE csm_pg_pe (
  pg_pe_id BIGINT AUTO_INCREMENT  NOT NULL,
  protection_group_id BIGINT NOT NULL,
  protection_element_id BIGINT NOT NULL,
  update_date DATE,
  PRIMARY KEY(pg_pe_id)
)Type=InnoDB
;

CREATE TABLE csm_role (
  role_id BIGINT AUTO_INCREMENT  NOT NULL,
  role_name VARCHAR(100) NOT NULL,
  role_description VARCHAR(200),
  application_id BIGINT NOT NULL,
  active_flag BOOL NOT NULL,
  update_date DATE,
  PRIMARY KEY(role_id)
)Type=InnoDB
;

CREATE TABLE csm_role_privilege (
  role_privilege_id BIGINT AUTO_INCREMENT  NOT NULL,
  role_id BIGINT NOT NULL,
  privilege_id BIGINT NOT NULL,
  update_date DATE,
  PRIMARY KEY(role_privilege_id)
)Type=InnoDB
;

CREATE TABLE csm_user (
  user_id BIGINT AUTO_INCREMENT  NOT NULL,
  login_name VARCHAR(100) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  organization VARCHAR(100),
  department VARCHAR(100),
  title VARCHAR(100),
  phone_number VARCHAR(15),
  password VARCHAR(100),
  email_id VARCHAR(100),
  start_date DATE,
  end_date DATE,
  update_date DATE,
  PRIMARY KEY(user_id)
)Type=InnoDB
;

CREATE TABLE csm_user_group (
  user_group_id BIGINT AUTO_INCREMENT  NOT NULL,
  user_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  PRIMARY KEY(user_group_id)
)Type=InnoDB
;

CREATE TABLE csm_user_group_role_pg (
  user_group_role_pg_id BIGINT AUTO_INCREMENT  NOT NULL,
  user_id BIGINT,
  group_id BIGINT,
  role_id BIGINT NOT NULL,
  protection_group_id BIGINT NOT NULL,
  update_date DATE,
  PRIMARY KEY(user_group_role_pg_id)
)Type=InnoDB
;

CREATE TABLE csm_user_pe (
  user_protection_element_id BIGINT AUTO_INCREMENT  NOT NULL,
  protection_element_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  update_date DATE,
  PRIMARY KEY(user_protection_element_id)
)Type=InnoDB
;



ALTER TABLE csm_application
ADD CONSTRAINT uq_application_name UNIQUE (application_name)
;
CREATE INDEX idx_application_id ON csm_group(application_id)
;
ALTER TABLE csm_group
ADD CONSTRAINT uq_group_group_name UNIQUE (application_id, group_name)
;
ALTER TABLE csm_privilege
ADD CONSTRAINT uq_privilege_name UNIQUE (privilege_name)
;
CREATE INDEX idx_application_id ON csm_protection_element(application_id)
;
ALTER TABLE csm_protection_element
ADD CONSTRAINT uq_pe_pe_name_attribute_value_app_id UNIQUE (object_id, attribute, attribute_value, application_id)
;
CREATE INDEX idx_application_id ON csm_protection_group(application_id)
;
ALTER TABLE csm_protection_group
ADD CONSTRAINT uq_protection_group_protection_group_name UNIQUE (application_id, protection_group_name)
;
CREATE INDEX idx_parent_protection_group_id ON csm_protection_group(parent_protection_group_id)
;
CREATE INDEX idx_protection_element_id ON csm_pg_pe(protection_element_id)
;
CREATE INDEX idx_protection_group_id ON csm_pg_pe(protection_group_id)
;
ALTER TABLE csm_pg_pe
ADD CONSTRAINT uq_protection_group_protection_element_protection_group_id UNIQUE (protection_element_id, protection_group_id)
;
CREATE INDEX idx_APPLICATION_ID ON csm_role(application_id)
;
ALTER TABLE csm_role
ADD CONSTRAINT uq_role_role_name UNIQUE (application_id, role_name)
;
CREATE INDEX idx_privilege_id ON csm_role_privilege(privilege_id)
;
ALTER TABLE csm_role_privilege
ADD CONSTRAINT uq_role_privilege_role_id UNIQUE (privilege_id, role_id)
;
CREATE INDEX idx_role_id ON csm_role_privilege(role_id)
;
ALTER TABLE csm_user
ADD CONSTRAINT uq_login_name UNIQUE (login_name)
;
CREATE INDEX idx_user_id ON csm_user_group(user_id)
;
CREATE INDEX idx_group_id ON csm_user_group(group_id)
;
CREATE INDEX idx_group_id ON csm_user_group_role_pg(group_id)
;
CREATE INDEX idx_role_id ON csm_user_group_role_pg(role_id)
;
CREATE INDEX idx_protection_group_id ON csm_user_group_role_pg(protection_group_id)
;
CREATE INDEX idx_user_id ON csm_user_group_role_pg(user_id)
;
CREATE INDEX idx_user_id ON csm_user_pe(user_id)
;
CREATE INDEX idx_protection_element_id ON csm_user_pe(protection_element_id)
;
ALTER TABLE csm_user_pe
ADD CONSTRAINT uq_user_protection_element_protection_element_id UNIQUE (user_id, protection_element_id)
;


ALTER TABLE csm_group ADD CONSTRAINT fk_application_group
FOREIGN KEY (application_id) REFERENCES csm_application (application_id)
ON DELETE CASCADE
;

ALTER TABLE csm_filter_clause ADD CONSTRAINT fk_application_filter_clause
FOREIGN KEY (application_id) REFERENCES csm_application (application_id)
ON DELETE CASCADE
;

ALTER TABLE csm_protection_element ADD CONSTRAINT fk_pe_application
FOREIGN KEY (application_id) REFERENCES csm_application (application_id)
ON DELETE CASCADE
;

ALTER TABLE csm_protection_group ADD CONSTRAINT fk_pg_application
FOREIGN KEY (application_id) REFERENCES csm_application (application_id)
ON DELETE CASCADE
;

ALTER TABLE csm_protection_group ADD CONSTRAINT fk_protection_group
FOREIGN KEY (parent_protection_group_id) REFERENCES csm_protection_group (protection_group_id)
;

ALTER TABLE csm_pg_pe ADD CONSTRAINT fk_protection_element_protection_group
FOREIGN KEY (protection_element_id) REFERENCES csm_protection_element (protection_element_id)
ON DELETE CASCADE
;

ALTER TABLE csm_pg_pe ADD CONSTRAINT fk_protection_group_protection_element
FOREIGN KEY (protection_group_id) REFERENCES csm_protection_group (protection_group_id)
ON DELETE CASCADE
;

ALTER TABLE csm_role ADD CONSTRAINT fk_application_role
FOREIGN KEY (application_id) REFERENCES csm_application (application_id)
ON DELETE CASCADE
;

ALTER TABLE csm_role_privilege ADD CONSTRAINT fk_privilege_role
FOREIGN KEY (privilege_id) REFERENCES csm_privilege (privilege_id)
ON DELETE CASCADE
;

ALTER TABLE csm_role_privilege ADD CONSTRAINT fk_role
FOREIGN KEY (role_id) REFERENCES csm_role (role_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_group ADD CONSTRAINT fk_user_group
FOREIGN KEY (user_id) REFERENCES csm_user (user_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_group ADD CONSTRAINT fk_ug_group
FOREIGN KEY (group_id) REFERENCES csm_group (group_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_group_role_pg ADD CONSTRAINT fk_user_group_role_protection_group_groups
FOREIGN KEY (group_id) REFERENCES csm_group (group_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_group_role_pg ADD CONSTRAINT fk_user_group_role_protection_group_role
FOREIGN KEY (role_id) REFERENCES csm_role (role_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_group_role_pg ADD CONSTRAINT fk_user_group_role_protection_group_protection_group
FOREIGN KEY (protection_group_id) REFERENCES csm_protection_group (protection_group_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_group_role_pg ADD CONSTRAINT fk_user_group_role_protection_group_user
FOREIGN KEY (user_id) REFERENCES csm_user (user_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_pe ADD CONSTRAINT fk_pe_user
FOREIGN KEY (user_id) REFERENCES csm_user (user_id)
ON DELETE CASCADE
;

ALTER TABLE csm_user_pe ADD CONSTRAINT fk_protection_element_user
FOREIGN KEY (protection_element_id) REFERENCES csm_protection_element (protection_element_id)
ON DELETE CASCADE
;

COMMIT;