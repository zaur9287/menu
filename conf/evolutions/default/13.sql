# passwordinfo schema

# --- !Ups

CREATE TABLE jobs (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  user_id VARCHAR,
  role_id INTEGER,
  company_id INTEGER,
  name VARCHAR NOT NULL,
  description VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE jobs;