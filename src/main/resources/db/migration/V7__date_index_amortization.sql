CREATE INDEX idx_amortization_entry_date
ON contract.amortization_entry (date);

CREATE INDEX idx_lease_contract_type_customer
ON contract.lease_contract (contract_type_id, customer_number);