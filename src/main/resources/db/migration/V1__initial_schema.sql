-- Tabels price_lists, legs, providers, routes is used to store the data from the external API
CREATE TABLE IF NOT EXISTS price_lists
(
    price_list_id UUID PRIMARY KEY,
    valid_until   TIMESTAMP NOT NULL,
    is_active     BOOLEAN   NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS legs
(
    leg_id        UUID PRIMARY KEY,
    price_list_id UUID NOT NULL,
    FOREIGN KEY (price_list_id) REFERENCES price_lists (price_list_id)
);

CREATE TABLE IF NOT EXISTS providers
(
    provider_id  UUID PRIMARY KEY,
    company_id   UUID           NOT NULL,
    company_name VARCHAR(255)   NOT NULL,
    price        NUMERIC(10, 2) NOT NULL,
    flight_start TIMESTAMP      NOT NULL,
    flight_end   TIMESTAMP      NOT NULL,
    duration     BIGINT         NOT NULL,
    leg_id       UUID           NOT NULL,
    FOREIGN KEY (leg_id) REFERENCES legs (leg_id),
    CONSTRAINT provider_flight_duration CHECK (flight_end > flight_start)
);

CREATE TABLE IF NOT EXISTS routes
(
    route_id  UUID PRIMARY KEY,
    from_id   UUID         NOT NULL,
    from_name VARCHAR(255) NOT NULL,
    to_id     UUID         NOT NULL,
    to_name   VARCHAR(255) NOT NULL,
    distance  BIGINT       NOT NULL,
    leg_id    UUID         NOT NULL UNIQUE,
    FOREIGN KEY (leg_id) REFERENCES legs (leg_id)
);

-- The table combined_routes is a table that contains all routes from different providers to every planet.
DROP TABLE IF EXISTS combined_routes;
CREATE TABLE combined_routes
(
    combined_route_id  UUID PRIMARY KEY,
    from_name          VARCHAR(255)   NOT NULL,
    to_name            VARCHAR(255)   NOT NULL,
    route              TEXT           NOT NULL,
    first_flight_start TIMESTAMP      NOT NULL,
    last_flight_end    TIMESTAMP      NOT NULL,
    company_names      TEXT           NOT NULL,
    total_price        NUMERIC(10, 2) NOT NULL,
    total_travel_time  BIGINT         NOT NULL,
    total_distance     BIGINT         NOT NULL,
    price_list_id      UUID           NOT NULL,
    valid_until        TIMESTAMP      NOT NULL
);


-- Reservations tabel
-- The table reservations is used to store the reservation data.
CREATE TABLE reservations
(
    reservation_id           UUID PRIMARY KEY,
    first_name               VARCHAR(255)   NOT NULL,
    last_name                VARCHAR(255)   NOT NULL,
    route                    TEXT           NOT NULL,
    total_quoted_price       NUMERIC(10, 2) NOT NULL,
    total_quoted_travel_time NUMERIC(10, 2) NOT NULL,
    company_names            TEXT           NOT NULL,
    price_list_id            UUID           NOT NULL,
    FOREIGN KEY (price_list_id) REFERENCES price_lists (price_list_id)
);
