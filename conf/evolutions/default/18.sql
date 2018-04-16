# sms schema

# --- !Ups

ALTER TABLE sms ADD COLUMN status VARCHAR DEFAULT 'pending';
ALTER TABLE sms DROP COLUMN IF EXISTS sent;

# --- !Downs
