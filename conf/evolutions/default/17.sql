# result schema

# --- !Ups

CREATE TABLE result (
  id                BIGSERIAL PRIMARY KEY NOT NULL,
  sms_id            BIGINT,
  question_id       BIGINT,
  answer_id         BIGINT,
  correct           BOOLEAN,
  weight            INTEGER,
  response          INTEGER
);

# --- !Downs

DROP TABLE result;