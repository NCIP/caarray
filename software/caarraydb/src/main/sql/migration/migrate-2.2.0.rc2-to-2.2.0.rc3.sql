-- issue 17568
update config_parameter set raw_value = '@user.admin.email.address@' where param = 'REG_EMAIL_TO';
update config_parameter set raw_value = '@user.admin.email.address@' where param = 'EMAIL_FROM';