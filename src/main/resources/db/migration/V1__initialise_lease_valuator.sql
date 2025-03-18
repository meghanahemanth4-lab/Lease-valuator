-- Create schema
CREATE SCHEMA IF NOT EXISTS contract;
CREATE TYPE frequency AS ENUM ('DAILY', 'MONTHLY', 'HALF-YEARLY');
CREATE TYPE vehicle_status AS ENUM ('NEW', 'REFURBISHED');
CREATE TYPE deployment as ENUM ('LEASING', 'UPFRONT', 'EXTERNAL_FINANCE', 'MBT', 'NON_MBT', 'EMPLOYEES');
CREATE TYPE vehicle_type AS ENUM (
    'CHAP_CHAP', 
    'COMMANDO', 
    'EKON', 
    'M_ELECTRIC', 
    'PIAM', 
    'SPIROVEO'
);

CREATE TYPE lease_contract_status as ENUM ('ACTIVE', 'TERMINATED', 'DEFAULTED','SHORT_CLOSE');

CREATE TABLE IF NOT EXISTS contract.contract_type (
    contract_type_id varchar(100) primary key,
    contract_code VARCHAR(40),
    tenure INT,
    interest_rate DECIMAL,
    installment DECIMAL,
    insurance DECIMAL,
    maintenance DECIMAL,
    payment_frequency frequency,
    base_rent DECIMAL NULL,
    deployment deployment,
    vehicle_status vehicle_status,
    type_of_vehicle vehicle_type,
    maintenance_amount DECIMAL NULL,
    insurance_amount DECIMAL NULL,
    downpayment DECIMAL
);

CREATE TABLE IF NOT EXISTS contract.lease_contract (
    contract_no UUID primary key,              -- Contract No.
    customer_id VARCHAR(50),              -- Customer ID
    vehicle_no VARCHAR(20),                        -- Vehicle No.
    customer_name VARCHAR(100),           -- Customer Name
    customer_number VARCHAR(100) unique,            -- Customer Number
    start_date DATE,                      -- Start Date
    end_date DATE NULL,                        -- End Date
    status lease_contract_status,    -- Status (e.g., Active/Pending)
    revenue_pending DECIMAL,                -- Revenue Pending
    value_of_lease_today DECIMAL,         -- Value of Lease Today
    contract_type_id varchar(100),
    vehicle_status vehicle_status,
    city varchar(50),
    country varchar(50),
    number_of_times_redeployed DECIMAL,    --number of times redeployed
    sum_of_pv_of_cf DECIMAL,                    --sum of pv of cash flows
    CONSTRAINT fk_contract_type
    FOREIGN KEY (contract_type_id) REFERENCES contract.contract_type(contract_type_id)
);

CREATE TYPE contract.lease_payment_status AS ENUM (
    'PAID',
    'PENDING',
    'DEFAULTED', 
    'TERMINATED',
    'NO_RENT_DAY',
    'SHORT_CLOSE',
    'NO_RENT_DAY_FUTURE'
);
CREATE TABLE contract.amortization_entry (
    amortization_entry_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period NUMERIC,
    lease_contract UUID NOT NULL,
    date DATE,
    installment NUMERIC,
    insurance DECIMAL NULL,
    maintenance DECIMAL NULL,
    base_rent DECIMAL NULL,
    cumulative_installment NUMERIC,
    closing_balance NUMERIC,
    outstanding_balance NUMERIC,
    pv_of_cf NUMERIC,
    interest_component NUMERIC,
    principal_component NUMERIC,
    cumulative_principal NUMERIC,
    cumulative_interest NUMERIC,
    remaining_amount NUMERIC,
    lease_payment_status contract.lease_payment_status DEFAULT 'PENDING',
    CONSTRAINT fk_lease_contract FOREIGN KEY (lease_contract)
        REFERENCES contract.lease_contract (contract_no)
        ON DELETE CASCADE
);

INSERT INTO contract.contract_type (
    contract_type_id,
    tenure,
    interest_rate,
    installment,
    payment_frequency,
    base_rent,
    deployment,
    vehicle_status,
    type_of_vehicle,
    maintenance_amount,
    insurance_amount,
    downpayment
) VALUES
-- CHAP Contracts
('CHAP_25KDWN_INS_MF_DR', 999, 12, 1278 + 100 + 100, 'DAILY', 1278, 'LEASING', 'NEW', 'CHAP_CHAP', 100, 100, 25000),
('CHAP_22KDWN_INS_MF_DR', 999, 12, 1278 + 100 + 100, 'DAILY', 1278, 'LEASING', 'NEW', 'CHAP_CHAP', 100, 100, 22000),
('CHAP_MAUTO_EMP_POLICY', 999, 12, 1150 + 100 + 100, 'DAILY', 1150, 'LEASING', 'NEW', 'CHAP_CHAP', 100, 100, 0),
('CHAP_15KDWN_INS_MF_DR', 999, 12, 1278 + 100 + 100, 'DAILY', 1278, 'LEASING', 'NEW', 'CHAP_CHAP', 100, 100, 15000),
('CHAP_299DAYS_25KDWN_INS_MF_DR', 299, 12, 3278 + 100 + 100, 'DAILY', 3278, 'LEASING', 'NEW', 'CHAP_CHAP', 100, 100, 25000),
('REPOSSESSED_0-1000KM_CHAP_969DAYS_25KDWN_INS_MF_DR', 969, 12, 1278 + 100 + 100, 'DAILY', 1278, 'LEASING', 'REFURBISHED', 'CHAP_CHAP', 100, 100, 25000),
('REPOSSESSED_0-1000KM_CHAP_269DAYS_25KDWN_INS_MF_DR', 269, 12, 3278 + 100 + 100, 'DAILY', 3278, 'LEASING', 'REFURBISHED', 'CHAP_CHAP', 100, 100, 25000),
('CRISTAL_BIO_CHAP_01', 999, 12, 1278 + 100 + 100, 'DAILY', 1278, 'LEASING', 'NEW', 'CHAP_CHAP', 100, 100, 25000),

-- CMDO (COMMANDO) Contracts
('CMDO_33KDWN_INS_MF_DR', 999, 12, 1628 + 100 + 100, 'DAILY', 1628, 'LEASING', 'NEW', 'COMMANDO', 100, 100, 33000),
('CMDO_MAUTO_EMP_POLICY', 999, 12, 1465 + 100 + 100, 'DAILY', 1465, 'LEASING', 'NEW', 'COMMANDO', 100, 100, 0),
('T-TRANS_CMD_01', 999, 12, 1500 + 100 + 100, 'DAILY', 1500, 'LEASING', 'NEW', 'COMMANDO', 100, 100, 33000),
('CMDO_299DAYS_25KDWN_INS_MF_DR', 299, 12, 4028 + 100 + 100, 'DAILY', 4028, 'LEASING', 'NEW', 'COMMANDO', 100, 100, 25000),
('CMDO_999DAYS_100KDWN_INS_MF_DR', 999, 12, 1628 + 100 + 100, 'DAILY', 1628, 'LEASING', 'NEW', 'COMMANDO', 100, 100, 100000),
('CMDO_599DAYS_75KDWN_INS_MF_DR', 599, 12, 2328 + 100 + 100, 'DAILY', 2328, 'LEASING', 'NEW', 'COMMANDO', 100, 100, 75000),
('REPOSSESSED_0-1000KM_CMDO_999DAYS_33KDWN_INS_MF_DR', 999, 12, 1628 + 100 + 100, 'DAILY', 1628, 'LEASING', 'REFURBISHED', 'COMMANDO', 100, 100, 33000),
('REPOSSESSED_0-1000KM_CMDO_599DAYS_75KDWN_INS_MF_DR', 599, 12, 2328 + 100 + 100, 'DAILY', 2328, 'LEASING', 'REFURBISHED', 'COMMANDO', 100, 100, 75000);



