ALTER TABLE form_publication
ADD COLUMN languages JSONB DEFAULT '["nb"]'::jsonb NOT NULL;
