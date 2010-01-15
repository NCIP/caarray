-- GF 22810 (rolling back RC4 insert of GEO provider)
delete from contact where name='GEO' and provider = true and discriminator = 'O';

-- GF 22880 (ExperimentOntologyCategory.EXTERNAL_ID needs database entry)
insert ignore into category (source, name) select id, 'ExternalId' from term_source where name='caArray' and version='2.0';

-- adding indices for performance
create index idx_name on biomaterial (name);
create index idx_external_id on biomaterial (external_id);
create index idx_name on caarrayfile (name);
create index idx_title on experiment (title);
create index idx_public_identifier on experiment (public_identifier);
create index idx_common_name on organism (common_name, term_source);
create index idx_name on contact (name);
create index idx_name on factor (name);
