CREATE TABLE contract.downpayments (
    id UUID PRIMARY KEY,
    customer_number VARCHAR(40),
    customer_code VARCHAR(40),
    contract_type_id VARCHAR(100),
    payment_amount DECIMAL,
    transaction_date DATE,
    vehicle_type vehicle_type,  
    deployment deployment  
);