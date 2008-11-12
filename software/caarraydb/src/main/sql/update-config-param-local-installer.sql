update config_parameter set raw_value = 'true' where param = 'SEND_CONFIRM_EMAIL';
update config_parameter set raw_value = 'true' where param = 'SEND_ADMIN_EMAIL';
update config_parameter set raw_value = 'false' where param = 'DEVELOPMENT_MODE';
update config_parameter set raw_value = '@user.admin.email.address@' where param = 'REG_EMAIL_TO';
update config_parameter set raw_value = '@user.admin.email.address@' where param = 'EMAIL_FROM';
