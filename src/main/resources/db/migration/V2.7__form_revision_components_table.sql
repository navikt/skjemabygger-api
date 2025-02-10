CREATE TABLE form_revision_components
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    value            JSONB  NOT NULL,
    form_revision_id BIGINT NOT NULL
);

ALTER TABLE form_revision
    ADD COLUMN form_revision_components_id BIGINT;

INSERT INTO form_revision_components (value, form_revision_id)
SELECT components, id
FROM form_revision;

UPDATE form_revision
SET form_revision_components_id = frd.id FROM form_revision_components frd
WHERE form_revision.id = frd.form_revision_id;

ALTER TABLE form_revision
    ADD CONSTRAINT fk_form_revision_components
        FOREIGN KEY (form_revision_components_id)
            REFERENCES form_revision_components (id);

ALTER TABLE form_revision_components
DROP
COLUMN form_revision_id;

ALTER TABLE form_revision
DROP
COLUMN components;
