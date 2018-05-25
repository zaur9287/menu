# passwordinfo schema

# --- !Ups

CREATE TABLE order_details (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  order_id INTEGER,
  good_id INTEGER,
  price DOUBLE,
  quantity DOUBLE
);

# --- !Downs

DROP TABLE order_details;