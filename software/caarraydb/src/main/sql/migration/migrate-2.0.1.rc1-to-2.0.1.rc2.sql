alter table array_design modify assay_type varchar(254) not null;
alter table array_design modify technology_type bigint(20) not null;
alter table array_design modify version varchar(254) not null;
alter table array_design modify provider bigint(20) not null;
alter table array_design modify design_file bigint(20) not null;
alter table array_design modify organism bigint(20) not null;

alter table gene modify full_name varchar(2000);