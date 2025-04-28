CREATE TABLE IF NOT EXISTS contract.rent_payment (
    id SERIAL PRIMARY KEY, -- Auto-incrementing primary key
    customer_number VARCHAR(100) NOT NULL, -- Customer number
    contract_type_id VARCHAR(100) NOT NULL, -- Foreign key to contract_type
    date DATE NOT NULL, -- Payment date
    payment_amount NUMERIC NOT NULL, -- Payment amount
    transaction_date DATE NOT NULL, -- Transaction date
    CONSTRAINT fk_contract_type FOREIGN KEY (contract_type_id) REFERENCES contract.contract_type (contract_type_id),
    CONSTRAINT fk_customer_number FOREIGN KEY (customer_number) REFERENCES contract.lease_contract (customer_number)
);

CREATE INDEX IF NOT EXISTS rent_payment_date ON contract.rent_payment (date);
CREATE INDEX IF NOT EXISTS rent_payment_transaction_date ON contract.rent_payment (transaction_date);

ALTER TABLE contract.amortization_entry
ADD COLUMN transaction_date DATE;

CREATE INDEX idx_transaction_date ON contract.amortization_entry (transaction_date);