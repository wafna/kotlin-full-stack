-- @formatter:off
SET search_path TO public;
DROP EXTENSION IF EXISTS "uuid-ossp";
CREATE EXTENSION "uuid-ossp" SCHEMA public;
-- @formatter:on

CREATE SCHEMA widgets;

CREATE TABLE widgets.servers
(
    id        UUID DEFAULT uuid_generate_v4() NOT NULL,
    host_name CHARACTER VARYING(32)           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE widgets.services
(
    id        UUID DEFAULT uuid_generate_v4() NOT NULL,
    server_id UUID                            NOT NULL,
    path      CHARACTER VARYING(128)          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (server_id) REFERENCES widgets.servers (id)
);

CREATE TABLE widgets.service_trusts
(
    id          UUID DEFAULT uuid_generate_v4() NOT NULL,
    service_id  UUID                            NOT NULL,
    consumer_id UUID                            NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (consumer_id) REFERENCES widgets.servers (id),
    FOREIGN KEY (service_id) REFERENCES widgets.services (id)
);