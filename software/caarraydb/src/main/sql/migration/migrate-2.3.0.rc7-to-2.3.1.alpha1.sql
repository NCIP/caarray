-- GF 21413, remove Project.hostProfile
alter table project drop foreign key project_hostaccess_fk;
alter table project drop column host_profile;

-- GF 20294, add geoAccession field to array design
alter table array_design add column geo_accession varchar(254);

-- GF 20295, populate known GEO accession
-- Affymetrix:
update array_design set geo_accession = 'GPL6801' where name = 'GenomeWideSNP_6.Full' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL6804' where name = 'GenomeWideSNP_5.Full' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL570' where name = 'HG-U133_Plus_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL3718' where name = 'Mapping250K_Nsp' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL3720' where name = 'Mapping250K_Sty' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL571' where name = 'HG-U133A_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL96' where name = 'HG-U133A' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL8300' where name = 'HG_U95Av2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL92' where name = 'HG_U95B' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL93' where name = 'HG_U95C' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL94' where name = 'HG_U95D' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL95' where name = 'HG_U95E' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL9197' where name = 'HT_HG-U133B' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL97' where name = 'HG-U133B' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL80' where name = 'Hu6800' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL6244' where name = 'HuGene-1_0-st-v1' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL6801' where name = 'GenomeWideSNP_6' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL81' where name = 'mg_u74av2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL1261' where name = 'Mouse430_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL8321' where name = 'Mouse430A_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL91' where name = 'HG_U95A' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL33' where name = 'MG_U74B' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL76' where name = 'Mu11KsubB' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL75' where name = 'Mu11KsubA' and provider = (select id from contact where provider = true and name = 'Affymetrix');
-- Illumina:
-- update array_design set geo_accession = 'GPL6102 or GPL6106 or GPL6370' where name = 'Human-6 v2 sequence' and provider = (select id from contact where provider = true and name = 'Illumina');
-- update array_design set geo_accession = 'GPL6434' where name = 'HumanHap550v3_A' and provider = (select id from contact where provider = true and name = 'Illumina');
-- update array_design set geo_accession = 'GPL6255 or GPL5060 or GPL5104 or GPL6014 or GPL6104' where name = 'HumanRef-8 v2 sequence' and provider = (select id from contact where provider = true and name = 'Illumina');
-- Agilent:
-- Genepix:
