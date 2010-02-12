-- GF 12110 - adding indices for performance
create index idx_name on biomaterial (name);
create index idx_external_id on biomaterial (external_id);
create index idx_name on caarrayfile (name);
create index idx_title on experiment (title);
create index idx_public_identifier on experiment (public_identifier);
create index idx_common_name on organism (common_name, term_source);
create index idx_name on contact (name);
create index idx_name on factor (name);
