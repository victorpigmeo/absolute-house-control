-- Persists the last-known on/off state of each greenhouse actuator (LED,
-- fan) so it survives service restarts instead of only living in the last
-- command sent this session. `device` is the actuator's short name (e.g.
-- "led", "fan"); a device with no row yet defaults to off. `id`, `created_at`,
-- `updated_at`, and `version` back the shared JPA BaseEntity used by all
-- entities in this service.
CREATE TABLE actuator_state (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    device VARCHAR(32) NOT NULL UNIQUE,
    is_on BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    version BIGINT NOT NULL
);
