-- GEO accession updates from https://gforge.nci.nih.gov/svnroot/caarray2/trunk/docs/requirements/use_cases/user_stories/caArray_GEO_platforms_reviewed.xls
update array_design set geo_accession = 'GPL3921' where name = 'HT_HG-U133A' and provider = (select id from contact where provider = true and name = 'Affymetrix');
update array_design set geo_accession = 'GPL3738' where name = 'Canine_2' and provider = (select id from contact where provider = true and name = 'Affymetrix');
