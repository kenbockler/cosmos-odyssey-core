CREATE TABLE legs
(
    legs_id   UUID PRIMARY KEY,
    route_id  UUID UNIQUE,
    from_id   UUID        NOT NULL,
    from_name VARCHAR(50) NOT NULL,
    to_id     UUID        NOT NULL,
    to_name   VARCHAR(50) NOT NULL,
    distance  BIGINT      NOT NULL
);

CREATE TABLE providers
(
    provider_id  UUID PRIMARY KEY,
    company_id   UUID NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    price        NUMERIC(10, 2) NOT NULL,
    flight_start TIMESTAMP NOT NULL,
    flight_end   TIMESTAMP NOT NULL,
    legs_id      UUID NOT NULL REFERENCES legs (legs_id)
);

CREATE TABLE pricelists
(
    pricelist_id UUID PRIMARY KEY,
    valid_until  TIMESTAMP NOT NULL,
    legs_id      UUID      NOT NULL REFERENCES legs (legs_id),
    created_at   TIMESTAMP NOT NULL
);

CREATE TABLE pricelist_routes
(
    pricelist_id UUID NOT NULL REFERENCES pricelists (pricelist_id),
    legs_id      UUID NOT NULL REFERENCES legs (legs_id),
    provider_id  UUID NOT NULL REFERENCES providers (provider_id),
    PRIMARY KEY (pricelist_id, legs_id, provider_id)
);

CREATE TABLE users
(
    user_id    SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL
);

CREATE TABLE reservations
(
    reservation_id    SERIAL PRIMARY KEY,
    user_id           INT NOT NULL REFERENCES users (user_id),
    legs_id           UUID NOT NULL REFERENCES legs (legs_id),
    provider_id       UUID NOT NULL REFERENCES providers (provider_id),
    total_price       NUMERIC(10, 2) NOT NULL,
    total_travel_time INTERVAL NOT NULL,
    reservation_time  TIMESTAMP NOT NULL
);
