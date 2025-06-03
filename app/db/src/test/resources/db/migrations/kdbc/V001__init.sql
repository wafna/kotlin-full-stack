CREATE SCHEMA testing;

CREATE TABLE testing.thingy
(
    id      UUID      NOT NULL,
    name    CHARACTER VARYING(32),
    created TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
