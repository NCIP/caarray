-- GF 22880 (ExperimentOntologyCategory.EXTERNAL_ID needs database entry)
insert ignore into category (source, name) select id, 'ExternalId' from term_source where name='caArray' and version='2.0';
