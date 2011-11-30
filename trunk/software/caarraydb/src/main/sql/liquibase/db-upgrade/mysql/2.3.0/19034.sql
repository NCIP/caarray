-- Implementation of Items 18907 and 19034 - separate project locking, and publication.
alter table project change column public_id_locked locked bit not null;
update project set locked = (status = 'PUBLIC');
alter table project drop column status;
