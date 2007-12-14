update config_parameter set raw_value = 'false' where param = 'SEND_CONFIRM_EMAIL';
update config_parameter set raw_value = 'myAdmin@email.com' where param = 'REG_EMAIL_TO';