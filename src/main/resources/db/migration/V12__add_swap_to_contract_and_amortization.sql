-- Add swap column to contract_type table
ALTER TABLE contract.contract_type
ADD COLUMN swap_amount DECIMAL NULL;

-- Add swap column to amortization_entry table
ALTER TABLE contract.amortization_entry
ADD COLUMN swap_amount DECIMAL NULL;