CREATE TABLE api_keys (
    client_id VARCHAR(255) PRIMARY KEY,
    clave VARCHAR(255) NOT NULL,
    secret_key VARCHAR(255) NOT NULL, -- Clave para firmar
    enabled BOOLEAN DEFAULT true
);