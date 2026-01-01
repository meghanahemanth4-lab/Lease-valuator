-- Add company_name column to users table
ALTER TABLE contract.users
ADD COLUMN IF NOT EXISTS company_name VARCHAR(100) NOT NULL DEFAULT 'Default Company';

-- Update the comment for the column
COMMENT ON COLUMN contract.users.company_name IS 'User''s company name';
