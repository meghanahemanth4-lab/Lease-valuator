CREATE TABLE contract.vehicle_no (
    vehicle_no_id UUID PRIMARY KEY,
    vehicle_no VARCHAR(50) NULL,            
    number_of_times_deployed INT,             
    is_deployed BOOLEAN DEFAULT FALSE
);

ALTER TABLE contract.lease_contract
ADD COLUMN vehicle_no_id UUID;

ALTER TABLE contract.lease_contract
DROP COLUMN vehicle_no;

ALTER TABLE contract.lease_contract
ALTER COLUMN vehicle_no_id DROP NOT NULL;

ALTER TABLE contract.lease_contract
ADD CONSTRAINT fk_vehicle_no
FOREIGN KEY (vehicle_no_id)
REFERENCES contract.vehicle_no(vehicle_no_id) 
ON DELETE SET NULL;
