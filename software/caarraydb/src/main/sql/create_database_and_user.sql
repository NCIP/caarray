DROP DATABASE IF EXISTS @database.name@;
CREATE DATABASE @database.name@;

GRANT ALL ON @database.name@.* TO '@database.user@'@'localhost' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;
GRANT ALL ON @database.name@.* TO '@database.user@'@'%' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;