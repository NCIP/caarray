CREATE TEMPORARY TABLE duplicate_arraydata AS
  (SELECT a.id FROM arraydata a,
                    (SELECT data_file FROM arraydata GROUP BY data_file HAVING count(*) > 1) d
    WHERE a.data_file = d.data_file AND a.data_set is null
  );

DELETE FROM protocol_application WHERE array_data in (SELECT * FROM duplicate_arraydata);
DELETE FROM image WHERE raw_array_data in (SELECT * FROM duplicate_arraydata);
DELETE FROM derivedarraydata_derivedfrom WHERE derivedarraydata_id in (SELECT * FROM duplicate_arraydata);
DELETE FROM derivedarraydata_derivedfrom WHERE derivedfrom_arraydata_id in (SELECT * FROM duplicate_arraydata);
DELETE FROM arraydata_hybridizations WHERE arraydata_id in (SELECT * FROM duplicate_arraydata);
DELETE FROM arraydata WHERE id IN (SELECT * FROM duplicate_arraydata);

ALTER TABLE arraydata ADD UNIQUE (data_file);