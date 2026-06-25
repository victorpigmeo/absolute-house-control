-- Baseline migration for the greenhouse service schema. The greenhouse
-- schema itself is created by Flyway automatically before this file runs
-- (spring.flyway.create-schemas: true), not by this file or any init
-- script.
-- Otherwise intentionally minimal: no domain tables yet. Proves Flyway is
-- wired correctly against the greenhouse schema and establishes its
-- migration history.

CREATE TABLE IF NOT EXISTS greenhouse_schema_marker (
    id SMALLINT PRIMARY KEY DEFAULT 1,
    bootstrapped_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT greenhouse_schema_marker_singleton CHECK (id = 1)
);

INSERT INTO greenhouse_schema_marker (id) VALUES (1);
