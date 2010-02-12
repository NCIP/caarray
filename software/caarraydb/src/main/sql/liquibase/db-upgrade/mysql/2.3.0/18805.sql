-- GF 18805
alter table experiment drop column payment_mechanism;
alter table experiment drop column payment_number;
alter table experiment drop column service_type;

drop table if exists payment_mechanism;
drop table if exists PAYMENT_MECHANISM;
