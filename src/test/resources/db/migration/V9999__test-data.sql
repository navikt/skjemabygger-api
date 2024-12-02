INSERT INTO recipient(recipient_id, name, po_box_address, postal_code, postal_name, created_by, changed_by)
  VALUES ('1', 'NAV Økonomi Stønad', 'Postboks 354', '8601', 'MO I RANA', 'test', 'test');

INSERT INTO recipient(recipient_id, name, po_box_address, postal_code, postal_name, created_by, changed_by)
  VALUES ('2', 'NAV Skanning sykmelding del A', 'Postboks 1411 Sentrum', '0109', 'Oslo', 'test', 'test');

select * from recipient;

-- Set start value for id sequences to decrease the chance of accidental green test runs due to little test data
SELECT setval('global_translation_id_seq', 21);
SELECT setval('global_translation_revision_id_seq', 1591);
