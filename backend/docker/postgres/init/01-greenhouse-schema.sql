-- Creates the greenhouse schema and a least-privilege role/user scoped to it.
-- Local-dev-only placeholder credentials; never reuse near production.

CREATE SCHEMA IF NOT EXISTS greenhouse;

CREATE ROLE greenhouse_app WITH LOGIN PASSWORD 'greenhouse_app';

-- PostgreSQL grants PUBLIC default CONNECT on the database and USAGE on the
-- public schema; revoke both so access is exactly what's granted below, not
-- whatever PUBLIC has by default.
REVOKE CONNECT ON DATABASE house_control FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT CONNECT ON DATABASE house_control TO greenhouse_app;

-- USAGE+CREATE is sufficient: Flyway connects as greenhouse_app itself, so
-- tables it creates are already owned by greenhouse_app (no extra grant
-- needed on objects this role creates itself).
GRANT USAGE, CREATE ON SCHEMA greenhouse TO greenhouse_app;

-- No grants on any other schema -- greenhouse_app cannot see/touch other
-- services' schemas (e.g. lighting, once it exists).
