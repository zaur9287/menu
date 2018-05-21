# passwordinfo schema

# --- !Ups

CREATE TABLE good_groups(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  parent_id INTEGER,
  name VARCHAR NOT NULL,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE good_groups;