INSERT INTO users (
    id,
    username,
    email,
    password,
    first_name,
    last_name,
    status,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    failed_login_attempts,
    deleted,
    created_at
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'admin',
    'admin@banking.local',
    '$2y$10$tO2NaeANRJxqiZEgXiASNehX8ps9Z/baGi5XRdTgeilF/oXAbG5ju',
    'Default',
    'Admin',
    'ACTIVE',
    true,
    true,
    true,
    true,
    0,
    false,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (id, user_id, role_id)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '22222222-2222-2222-2222-222222222222'
)
ON CONFLICT (user_id, role_id) DO NOTHING;
