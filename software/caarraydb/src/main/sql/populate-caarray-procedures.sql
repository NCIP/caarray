-- this script should be run with the delimiter set to "//"

drop procedure if exists create_features;//
create procedure create_features (in rows int, in cols int, in design_details_id bigint)
begin
    declare x int default 0;
    declare y int default 0;
    while (y < rows) do
        while (x < cols) do
            insert into design_element (discriminator, feature_column, feature_row, block_column, block_row, feature_details_id) values ('F', x, y, 0, 0, design_details_id);
            set x = x + 1;
        end while;
        set y = y + 1;
        set x = 0;
    end while;
end;
//
