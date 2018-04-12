# quiz schema

# --- !Ups

CREATE TABLE quiz (
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  name        VARCHAR NOT NULL,
  training_id BIGINT,
  category_id BIGINT,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at  TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE quiz;