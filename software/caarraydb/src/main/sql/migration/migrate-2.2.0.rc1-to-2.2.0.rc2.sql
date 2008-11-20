-- issue 16406
alter table hybridization add constraint name unique (name, experiment);
