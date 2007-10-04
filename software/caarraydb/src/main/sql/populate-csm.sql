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

COMMIT;