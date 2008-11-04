-- gforge defect 17119
insert into derivedarraydata_derivedfrom (derivedarraydata_id, derivedfrom_arraydata_id)
    select dadh.derivedarraydata_id, radh.rawarraydata_id from derivedarraydata_hybridizations dadh
            join rawarraydata_hybridizations radh on dadh.hybridization_id = radh.hybridization_id
        where not exists (select * from derivedarraydata_derivedfrom where derivedarraydata_id = dadh.derivedarraydata_id)
            and 1 = (select count(*) from rawarraydata_hybridizations radh2 where radh2.hybridization_id = radh.hybridization_id);
