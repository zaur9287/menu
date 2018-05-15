# tokens schema

# --- !Ups

CREATE TABLE tokens (
    id          VARCHAR NOT NULL PRIMARY KEY,
    userid      VARCHAR NOT NULL,
    expiry      TIMESTAMP WITH TIME ZONE NOT NULL
);

# --- !Downs

DROP TABLE tokens;