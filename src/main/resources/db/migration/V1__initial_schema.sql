CREATE TABLE pricelists
(
    pricelist_id UUID PRIMARY KEY,
    valid_until  TIMESTAMP NOT NULL,
    created_at   TIMESTAMP NOT NULL
);

CREATE TABLE legs
(
    leg_id       UUID PRIMARY KEY,
    route_id     UUID UNIQUE,
    from_id      UUID        NOT NULL,
    from_name    VARCHAR(50) NOT NULL,
    to_id        UUID        NOT NULL,
    to_name      VARCHAR(50) NOT NULL,
    distance     BIGINT      NOT NULL,
    pricelist_id UUID        NOT NULL REFERENCES pricelists (pricelist_id)
);

CREATE TABLE providers
(
    provider_id  UUID           NOT NULL,
    company_id   UUID           NOT NULL,
    company_name VARCHAR(100)   NOT NULL,
    price        NUMERIC(10, 2) NOT NULL,
    flight_start TIMESTAMP      NOT NULL,
    flight_end   TIMESTAMP      NOT NULL,
    leg_id       UUID           NOT NULL,
    PRIMARY KEY (provider_id, leg_id),
    CONSTRAINT provider_flight_duration CHECK (flight_end > flight_start),
    FOREIGN KEY (leg_id) REFERENCES legs (leg_id)
);

-- Routes tabel
CREATE TABLE routes
(
    id             UUID           NOT NULL,
    startId        UUID           NOT NULL,
    endId          UUID           NOT NULL,
    path           TEXT           NOT NULL, -- Salvestab kõik planeedid (nimed)
    total_distance NUMERIC(10, 2) NOT NULL,
    total_price    NUMERIC(10, 2) NOT NULL,
    total_duration BIGINT         NOT NULL
);

-- Users tabel
CREATE TABLE users
(
    user_id    SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL
);

CREATE TABLE reservations
(
    reservation_id    SERIAL PRIMARY KEY,
    user_id           INT            NOT NULL REFERENCES users (user_id),
    leg_id            UUID           NOT NULL,
    provider_id       UUID           NOT NULL,
    total_price       NUMERIC(10, 2) NOT NULL,
    total_travel_time INTERVAL       NOT NULL,
    reservation_time  TIMESTAMP      NOT NULL,
    FOREIGN KEY (leg_id) REFERENCES legs (leg_id),
    FOREIGN KEY (provider_id, leg_id) REFERENCES providers (provider_id, leg_id)
);
