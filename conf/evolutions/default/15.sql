
# --- !Ups
ALTER TABLE users ADD COLUMN owner BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN company_id BIGINT DEFAULT 0;


# --- !Downs