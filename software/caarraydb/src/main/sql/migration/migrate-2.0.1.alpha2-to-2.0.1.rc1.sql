update caarrayfile set type = 'AGILENT_DERIVED_TXT' where type = 'AGILENT_TXT';

alter table array_design add column description text;
