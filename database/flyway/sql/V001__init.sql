-- noinspection SqlNoDataSourceInspectionForFile

CREATE SCHEMA fullstack;

CREATE TABLE fullstack.users
(
    id         UUID      NOT NULL,
    PRIMARY KEY (id),
    username   TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP DEFAULT NULL,
    UNIQUE (username)
);

CREATE TABLE fullstack.data_blocks
(
    id         UUID      NOT NULL,
    PRIMARY KEY (id),
    owner      UUID      NOT NULL REFERENCES fullstack.users (id),
    name       TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP DEFAULT NULL,
    UNIQUE (owner, name)
);

CREATE TABLE fullstack.data_records
(
    id            UUID NOT NULL,
    PRIMARY KEY (id),
    data_block_id UUID NOT NULL REFERENCES fullstack.data_blocks (id),
    key           TEXT NOT NULL,
    data          TEXT[] NOT NULL
);
