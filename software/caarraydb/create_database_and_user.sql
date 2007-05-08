CREATE DATABASE IF NOT EXISTS caarray2;

GRANT ALL ON caarray2.* TO 'caarray2op'@'localhost' IDENTIFIED BY 'password' WITH GRANT OPTION;
GRANT ALL ON caarray2.* TO 'caarray2op'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;
