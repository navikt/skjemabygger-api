CREATE TABLE recipient
(
    id               INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    recipient_id     VARCHAR(36) UNIQUE NOT NULL,
    name             VARCHAR(100) NOT NULL,
		po_box_address   VARCHAR(100) NOT NULL,
		postal_code      VARCHAR(4) NOT NULL,
		postal_name      VARCHAR(50) NOT NULL,
		created_at       TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
		created_by       VARCHAR(20) NOT NULL,
		changed_at       TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
		changed_by       VARCHAR(20) NOT NULL
);
