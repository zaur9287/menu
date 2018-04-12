# Clients schema

# --- !Ups

CREATE TABLE contacts (
    id            BIGSERIAL       NOT NULL,
    client_id     BIGSERIAL       NOT NULL,
    mobile        VARCHAR(20)           ,
    description   VARCHAR(255)          ,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at    TIMESTAMP WITH TIME ZONE
);

# --- !Downs

DROP TABLE contacts;