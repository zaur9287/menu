# passwordinfo schema

# --- !Ups

CREATE TABLE company(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  name VARCHAR NOT NULL,
  description VARCHAR,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE company;