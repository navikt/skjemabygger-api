CREATE SEQUENCE recipients_id_seq;

CREATE TABLE recipients
(
    id              BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('recipients_id_seq'::regclass),
    recipientid     VARCHAR(255) NOT NULL,
    name            VARCHAR(100),
		poboxaddress    VARCHAR(100),
		postalcode      VARCHAR(4),
		postalname      VARCHAR(50),
		archivesubjects VARCHAR(255),
		createdat       TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
		createdby       VARCHAR(20),
		changedat       TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
		changedby       VARCHAR(20)
);

ALTER TABLE recipients
ADD CONSTRAINT constraint_name UNIQUE (recipientid);

CREATE INDEX recipients_recipientid_idx ON recipients(recipientid);
