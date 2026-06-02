CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(100) NOT NULL,
    topic VARCHAR(100) NOT NULL,
    source_service VARCHAR(100) NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id UUID,
    actor_user_id UUID,
    occurred_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payload TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_topic ON audit_logs(topic);
CREATE INDEX IF NOT EXISTS idx_audit_logs_aggregate_type ON audit_logs(aggregate_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_aggregate_id ON audit_logs(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_occurred_at ON audit_logs(occurred_at);
