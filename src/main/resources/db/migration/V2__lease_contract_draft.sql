CREATE TABLE contract.customer_contract_draft(

    contract_draft_id UUID primary key,
    start_date DATE NULL,                      -- Start Date
    end_date DATE NULL,                        -- End Date
    contract_type_id varchar(100),
    contract_code VARCHAR(40),
    CONSTRAINT fk_contract_type
    FOREIGN KEY (contract_type_id) REFERENCES contract.contract_type(contract_type_id)
);

CREATE TABLE contract.customer (
    customer_number VARCHAR(50) primary key,              -- Customer Number
    customer_name VARCHAR(100),           -- Customer Name
    city varchar(100),
    country varchar(100),
    phone_number varchar(100),
    is_lease_contract BOOLEAN,
    contract_draft_id UUID,
    CONSTRAINT fk_customer_contract_draft
        FOREIGN KEY (contract_draft_id) REFERENCES contract.customer_contract_draft(contract_draft_id)
);

CREATE TABLE contract.interest_rate (
    id BIGINT primary key,
    rate FLOAT
);

INSERT INTO contract.interest_rate (id, rate) VALUES (1, 12.00);


