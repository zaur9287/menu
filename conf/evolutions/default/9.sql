# passwordinfo schema

# --- !Ups

CREATE TABLE goods(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  group_id INTEGER,
  company_id INTEGER,
  name VARCHAR NOT NULL,
  description VARCHAR,
  price DECIMAL,
  quantity DECIMAL,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE goods;