# passwordinfo schema

# --- !Ups

CREATE TABLE role (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  name VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE role;