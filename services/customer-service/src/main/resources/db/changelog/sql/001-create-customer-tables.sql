CREATE TABLE customers (
                           id UUID PRIMARY KEY,

                           user_id UUID NOT NULL UNIQUE,
                           cif VARCHAR(50) NOT NULL UNIQUE,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           date_of_birth DATE,
                           gender VARCHAR(20),
                           email VARCHAR(150) NOT NULL,
                           phone_number VARCHAR(50),
                           status VARCHAR(50) NOT NULL,

                           deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           deleted_at TIMESTAMP,
                           deleted_by UUID,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP
);

CREATE TABLE customer_documents (
                                    id UUID PRIMARY KEY,

                                    customer_id UUID NOT NULL,
                                    document_type VARCHAR(50) NOT NULL,
                                    document_number VARCHAR(100) NOT NULL,
                                    issuing_country VARCHAR(100) NOT NULL,
                                    issue_date DATE,
                                    expiry_date DATE,

                                    deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                    deleted_at TIMESTAMP,
                                    deleted_by UUID,

                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_customer_documents_customer
                                        FOREIGN KEY (customer_id)
                                            REFERENCES customers(id)
);

CREATE TABLE customer_addresses (
                                    id UUID PRIMARY KEY,

                                    customer_id UUID NOT NULL,
                                    address_type VARCHAR(50) NOT NULL,
                                    country VARCHAR(100) NOT NULL,
                                    city VARCHAR(100) NOT NULL,
                                    street VARCHAR(255) NOT NULL,
                                    postal_code VARCHAR(50),
                                    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

                                    deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                    deleted_at TIMESTAMP,
                                    deleted_by UUID,

                                    CONSTRAINT fk_customer_addresses_customer
                                        FOREIGN KEY (customer_id)
                                            REFERENCES customers(id)
);

CREATE INDEX idx_customers_user_id
    ON customers(user_id);

CREATE INDEX idx_customers_email
    ON customers(email);

CREATE INDEX idx_customers_cif
    ON customers(cif);

CREATE INDEX idx_customers_status
    ON customers(status);

CREATE INDEX idx_customers_deleted
    ON customers(deleted);

CREATE INDEX idx_customer_documents_customer_id
    ON customer_documents(customer_id);

CREATE INDEX idx_customer_documents_document_number
    ON customer_documents(document_number);

CREATE INDEX idx_customer_documents_deleted
    ON customer_documents(deleted);

CREATE INDEX idx_customer_addresses_customer_id
    ON customer_addresses(customer_id);

CREATE INDEX idx_customer_addresses_deleted
    ON customer_addresses(deleted);
