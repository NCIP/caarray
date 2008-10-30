-- Defect 13744
alter table factor add index factor_experiment_fk (experiment), add constraint factor_experiment_fk foreign key (experiment) references experiment (id);
