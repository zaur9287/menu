CREATE TABLE images(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  path VARCHAR NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);

CREATE TABLE company(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  name VARCHAR NOT NULL,
  description VARCHAR,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);


CREATE TABLE contacts(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  property VARCHAR NOT NULL,
  value VARCHAR,
  users_id VARCHAR,
  company_id VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);

CREATE TABLE good_groups(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  parent_id INTEGER,
  name VARCHAR NOT NULL,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);


CREATE TABLE goods(
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  group_id INTEGER,
  company_id INTEGER,
  name VARCHAR NOT NULL,
  description VARCHAR,
  price DECIMAL,
  quantity DECIMAL,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);


CREATE TABLE table (
  id BIGSERIAL NOT NULL PRIMARY KEY ,
  company_id INTEGER NOT NULL,
  name VARCHAR,
  description VARCHAR,
  image_id INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);


CREATE TABLE order (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  table_id INTEGER,
  company_id INTEGER,
  user_id VARCHAR,
  good_id INTEGER,
  price DECIMAL,
  quantity DECIMAL,
  status VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);

CREATE TABLE role (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  name VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);


CREATE TABLE jobs (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  user_id VARCHAR,
  role_id INTEGER,
  company_id INTEGER,
  name VARCHAR NOT NULL,
  description VARCHAR,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at BOOLEAN
);


