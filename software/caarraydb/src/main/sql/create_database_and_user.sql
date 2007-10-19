DROP DATABASE IF EXISTS @database.name@;
CREATE DATABASE @database.name@;

DELETE FROM mysql.user WHERE User='@database.user@';

GRANT SELECT,INSERT,UPDATE,DELETE ON @database.name@.* TO '@database.user@'@'localhost' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;
GRANT SELECT,INSERT,UPDATE,DELETE ON @database.name@.* TO '@database.user@'@'%' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;