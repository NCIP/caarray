DROP DATABASE IF EXISTS caarray2;
CREATE DATABASE IF NOT EXISTS caarray2;

USE mysql;
GRANT ALL ON caarray2.* TO 'caarray2op'@'localhost' IDENTIFIED BY 'password' WITH GRANT OPTION;
GRANT ALL ON caarray2.* TO 'caarray2op'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;
