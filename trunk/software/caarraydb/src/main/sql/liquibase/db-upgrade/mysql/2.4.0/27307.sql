create table accession (gene_id bigint not null, database_name varchar(255) not null, accession_number varchar(255) not null, primary key (gene_id, database_name, accession_number)) type=InnoDB;
alter table accession add index accession_gene_fk_idx (gene_id), add constraint accession_gene_fk_constraint foreign key (gene_id) references gene (id);

insert accession(gene_id, database_name, accession_number) select id, 'Ensemble', ensemblgeneid from gene where ensemblgeneid is not null;
insert accession(gene_id, database_name, accession_number) select id, 'Entrez Gene', entrezgeneid from gene where entrezgeneid is not null;
insert accession(gene_id, database_name, accession_number) select id, 'Genbank', genbank_accession from gene where genbank_accession is not null;
insert accession(gene_id, database_name, accession_number) select id, 'UniGene', unigeneclusterid from gene where unigeneclusterid is not null;

alter table gene
	drop column ensemblgeneid, 
	drop column entrezgeneid, 
	drop column genbank_accession, 
	drop column genbank_accession_version, 
	drop column unigeneclusterid;