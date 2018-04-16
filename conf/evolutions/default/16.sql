# sms schema

# --- !Ups

CREATE TABLE sms (
  id                BIGSERIAL PRIMARY KEY NOT NULL,
  participant_id    BIGINT,
  training_id       BIGINT,
  category_id       BIGINT,
  quiz_id           BIGINT,
  sent              TIMESTAMP WITH TIME ZONE,
  opened            TIMESTAMP WITH TIME ZONE,
  submitted         TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE sms;