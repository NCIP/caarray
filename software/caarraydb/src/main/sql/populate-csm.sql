-- Sets up the CSM tables with initial development values

-- Create the application objects and associated protection elements
INSERT INTO csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE,
DATABASE_URL, DATABASE_USER_NAME, DATABASE_PASSWORD, DATABASE_DIALECT, DATABASE_DRIVER)
values ("csmupt","CSM UPT Super Admin Application",0,0,sysdate(),
'jdbc:mysql://localhost:3306/caarray2', 'caarray2op', 'qN+MnXquuqO8j2uyHEABIQ==', 'org.hibernate.dialect.MySQLDialect', 'com.mysql.jdbc.Driver');

insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("csmupt","CSM UPT Super Admin Application Protection Element","csmupt",1,sysdate());

INSERT INTO csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE,
DATABASE_URL, DATABASE_USER_NAME, DATABASE_PASSWORD, DATABASE_DIALECT, DATABASE_DRIVER)
VALUES ("caarray","description of caarray",0,0,sysdate(),
'jdbc:mysql://localhost:3306/caarray2', 'caarray2op', 'qN+MnXquuqO8j2uyHEABIQ==', 'org.hibernate.dialect.MySQLDialect', 'com.mysql.jdbc.Driver');

insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("caarray","caarray Admin Application Protection Element","caarray",1,sysdate());

-- Create some users and their protection elements
 -- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("caarrayadmin","caArray","Administrator","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,1,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,1,sysdate());

insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("fb_admin","caArray","ldapuser","ldap_only_no_pass",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,2,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,2,sysdate());

-- 2nd db-backed user to test permissions
-- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("caarrayuser","caArray","User","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,3,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,3,sysdate());

-- The synthetic anonymous user.  This is required because we are using instance level
-- security on objects that anonymous (non-logged-in) users sometimes have access to.
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("__anonymous__","Anonymous","User","anonymous_access_only_no_pass",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,4,sysdate());

 -- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("researchscientist","ResearchScientist","ResearchScientist","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,5,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,5,sysdate());

 -- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("labadministrator","LabAdministrator","LabAdministrator","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,6,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,6,sysdate());

 -- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("labscientist","LabScientist","LabScientist","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,7,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,7,sysdate());

 -- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("biostatistician","Biostatistician","Biostatistician","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,8,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,8,sysdate());

-- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("systemadministrator","SystemAdministrator","SystemAdministrator","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,9,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,9,sysdate());

-- f1rebird05 is password
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("collaborator","Collaborator","Collaborator","gJ5bRQxV/Qnei3BvqISY2Q==",sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(1,10,sysdate());
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID,UPDATE_DATE)
values(2,10,sysdate());

-- Groups
-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('PrincipalInvestigator', sysdate(), 1);

-- The Public group corresponds to public access profiles - everyone (except the anonymous user)
-- should be in this group
insert into csm_group (group_name, update_date, application_id)
values('Public', sysdate(), 2);

-- The anonymous group corresponds to access profiles.  Everyone, including the anonymous user,
-- should be in this group
insert into csm_group (group_name, update_date, application_id)
values('__anonymous__', sysdate(), 2);

-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('ResearchScientist', sysdate(), 1);

-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('LabAdministrator', sysdate(), 1);

-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('LabScientist', sysdate(), 1);

-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('Biostatistician', sysdate(), 1);

-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('SystemAdministrator', sysdate(), 1);

-- This group corresponds to a security group
insert into csm_group (group_name, update_date, application_id)
values('Collaborator', sysdate(), 1);

-- all three real users belong to all groups
insert into csm_user_group (user_id, group_id)
values(1, 1);
insert into csm_user_group (user_id, group_id)
values(1, 2);
insert into csm_user_group (user_id, group_id)
values(1, 3);

insert into csm_user_group (user_id, group_id)
values(2, 1);
insert into csm_user_group (user_id, group_id)
values(2, 2);
insert into csm_user_group (user_id, group_id)
values(2, 3);

insert into csm_user_group (user_id, group_id)
values(3, 1);
insert into csm_user_group (user_id, group_id)
values(3, 2);
insert into csm_user_group (user_id, group_id)
values(3, 3);

-- the anonymous user belong to just the anonymous group
insert into csm_user_group (user_id, group_id)
values(4, 3);

-- the ResearchScientist
insert into csm_user_group (user_id, group_id)
values(5, 4);

-- the LabAdmin
insert into csm_user_group (user_id, group_id)
values(6, 5);

-- the LabScientist
insert into csm_user_group (user_id, group_id)
values(7, 6);

-- the Biostatistician
insert into csm_user_group (user_id, group_id)
values(8, 7);

-- the SysAdmin
insert into csm_user_group (user_id, group_id)
values(9, 8);

-- the Collaborator
insert into csm_user_group (user_id, group_id)
values(10, 9);

#
# The following entries are Common Set of Privileges
#

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("CREATE","This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("ACCESS","This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("READ","This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("WRITE","This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("UPDATE","This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("DELETE","This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("EXECUTE","This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc", sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Create', 'Create', 2, 1, sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Access', 'Access', 2, 1, sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Read', 'Read', 2, 1, sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Write', 'Write', 2, 1, sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Update', 'Update', 2, 1, sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Delete', 'Delete', 2, 1, sysdate());

INSERT INTO csm_role (role_name, role_description, application_id, active_flag, update_date)
VALUES('Execute', 'Execute', 2, 1, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(1, 1, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(2, 2, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(3, 3, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(4, 4, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(5, 5, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(6, 6, sysdate());

INSERT INTO csm_role_privilege (role_id, privilege_id, update_date)
VALUES(7, 7, sysdate());

-- Our security filters
insert into csm_filter_clause (class_name, filter_chain, target_class_name, 
application_id, update_date, target_class_attribute_name, target_class_attribute_type, 
generated_sql)
values ('gov.nih.nci.caarray.domain.project.Project', 'gov.nih.nci.caarray.domain.project.Project', 'gov.nih.nci.caarray.domain.project.Project - self',
2, sysdate(), 'id', 'java.lang.Long', 
'ID in (select table_name_csm_.ID from PROJECT table_name_csm_ where table_name_csm_.ID in (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = g.group_id) and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id  or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))');

-- This is the real sql, but due to hibernate bug http://opensource.atlassian.com/projects/hibernate/browse/HHH-2593
-- we'll go without the union
-- 'ID in (select table_name_csm_.ID from PROJECT table_name_csm_ where table_name_csm_.ID in (select pe.attribute_value from csm_protection_element pe, csm_user u, csm_user_pe upe where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and u.user_id = upe.user_id and upe.protection_element_id = pe.protection_element_id union select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_group g where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and (ugrpg.user_id = u.user_id or ugrpg.group_id = g.group_id) and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id  or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ''))');

COMMIT;