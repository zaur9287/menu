# question schema

# --- !Ups

CREATE TABLE question (
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  text        VARCHAR NOT NULL,
  weight      INTEGER,
  quiz_id     BIGINT,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at  TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE question;