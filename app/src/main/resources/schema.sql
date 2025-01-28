DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

create TABLE urls (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMP
);
CREATE TABLE url_checks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url_id BIGINT NOT NULL,
    status_code INT,
    h1 TEXT,
    title TEXT,
    description TEXT,
    created_at TIMESTAMP,
    FOREIGN KEY (url_id) REFERENCES urls(id)
);
