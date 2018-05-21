# passwordinfo schema

# --- !Ups

CREATE TABLE order (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  table_id INTEGER,
  company_id INTEGER,
  user_id VARCHAR,
  good_id INTEGER,
  price DECIMAL,
  quantity DECIMAL,
  status VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE order;