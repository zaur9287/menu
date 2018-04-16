# qui schema

# --- !Ups

ALTER TABLE quiz DROP COLUMN IF EXISTS spiker;
ALTER TABLE quiz ADD COLUMN spiker VARCHAR DEFAULT '';

# --- !Downs
