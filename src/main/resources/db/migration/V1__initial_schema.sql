CREATE TABLE price_lists
(
    price_list_id UUID PRIMARY KEY,
    valid_until   TIMESTAMP NOT NULL
);

CREATE TABLE legs
(
    leg_id        UUID PRIMARY KEY,
    price_list_id UUID NOT NULL,
    FOREIGN KEY (price_list_id) REFERENCES price_lists (price_list_id)
);

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

CREATE TABLE travel_routes
(
    travel_route_id          UUID PRIMARY KEY,
    from_id                  UUID           NOT NULL,
    from_name                VARCHAR(255)   NOT NULL,
    to_id                    UUID           NOT NULL,
    to_name                  VARCHAR(255)   NOT NULL,
    total_quoted_travel_time BIGINT         NOT NULL,
    total_quoted_price       NUMERIC(10, 2) NOT NULL,
    total_quoted_duration    BIGINT         NOT NULL
);

-- Users tabel
CREATE TABLE users
(
    user_id       UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    token         VARCHAR(255),
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL
);

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

