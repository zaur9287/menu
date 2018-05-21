# passwordinfo schema

# --- !Ups

CREATE TABLE table (
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  company_id INTEGER NOT NULL,
  name VARCHAR,
  description VARCHAR,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE table;