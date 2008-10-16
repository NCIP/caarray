-- Defect 13744 factor -> experiment - there's only one FK, but because of the lowercase/uppercase issues,
-- older DBs might have a different FK name
alter table factor drop foreign key FKB393F94F3931A16B;
alter table factor drop foreign key FK7B26ED4F3931A16B;
