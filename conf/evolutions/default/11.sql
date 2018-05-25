# passwordinfo schema

# --- !Ups

CREATE TABLE order_ (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  table_id INTEGER,
  company_id INTEGER,
  user_id VARCHAR,
  status VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted BOOLEAN
);

# --- !Downs

DROP TABLE order_;