create table mirna_accession (annotation_id bigint not null, database_name varchar(255) not null, accession_number varchar(255) not null, primary key (annotation_id, database_name, accession_number)) type=InnoDB;
