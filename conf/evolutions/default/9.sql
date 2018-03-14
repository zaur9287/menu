# passwordinfo schema

# --- !Ups

CREATE TABLE passwordinfo (
    hasher          VARCHAR NOT NULL,
    password        VARCHAR NOT NULL,
    salt            VARCHAR,
    loginInfoId     BIGINT NOT NULL
);

# --- !Downs

DROP TABLE passwordinfo;
