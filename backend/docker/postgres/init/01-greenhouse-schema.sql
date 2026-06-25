-- Creates a least-privilege role for the greenhouse service to connect as.
-- Schema creation is now Flyway's job (spring.flyway.create-schemas: true,
-- see V1__baseline.sql), not this script.
-- Local-dev-only placeholder credentials; never reuse near production.

CREATE ROLE sa_absolute_house_control WITH LOGIN PASSWORD 'sa_absolute_house_control';

-- PostgreSQL grants PUBLIC default CONNECT on the database and USAGE on the
-- public schema; revoke both so access is exactly what's granted below, not
-- whatever PUBLIC has by default.
REVOKE CONNECT ON DATABASE absolute_house_control FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT CONNECT ON DATABASE absolute_house_control TO sa_absolute_house_control;

-- CREATE ON DATABASE lets Flyway (connecting as this role) create the
-- greenhouse schema itself; the role becomes its owner and so already has
-- full rights on it, with no separate per-schema grant needed.
GRANT CREATE ON DATABASE absolute_house_control TO sa_absolute_house_control;
