# passwordinfo schema

# --- !Ups

CREATE TABLE contacts(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  property VARCHAR NOT NULL,
  value VARCHAR,
  users_id VARCHAR,
  company_id VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE contacts;