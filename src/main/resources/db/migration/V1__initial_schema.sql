-- Tabels price_lists, legs, providers, routes is used to store the data from the external API
DROP TABLE IF EXISTS price_lists;
CREATE TABLE price_lists
(
    price_list_id UUID PRIMARY KEY,
    valid_until   TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS legs;
CREATE TABLE legs
(
    leg_id        UUID PRIMARY KEY,
    price_list_id UUID NOT NULL,
    FOREIGN KEY (price_list_id) REFERENCES price_lists (price_list_id)
);

DROP TABLE IF EXISTS providers;
CREATE TABLE providers
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

DROP TABLE IF EXISTS routes;
CREATE TABLE routes
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

-- The table flight_routes is an intermediate table that contains only the necessary fields to compile the final routes.
-- The table flight_routes includes all possible flights offered by various providers.
DROP TABLE IF EXISTS flight_routes;
CREATE TABLE flight_routes
(
    flight_route_id          UUID PRIMARY KEY,
    from_name                VARCHAR(255)   NOT NULL,
    to_name                  VARCHAR(255)   NOT NULL,
    flight_start             TIMESTAMP      NOT NULL,
    flight_end               TIMESTAMP      NOT NULL,
    company_name             VARCHAR(255)   NOT NULL,
    total_quoted_price       NUMERIC(10, 2) NOT NULL,
    total_quoted_travel_time BIGINT         NOT NULL,
    total_quoted_distance    BIGINT         NOT NULL
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
    total_distance     BIGINT         NOT NULL
);


-- Users tabel
-- The table users is used to store the user data.
CREATE TABLE users
(
    user_id       UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    token         VARCHAR(255),
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL
);

-- Reservations tabel
-- The table reservations is used to store the reservation data.
CREATE TABLE reservations
(
    reservation_id           UUID PRIMARY KEY,
    user_id                  UUID           NOT NULL,
    total_quoted_price       NUMERIC(10, 2) NOT NULL,
    total_quoted_travel_time BIGINT         NOT NULL,
    price_list_id            UUID           NOT NULL,
    FOREIGN KEY (price_list_id) REFERENCES price_lists (price_list_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- The table reservation_routes is an intermediate table that contains only the necessary fields to compile the final routes.
CREATE TABLE reservation_routes
(
    id             UUID PRIMARY KEY,
    reservation_id UUID NOT NULL,
    route_id       UUID NOT NULL,
    provider_id    UUID NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id),
    FOREIGN KEY (route_id) REFERENCES routes (route_id),
    FOREIGN KEY (provider_id) REFERENCES providers (provider_id)
);
