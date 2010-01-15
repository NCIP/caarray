-- issue 16406
alter table hybridization add constraint name unique (name, experiment);

-- issue 17560
insert into contact(discriminator, name, provider) values ('O', 'ScanArray', true);