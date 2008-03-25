DROP DATABASE IF EXISTS @database.name@;
CREATE DATABASE @database.name@ DEFAULT CHARACTER SET latin1;

DELETE FROM mysql.user WHERE User='@database.user@';

GRANT ALL ON @database.name@.* TO '@database.user@'@'localhost' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;
GRANT ALL ON @database.name@.* TO '@database.user@'@'%' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;
