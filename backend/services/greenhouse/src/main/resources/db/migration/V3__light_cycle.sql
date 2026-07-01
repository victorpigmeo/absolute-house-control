-- Persists named LED "light cycles" (e.g. Veg, Flora), each with a 6-field
-- cron expression for when the LED turns on and another for when it turns
-- off. At most one cycle is active at a time; `active` defaults to false so
-- newly created cycles never preempt whichever cycle is already running.
-- `id`, `created_at`, `updated_at`, and `version` back the shared JPA
-- BaseEntity used by all entities in this service.
CREATE TABLE light_cycle (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    on_cron VARCHAR NOT NULL,
    off_cron VARCHAR NOT NULL,
    active BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    version BIGINT NOT NULL
);
