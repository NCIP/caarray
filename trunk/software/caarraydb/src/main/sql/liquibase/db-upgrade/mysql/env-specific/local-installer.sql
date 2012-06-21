update config_parameter set raw_value = 'true' where param = 'SEND_CONFIRM_EMAIL';
update config_parameter set raw_value = 'true' where param = 'SEND_ADMIN_EMAIL';
update config_parameter set raw_value = 'false' where param = 'DEVELOPMENT_MODE';
update config_parameter set raw_value = '@user.admin.email.address.to@' where param = 'REG_EMAIL_TO' and '@user.admin.email.address.to@' not like '@%@';
update config_parameter set raw_value = '@user.admin.email.address.from@' where param = 'EMAIL_FROM' and '@user.admin.email.address.from@' not like '@%@';
