CREATE TABLE IF NOT EXISTS pollee
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    service_url VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_checked_at TIMESTAMP WITH TIME ZONE
);