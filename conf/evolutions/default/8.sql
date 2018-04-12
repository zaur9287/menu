# logininfo schema

# --- !Ups

CREATE TABLE logininfo (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    providerid          VARCHAR NOT NULL,
    providerkey         VARCHAR NOT NULL,
    userid              VARCHAR NOT NULL
);

# --- !Downs

DROP TABLE logininfo;