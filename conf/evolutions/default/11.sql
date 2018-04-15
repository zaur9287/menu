# category schema

# --- !Ups

CREATE TABLE category (
  id    BIGSERIAL PRIMARY KEY NOT NULL,
  name  VARCHAR NOT NULL,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at  TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE category;