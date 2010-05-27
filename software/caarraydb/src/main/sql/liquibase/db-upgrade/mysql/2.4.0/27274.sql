ALTER TABLE design_element ADD COLUMN feature_number bigint, ADD COLUMN feature_x_coordinate double precision, ADD COLUMN feature_y_coordinate double precision, ADD COLUMN feature_coordinate_units bigint(19) null, add constraint feature_coordinate_units_fk foreign key (feature_coordinate_units) references term (id);