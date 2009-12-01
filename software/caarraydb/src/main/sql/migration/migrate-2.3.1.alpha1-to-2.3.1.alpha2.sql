-- GEO accession updates from https://gforge.nci.nih.gov/svnroot/caarray2/trunk/docs/requirements/use_cases/user_stories/caArray_GEO_platforms_reviewed.xls
update array_design set geo_accession = 'GPL3921' where name = 'HT-HG_U133A' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL6244' where name = 'HuGene-1_0-st-v1.r3' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL2641' where name = 'Mapping10K_Xba142' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL2004' where name = 'Mapping50K_Hind240' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL2005' where name = 'Mapping50K_Xba240' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL5188' where name = 'HuEx-1_0-st-v2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL5188' where name = 'HuEx-1_0-st-v1' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL341' where name = 'RAE230A' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL1355' where name = 'Rat230_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL98' where name = 'Hu35KsubA' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL3738' where name = 'Canine_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
