# Users schema

# --- !Ups

CREATE TABLE users (
    userID          VARCHAR NOT NULL PRIMARY KEY,
    fullName        VARCHAR,
    email           VARCHAR,
    avatarURL       VARCHAR,
    description     VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at      TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE users;

