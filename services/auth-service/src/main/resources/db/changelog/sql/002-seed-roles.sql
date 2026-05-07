INSERT INTO roles (id, name, description, deleted, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'CUSTOMER', 'Default customer role', false, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 'ADMIN', 'System administrator role', false, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'SUPPORT', 'Support operator role', false, CURRENT_TIMESTAMP),
    ('44444444-4444-4444-4444-444444444444', 'AUDITOR', 'Audit viewer role', false, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;