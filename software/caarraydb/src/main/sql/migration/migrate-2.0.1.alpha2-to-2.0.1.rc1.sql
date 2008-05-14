update caarrayfile set type = 'AGILENT_DERIVED_TXT' where type = 'AGILENT_TXT';

alter table array_design add column description text;

create index ap_name_idx on design_element (name);