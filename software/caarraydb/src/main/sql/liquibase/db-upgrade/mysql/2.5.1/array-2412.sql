alter table caarrayfile add column unique_name_project boolean;
alter table caarrayfile add constraint unique_name_project_constraint unique (name, project, unique_name_project);

update caarrayfile set unique_name_project = 1 where project is null or id in
  (select myid from
      (select count(*) as mycount, sum(id) as myid, project, name from caarrayfile
       where parent_id is null group by project, name) groups
   where mycount = 1);
