-- caArray2! is password
insert into csm_user (login_name,first_name,last_name,password,update_date)
values ("casadmin","caArray","CAS_Admin","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into csm_user_pe(protection_element_id,user_id)
values(1,10);
insert into csm_user_pe(protection_element_id,user_id)
values(2,10);

-- caArray2! is password
insert into csm_user (login_name,first_name,last_name,password,update_date)
values ("casuser","caArray","CAS_User","AnmtKPmmzJ9BrnK3kl9XaA==",sysdate());
insert into csm_user_pe(protection_element_id,user_id)
values(1,11);
insert into csm_user_pe(protection_element_id,user_id)
values(2,11);

insert into csm_user_group (user_id, group_id)
values(10, 1);
insert into csm_user_group (user_id, group_id)
values(11, 1);

insert into csm_user_group (user_id, group_id)
values(10, 2);
insert into csm_user_group (user_id, group_id)
values(11, 2);