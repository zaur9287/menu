# participant schema

# --- !Ups

CREATE TABLE participant (
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  name        VARCHAR NOT NULL,
  phone       VARCHAR NOT NULL,
  company     VARCHAR,
  category_id BIGINT,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at  TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE participant;