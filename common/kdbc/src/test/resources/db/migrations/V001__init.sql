-- @formatter:off
SET search_path TO public;
DROP EXTENSION IF EXISTS "uuid-ossp";
CREATE EXTENSION "uuid-ossp" SCHEMA public;
-- @formatter:on

CREATE SCHEMA testing;

CREATE TABLE testing.thingy
(
    id   UUID DEFAULT uuid_generate_v4() NOT NULL,
    name CHARACTER VARYING(32)           NOT NULL,
    PRIMARY KEY (id)
);
