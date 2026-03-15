INSERT INTO roles (id, name, description, is_deleted, created_at, updated_at)
VALUES
    (unhex(replace(uuid(), '-', '')), 'ADMIN', 'Administrator with full access', b'0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (unhex(replace(uuid(), '-', '')), 'CUSTOMER', 'End user for shopping', b'0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);