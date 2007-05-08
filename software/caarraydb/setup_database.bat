@echo off
echo Running database setup for caarray2...
echo Assumes user has set the mysql root password according to MySQL setup instructions on the wiki...

@echo on

mysql --user=root --password=passw0rd < create_database_and_user.sql 
mysql --user=caarray2op --password=password caarray2 < caarray_ddl.sql 

pause
