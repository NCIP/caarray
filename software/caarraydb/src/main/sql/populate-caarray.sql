-- Placeholder file for caarray object populate
insert into category (name) value ('OrganismPart');
insert into category (name) value ('MaterialType');
insert into category (name) value ('CellType');
insert into category (name) value ('DiseaseState');
insert into category (name) value ('ExperimentDesignType');
insert into category (name) value ('QualityControlDescriptionType');
insert into category (name) value ('ReplicateDescriptionType');
insert into category (name) value ('Roles');

insert into termsource (name, url, version) values ('MGED', 'http://mged.sourceforge.net/ontologies/MGEDontology.php', '1.3.1.1');

insert into term (value, source, category) select 'Brain', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='OrganismPart';
insert into term (value, source, category) select 'Leg', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='OrganismPart';
insert into term (value, source, category) select 'Feed', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='OrganismPart';
insert into term (value, source, category) select 'cell lysate', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='MaterialType';
insert into term (value, source, category) select 'total RNA', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='MaterialType';
insert into term (value, source, category) select 'DNA', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='MaterialType';
insert into term (value, source, category) select 'Brain Tissue', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='CellType';
insert into term (value, source, category) select 'Chromaphine Cells', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='CellType';
insert into term (value, source, category) select 'Adrenocortical Carcinoma', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='DiseaseState';
insert into term (value, source, category) select 'Pharmacogenomic', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='ExperimentDesignType';
insert into term (value, source, category) select 'biological_replicate', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='QualityControlDescriptionType';
insert into term (value, source, category) select 'dye_swap_quality_control', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='QualityControlDescriptionType';
insert into term (value, source, category) select 'peer_review_quality_control', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='QualityControlDescriptionType';
insert into term (value, source, category) select 'biological_replicate', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='ReplicateDescriptionType';
insert into term (value, source, category) select 'dye_swap_replicate', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='ReplicateDescriptionType';
insert into term (value, source, category) select 'technical_replicate', termsource.id, category.id from termsource, category where termsource.name='MGED' and category.name='ReplicateDescriptionType';


insert into organism (common_name, scientific_name, taxonomy_rank, ethnicity_strain) values ('canine', 'Canine', '', '');
insert into organism (common_name, scientific_name, taxonomy_rank, ethnicity_strain) values ('c.feline', 'Cat', '', '');
insert into organism (common_name, scientific_name, taxonomy_rank, ethnicity_strain) values ('c.mouseful', 'Mouse', '', '');

insert into contact(discriminator, name) values ('O', 'Affymetrix');
insert into contact(discriminator, name) values ('O', 'Illumina');

insert into array_design(name, version, provider) select 'Sentrix', '1.0', id from contact where discriminator='O' and name='Affymetrix';
insert into array_design(name, version, provider) select 'Moohaa', '1.0', id from contact where discriminator='O' and name='Affymetrix';
insert into array_design(name, version, provider) select 'Ishtarti', '1.0', id from contact where discriminator='O' and name='Affymetrix';
insert into array_design(name, version, provider) select 'Illuminati', '1.0', id from contact where discriminator='O' and name='Illumina';
insert into array_design(name, version, provider) select 'Dojo', '1.0', id from contact where discriminator='O' and name='Illumina';
