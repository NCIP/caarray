DROP DATABASE IF EXISTS @database.name@;
CREATE DATABASE @database.name@;

GRANT ALL ON @database.name@.* TO '@database.user@'@'@database.server@' IDENTIFIED BY '@database.password@' WITH GRANT OPTION;
