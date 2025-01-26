CREATE TABLE form
(
	id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	skjemanummer VARCHAR(24),
	path         VARCHAR(24),
	created_at   TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by   VARCHAR(128)             NOT NULL,
	deleted_at   TIMESTAMP WITH TIME ZONE,
	deleted_by   VARCHAR(128),
	UNIQUE (skjemanummer),
	UNIQUE (path)
);

CREATE TABLE form_revision
(
	id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_id    BIGINT                   NOT NULL,
	revision   INT                      NOT NULL,
	title      VARCHAR(128)             NOT NULL,
	components JSONB                    NOT NULL,
	properties JSONB                    NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by VARCHAR(128)             NOT NULL,
	UNIQUE (form_id, revision),
	CONSTRAINT fk_form_revision_form
		FOREIGN KEY (form_id)
			REFERENCES form (id)
);

CREATE TABLE global_translation
(
	id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	key        VARCHAR(1024) UNIQUE NOT NULL,
	tag        VARCHAR(16)          NOT NULL,
	deleted_at TIMESTAMP WITH TIME ZONE,
	deleted_by VARCHAR(128)
);

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

CREATE TABLE published_global_translation
(
	id         INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by VARCHAR(128)             NOT NULL
);

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


CREATE TABLE form_translation
(
	id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_id    BIGINT        NOT NULL,
	key        VARCHAR(1024) NOT NULL,
	deleted_at TIMESTAMP WITH TIME ZONE,
	deleted_by VARCHAR(128),
	UNIQUE (form_id, key),
	CONSTRAINT fk_form_translation_form
		FOREIGN KEY (form_id)
			REFERENCES form (id)
);

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

CREATE TABLE form_revision_translation_revision
(
	id                           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_revision_id             BIGINT NOT NULL,
	form_translation_revision_id BIGINT,
	UNIQUE (form_revision_id, form_translation_revision_id),
	CONSTRAINT fk_form_revision_translation_revision_form_translation_revision
		FOREIGN KEY (form_translation_revision_id)
			REFERENCES form_translation_revision (id),
	CONSTRAINT fk_form_revision_translation_revision_form_revision
		FOREIGN KEY (form_revision_id)
			REFERENCES form_revision (id)
);

CREATE FUNCTION form_translation_revision_check_link_to_global()
	RETURNS TRIGGER
	LANGUAGE PLPGSQL
AS $$
BEGIN
IF
NEW.nb IS NOT NULL || NEW.nn IS NOT NULL || NEW.en IS NOT NULL THEN
     RAISE EXCEPTION 'DB.FORMSAPI.001';
END IF;
RETURN NEW;
END;
$$;

CREATE TRIGGER trigger_form_translation_revision_check_insert
	BEFORE INSERT
	ON form_translation_revision
	FOR EACH ROW
	WHEN ( NEW.global_translation_id IS NOT NULL )
	EXECUTE FUNCTION form_translation_revision_check_link_to_global();

CREATE TABLE form_publication
(
	id                              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	form_revision_id                BIGINT                   NOT NULL,
	published_global_translation_id INT                      NOT NULL,
	created_at                      TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
	created_by                      VARCHAR(128)             NOT NULL,
	UNIQUE (form_revision_id, published_global_translation_id),
	CONSTRAINT fk_form_publication_form_revision
		FOREIGN KEY (form_revision_id)
			REFERENCES form_revision (id),
	CONSTRAINT fk_form_publication_published_global_translation
		FOREIGN KEY (published_global_translation_id)
			REFERENCES published_global_translation (id)
);
