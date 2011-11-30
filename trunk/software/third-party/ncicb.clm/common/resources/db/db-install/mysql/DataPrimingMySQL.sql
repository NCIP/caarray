# 
# The following entries creates a super admin application incase you decide 
# to use this database to run UPT also. In that case you need to provide
# the project login id and name for the super admin.
# However in incase you are using this database just to host the application's
# authorization schema, these enteries are not used and hence they can be left as 
# it is.
#

insert into csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE)
values ("csmupt","CSM UPT Super Admin Application",0,0,sysdate());

insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("superadmin","super.admin.first.name","super.admin.last.name","zJPWCwDeSgG8j2uyHEABIQ==",sysdate());

 
insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("csmupt","CSM UPT Super Admin Application Protection Element","csmupt",1,sysdate());

insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID)
values(1,1);

# 
# The following entry is for your application. 
# Replace <<application_context_name>> with your application name.
#

INSERT INTO csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE,DATABASE_URL,DATABASE_USER_NAME,DATABASE_PASSWORD,DATABASE_DIALECT,DATABASE_DRIVER,CSM_VERSION)
VALUES ("CLM","CLM",0,0,sysdate(),"jdbc:mysql://localhost:3306/clm","clm","nGNTxuVEogo=","org.hibernate.dialect.MySQLDialect","com.mysql.jdbc.Driver","4.2");

insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("CLM","CLM","CLM",1,sysdate());

insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID)
values(2,1);

insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("APPLICATION_NAME:csmupt","APPLICATION_NAME:csmupt","APPLICATION_NAME:csmupt",2,sysdate());


insert into csm_protection_group(PROTECTION_GROUP_NAME,PROTECTION_GROUP_DESCRIPTION,APPLICATION_ID,LARGE_ELEMENT_COUNT_FLAG,UPDATE_DATE)
values("APPLICATION_NAME","APPLICATION_NAME",2,0,sysdate());

insert into csm_pg_pe(protection_group_id,protection_element_id,update_date) values (1,3,sysdate());

insert into csm_role(role_name,role_description,application_id,active_flag,update_date)
values("READ_ROLE","READ_ROLE",2,1,sysdate());



insert into csm_user_group_role_pg(user_id,role_id,protection_group_id,update_date)
values(1,1,1,sysdate());


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

insert into csm_role_privilege(role_id,privilege_id) values (1,3);


COMMIT;
