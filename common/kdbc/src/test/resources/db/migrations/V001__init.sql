CREATE SCHEMA testing;

CREATE TABLE testing.thingy
(
    id   UUID                  NOT NULL,
    name CHARACTER VARYING(32) NOT NULL,
    PRIMARY KEY (id)
);
