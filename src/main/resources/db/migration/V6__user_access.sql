-- filepath: c:\Users\HP\Documents\sap-lease-valuator-service\src\main\resources\db\migration\V6__user_access.sql
CREATE ROLE read_users;
CREATE ROLE write_users;

GRANT SELECT ON ALL TABLES IN SCHEMA contract TO read_users;
GRANT SELECT, UPDATE, DELETE ON ALL TABLES IN SCHEMA contract TO write_users;

ALTER DEFAULT PRIVILEGES IN SCHEMA contract
GRANT SELECT ON TABLES TO read_users;

ALTER DEFAULT PRIVILEGES IN SCHEMA contract
GRANT SELECT, UPDATE, DELETE ON TABLES TO write_users;