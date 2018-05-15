# passwordinfo schema

# --- !Ups

CREATE TABLE passwordinfo (
  id            BIGSERIAL PRIMARY KEY NOT NULL,
  logininfo_id  BIGINT NOT NULL,
  hasher        VARCHAR NOT NULL,
  password      VARCHAR NOT NULL,
  salt          VARCHAR
);

# --- !Downs

DROP TABLE passwordinfo;