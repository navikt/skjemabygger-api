CREATE TABLE recipients
(
    id              INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    recipientid     VARCHAR(36) UNIQUE NOT NULL,
    name            VARCHAR(100) NOT NULL,
		poboxaddress    VARCHAR(100) NOT NULL,
		postalcode      VARCHAR(4) NOT NULL,
		postalname      VARCHAR(50) NOT NULL,
		archivesubjects VARCHAR(255),
		createdat       TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
		createdby       VARCHAR(20) NOT NULL,
		changedat       TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
		changedby       VARCHAR(20) NOT NULL
);
