# passwordinfo schema

# --- !Ups

CREATE TABLE images(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  path VARCHAR NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE images;