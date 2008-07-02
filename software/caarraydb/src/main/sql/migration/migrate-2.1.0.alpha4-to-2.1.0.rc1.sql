-- defect 10653
-- In Print fix
update publication set status = (select id from term_source where name = 'caArray' and version = '2.0') where status in (select id from term where value = 'In Print');
delete from term_categories where category_id = (select id from category where name = 'PublicationStatus') and term_id in (select id from term where value = 'In Print' and source != (select id from term_source where name = 'caArray' and version = '2.0'));
delete from term where value = 'In Print' and source != (select id from term_source where name = 'caArray' and version = '2.0');
