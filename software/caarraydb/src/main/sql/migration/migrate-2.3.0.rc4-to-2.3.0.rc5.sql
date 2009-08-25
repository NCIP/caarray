-- GF 22810 (rolling back RC4 insert of GEO provider)
delete from contact where name='GEO' and provider = true and discriminator = 'O';

-- GF 22880 (ExperimentOntologyCategory.EXTERNAL_ID needs database entry)
insert into category (source, name) select id, 'ExternalId' from term_source where name='caArray';