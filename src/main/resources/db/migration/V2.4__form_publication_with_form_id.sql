ALTER TABLE form_publication
	ADD COLUMN form_id BIGINT;

UPDATE form_publication
SET form_id = (SELECT fr.form_id FROM form_revision fr WHERE fr.id = form_publication.form_revision_id);

ALTER TABLE form_publication
ALTER COLUMN form_id SET NOT NULL;

ALTER TABLE form_publication
	ADD CONSTRAINT fk_form_publication_form
		FOREIGN KEY (form_id)
			REFERENCES form (id);
