# answer schema

# --- !Ups

CREATE TABLE answer (
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  text        VARCHAR NOT NULL,
  correct     BOOLEAN,
  question_id BIGINT,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at  TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE answer;