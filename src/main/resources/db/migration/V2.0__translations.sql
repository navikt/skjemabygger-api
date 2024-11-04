CREATE TABLE global_translation
(
	id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	key        VARCHAR(1024) UNIQUE NOT NULL,
	tag        VARCHAR(16)          NOT NULL,
	deleted_at TIMESTAMP WITH TIME ZONE,
	deleted_by VARCHAR(128)
);

-- TRIGGER: Only allow inserts into global_translation_revision? Allow delete if not published?
CREATE TABLE global_translation_revision
(
	id                    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	global_translation_id BIGINT                   NOT NULL,
	revision              INT                      NOT NULL,
	nb                    VARCHAR(1024),
	nn                    VARCHAR(1024),
	en                    VARCHAR(1024),
	created_at            TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by            VARCHAR(128)             NOT NULL,
	UNIQUE (global_translation_id, revision),
	CONSTRAINT fk_global_translation
		FOREIGN KEY (global_translation_id)
			REFERENCES global_translation (id)
);

-- TRIGGER: Do not allow delete
CREATE TABLE published_global_translation
(
	id         INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by VARCHAR(128)             NOT NULL
);

-- TRIGGER: Do not allow delete
CREATE TABLE published_global_translation_revision
(
	published_global_translation_id INT,
	global_translation_revision_id  BIGINT,
	PRIMARY KEY (published_global_translation_id, global_translation_revision_id),
	CONSTRAINT fk_published_global_translation
		FOREIGN KEY (published_global_translation_id)
			REFERENCES published_global_translation (id),
	CONSTRAINT fk_global_translation_revision
		FOREIGN KEY (global_translation_revision_id)
			REFERENCES global_translation_revision (id)
);


-- Trigger: Only allow inserts into form_translation? And allow DELETE if not published?
-- form_path is temporary until we also migrate the forms to separate tables and replace form_path with a foreign key to table form
CREATE TABLE form_translation
(
	id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_path VARCHAR(32),
	key       VARCHAR(1024) NOT NULL,
	UNIQUE (form_path, key)
);

-- Trigger: Only allow inserts into form_translation_revision? And possibly DELETE if not published?
-- And to ensure nb,nn,en is null if global_translation_id is not null, and that global_translation_id is null if any of nb/nn/en exists?
CREATE TABLE form_translation_revision
(
	id                    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_translation_id   BIGINT                   NOT NULL,
	revision              INT                      NOT NULL,
	global_translation_id BIGINT,
	nb                    VARCHAR(1024),
	nn                    VARCHAR(1024),
	en                    VARCHAR(1024),
	created_at            TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by            VARCHAR(128)             NOT NULL,
	UNIQUE (form_translation_id, revision),
	CONSTRAINT fk_form_translation
		FOREIGN KEY (form_translation_id)
			REFERENCES form_translation (id),
	CONSTRAINT fk_global_translation
		FOREIGN KEY (global_translation_id)
			REFERENCES global_translation (id)
);

-- form_path is temporary until we also migrate the forms to separate tables and replace form_path with a foreign key to table form_revision
CREATE TABLE form_revision_translation_revision
(
	id                           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_path                    VARCHAR(32),
	form_translation_revision_id BIGINT,
	UNIQUE (form_path, form_translation_revision_id),
	CONSTRAINT form_translation_revision
		FOREIGN KEY (form_translation_revision_id)
			REFERENCES form_translation_revision (id)
);
