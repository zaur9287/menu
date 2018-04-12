# Interfaces schema

# --- !Ups

CREATE TABLE interfaces (
    id              BIGSERIAL NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at      TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE interfaces;