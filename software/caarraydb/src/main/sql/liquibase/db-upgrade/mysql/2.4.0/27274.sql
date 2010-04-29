ALTER TABLE design_element ADD COLUMN feature_x_coordinate double precision, ADD COLUMN feature_y_coordinate double precision, ADD COLUMN feature_coordinate_units varchar(2), ADD COLUMN feature_number bigint;
ALTER TABLE probeannotation ADD COLUMN chromosome_name varchar(255), ADD COLUMN chromosome_start_position bigint, ADD COLUMN chromosome_end_position bigint;
