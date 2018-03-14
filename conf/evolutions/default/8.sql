# userlogininfo schema

# --- !Ups

CREATE TABLE userlogininfo (
    userID             VARCHAR NOT NULL,
    loginInfoId        BIGINT NOT NULL
);

# --- !Downs

DROP TABLE userlogininfo;