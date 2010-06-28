alter table gene
	add ensemblgeneid varchar(255), 
	add entrezgeneid varchar(255), 
	add genbank_accession varchar(255), 
	add genbank_accession_version varchar(255), 
	add unigeneclusterid varchar(255),
	add refseq_accession varchar(255),
	add thc_accession varchar(255);
	
update gene set ensemblgeneid = (select accession_number from accession where accession.gene_id = gene.id and accession.database_name = 'Ensemble' limit 1);
update gene set entrezgeneid = (select accession_number from accession where accession.gene_id = gene.id and accession.database_name = 'Entrez Gene' limit 1);
update gene set genbank_accession = (select accession_number from accession where accession.gene_id = gene.id and accession.database_name = 'Genbank' limit 1);
update gene set unigeneclusterid = (select accession_number from accession where accession.gene_id = gene.id and accession.database_name = 'UniGene' limit 1);
update gene set refseq_accession = (select accession_number from accession where accession.gene_id = gene.id and accession.database_name = 'RefSeq' limit 1);
update gene set thc_accession = (select accession_number from accession where accession.gene_id = gene.id and accession.database_name = 'THC' limit 1);

delete accession from accession, gene where gene.id = accession.gene_id and accession.database_name = 'Ensemble' and accession.accession_number = gene.ensemblgeneid;
delete accession from accession, gene where gene.id = accession.gene_id and accession.database_name = 'Entrez Gene' and accession.accession_number = gene.entrezgeneid;
delete accession from accession, gene where gene.id = accession.gene_id and accession.database_name = 'Genbank' and accession.accession_number = gene.genbank_accession;
delete accession from accession, gene where gene.id = accession.gene_id and accession.database_name = 'UniGene' and accession.accession_number = gene.unigeneclusterid;
delete accession from accession, gene where gene.id = accession.gene_id and accession.database_name = 'RefSeq' and accession.accession_number = gene.refseq_accession;
delete accession from accession, gene where gene.id = accession.gene_id and accession.database_name = 'THC' and accession.accession_number = gene.thc_accession;
