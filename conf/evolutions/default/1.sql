# Clients schema

# --- !Ups

CREATE TABLE clients (
    id BIGSERIAL NOT NULL,
    name varchar(255) NOT NULL,
    description VARCHAR(255),
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE clients;