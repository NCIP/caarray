-- Sets up the CSM tables with initial development values

-- Create the application objects and associated protection elements
INSERT INTO CSM_APPLICATION(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE,
DATABASE_URL, DATABASE_USER_NAME, DATABASE_PASSWORD, DATABASE_DIALECT, DATABASE_DRIVER)
values ("csmupt","CSM UPT Super Admin Application",0,0,sysdate(),
'jdbc:mysql://localhost:3306/caarray2', 'caarray2op', 'qN+MnXquuqO8j2uyHEABIQ==', 'org.hibernate.dialect.MySQLDialect', 'com.mysql.jdbc.Driver');

insert into CSM_PROTECTION_ELEMENT(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("csmupt","CSM UPT Super Admin Application Protection Element","csmupt",1,sysdate());

INSERT INTO CSM_APPLICATION(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE,
DATABASE_URL, DATABASE_USER_NAME, DATABASE_PASSWORD, DATABASE_DIALECT, DATABASE_DRIVER)
VALUES ("caarray","description of caarray",0,0,sysdate(),
'jdbc:mysql://localhost:3306/caarray2', 'caarray2op', 'qN+MnXquuqO8j2uyHEABIQ==', 'org.hibernate.dialect.MySQLDialect', 'com.mysql.jdbc.Driver');

insert into CSM_PROTECTION_ELEMENT(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("caarray","caarray Admin Application Protection Element","caarray",1,sysdate());

-- Create some users and their protection elements

-- The synthetic anonymous user.  This is required because we are using instance level
-- security on objects that anonymous (non-logged-in) users sometimes have access to.
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("__anonymous__","Anonymous","User","sNYUJiYS6oaabiiT/fn5hIc+SidokNh2+YRTFJJYiuZLRCIBFypS8Q==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,1,sysdate());

 -- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("caarrayadmin","caArray","Administrator","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,2,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,2,sysdate());

-- 2nd db-backed user to test permissions
-- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("caarrayuser","caArray","User","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,3,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,3,sysdate());

 -- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("researchscientist","ResearchScientist","ResearchScientist","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,4,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,4,sysdate());

 -- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("labadministrator","LabAdministrator","LabAdministrator","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,5,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,5,sysdate());

 -- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("labscientist","LabScientist","LabScientist","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,6,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,6,sysdate());

 -- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("biostatistician","Biostatistician","Biostatistician","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,7,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,7,sysdate());

-- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("systemadministrator","SystemAdministrator","SystemAdministrator","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,8,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,8,sysdate());

-- caArray2! is password
insert into CSM_USER (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("collaborator","Collaborator","Collaborator","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,9,sysdate());
insert into CSM_USER_PE(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,9,sysdate());

-- The anonymous group corresponds to access profiles.  Everyone, including the anonymous user,
-- should be in this group
insert into CSM_GROUP (group_name, update_date, application_id)
values('__anonymous__', sysdate(), 2);

-- Groups
-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('PrincipalInvestigator', sysdate(), 2);

-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('ResearchScientist', sysdate(), 2);

-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('LabAdministrator', sysdate(), 2);

-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('LabScientist', sysdate(), 2);

-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('Biostatistician', sysdate(), 2);

-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('SystemAdministrator', sysdate(), 2);

-- This group corresponds to a security group
insert into CSM_GROUP (group_name, update_date, application_id)
values('Collaborator', sysdate(), 2);

-- the anonymous user belong to just the anonymous group
insert into CSM_USER_GROUP (user_id, group_id)
values(1, 1);

-- everybody belongs to the anonymous group
insert into CSM_USER_GROUP (user_id, group_id)
values(2, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(3, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(4, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(5, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(6, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(7, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(8, 1);
insert into CSM_USER_GROUP (user_id, group_id)
values(9, 1);


-- the first 2 real users are in the PI group
insert into CSM_USER_GROUP (user_id, group_id)
values(2, 2);
insert into CSM_USER_GROUP (user_id, group_id)
values(3, 2);

-- the ResearchScientist
insert into CSM_USER_GROUP (user_id, group_id)
values(4, 3);

-- the LabAdmin
insert into CSM_USER_GROUP (user_id, group_id)
values(5, 4);

-- the LabScientist
insert into CSM_USER_GROUP (user_id, group_id)
values(6, 5);

-- the Biostatistician
insert into CSM_USER_GROUP (user_id, group_id)
values(7, 6);

-- the SysAdmin
insert into CSM_USER_GROUP (user_id, group_id)
values(8, 7);

-- the Collaborator
insert into CSM_USER_GROUP (user_id, group_id)
values(9, 8);

#
# The following entries are Common Set of Privileges
#

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("CREATE","This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection", sysdate());

# we will use the ACCESS privilege to represent BROWSABILITY
INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("ACCESS","This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself", sysdate());

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("READ","This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry", sysdate());

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("WRITE","This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity", sysdate());

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("UPDATE","This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc", sysdate());

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("DELETE","This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc", sysdate());

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("EXECUTE","This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc", sysdate());

INSERT INTO CSM_PRIVILEGE (privilege_name, privilege_description, update_date)
VALUES("PERMISSIONS","This privilege allows a user to modify the permissions for a particular resource. The resource can be a method, function, behavior of the application, URL, button etc", sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Create', 'Create', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Access', 'Access', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Read', 'Read', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Write', 'Write', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Update', 'Update', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Delete', 'Delete', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Execute', 'Execute', 2, 1, sysdate());

INSERT INTO CSM_ROLE (role_name, role_description, application_id, active_flag, update_date)
VALUES('Permissions', 'Permissions', 2, 1, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(1, 1, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(2, 2, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(3, 3, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(4, 4, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(5, 5, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(6, 6, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(7, 7, sysdate());

INSERT INTO CSM_ROLE_PRIVILEGE (role_id, privilege_id, update_date)
VALUES(8, 8, sysdate());

-- Our security filters

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, 
application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
values ('gov.nih.nci.caarray.domain.project.Project', 'gov.nih.nci.caarray.domain.project.Project', 'gov.nih.nci.caarray.domain.project.Project - self',
2, sysdate(), 'id', 'java.lang.Long', 
'ID in (select table_name_csm_.ID from PROJECT table_name_csm_ where table_name_csm_.ID in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''ACCESS''))');

-- This is the real sql, but due to hibernate bug http://opensource.atlassian.com/projects/hibernate/browse/HHH-2593
-- we'll go without the union
-- 'ID in (select table_name_csm_.ID from PROJECT table_name_csm_ where table_name_csm_.ID in (select pe.attribute_value from csm_protection_element pe, csm_user u, csm_user_pe upe where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and u.user_id = upe.user_id and upe.protection_element_id = pe.protection_element_id union select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = g.group_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''ACCESS''))');

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, 
application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
values ('gov.nih.nci.caarray.domain.project.Experiment', 'gov.nih.nci.caarray.domain.project.Experiment', 'gov.nih.nci.caarray.domain.project.Experiment - self',
2, sysdate(), 'id', 'java.lang.Long', 
'ID in (select ex.ID from experiment ex inner join project p on ex.id = p.experiment where p.id in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''ACCESS''))');

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, 
application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
values ('gov.nih.nci.caarray.domain.permissions.CollaboratorGroup', 'gov.nih.nci.caarray.domain.permissions.CollaboratorGroup', 'gov.nih.nci.caarray.domain.permissions.CollaboratorGroup - self',
2, sysdate(), 'id', 'java.lang.Long', 
'ID in (select table_name_csm_.ID from COLLABORATOR_GROUP table_name_csm_ where table_name_csm_.ID in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.permissions.CollaboratorGroup'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))');

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
 values ('gov.nih.nci.caarray.domain.hybridization.Hybridization', 'gov.nih.nci.caarray.domain.hybridization.Hybridization', 'gov.nih.nci.caarray.domain.hybridization.Hybridization - self',
 2, sysdate(), 'id', 'java.lang.Long', 
  'ID in (select h.ID from hybridization h inner join labeledextracthybridization lhe on h.id = lhe.hybridization_id inner join extractlabeledextract ele on lhe.labeledextract_id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = se.extract_id inner join biomaterial s on se.sample_id = s.id where s.ID in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))');

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
 values ('gov.nih.nci.caarray.domain.project.Factor', 'gov.nih.nci.caarray.domain.project.Factor', 'gov.nih.nci.caarray.domain.project.Factor - self',
 2, sysdate(), 'id', 'java.lang.Long', 
  'ID in (select f.ID from factor f inner join experiment ex on f.experiment = ex.id left join project p on ex.id = p.experiment where p.id in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))');

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
 values ('gov.nih.nci.caarray.domain.data.AbstractArrayData', 'gov.nih.nci.caarray.domain.data.AbstractArrayData', 'gov.nih.nci.caarray.domain.data.AbstractArrayData - self',
 2, sysdate(), 'id', 'java.lang.Long', 
  'ID in (select ad.id from arraydata ad left join hybridization h on ad.hybridization = h.id left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id left join hybridization h2 on dadh.hybridization_id = h2.id left join labeledextracthybridization leh on h.id = leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where s.id is not null and s.id in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') or s2.id is not null and s2.id in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))');

insert into CSM_FILTER_CLAUSE (class_name, filter_chain, target_class_name, application_id, update_date, target_class_attribute_name, target_class_attribute_type, generated_sql)
 values ('gov.nih.nci.caarray.domain.sample.AbstractBioMaterial', 'gov.nih.nci.caarray.domain.sample.AbstractBioMaterial', 'gov.nih.nci.caarray.domain.sample.AbstractBioMaterial - all',
 2, sysdate(), 'id', 'java.lang.Long', 
  concat('ID in (select b.ID from biomaterial b left join experimentsource es on b.id = es.source_id left join experiment ex on es.experiment_id = ex.id left join project p on ex.id = p.experiment left join sampleextract se on b.id = se.extract_id left join biomaterial b2 on b2.id = se.sample_id left join extractlabeledextract ele on b.id = ele.labeledextract_id left join sampleextract se2 on ele.extract_id = se2.extract_id left join biomaterial b3 on b3.id = se2.sample_id where ',
'b.discriminator=''SO'' and p.id in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') or ',
'b.discriminator = ''SA'' and b.ID in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') OR ',
'b2.discriminator = ''SA'' and b2.ID in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') OR ',
'b3.discriminator=''SA'' and b3.id in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))')); 

