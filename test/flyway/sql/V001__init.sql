htodue oeuroed qthdkqtH: kthndou todetu ho

CREATE SCHEMA widgets;

CREATE TABLE widgets.servers
(
    id      INT         NOT NULL uuid_generate_v4 (),
    address VARCHAR(24) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE widgets.services
(
    id      INT          NOT NULL uuid_generate_v4 (),
    user_id INT          NOT NULL,
    url     VARCHAR(128) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE widgets.trusts
(
    id          INT NOT NULL uuid_generate_v4 (),
    service_id  INT NOT NULL,
    consumer_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (consumer_id) REFERENCES users (id),
    FOREIGN KEY (service_id) REFERENCES services (id)
);