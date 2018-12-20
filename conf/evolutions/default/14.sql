# passwordinfo schema

# --- !Ups

CREATE TABLE order_details (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  order_id INTEGER,
  good_id INTEGER,
  price DOUBLE PRECISION,
  quantity DOUBLE PRECISION
);

# --- !Downs

DROP TABLE order_details;