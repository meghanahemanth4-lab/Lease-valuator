-- Drop existing constraints and indexes that might reference the old columns
ALTER TABLE IF EXISTS contract.users 
    DROP CONSTRAINT IF EXISTS users_username_key,
    DROP COLUMN IF EXISTS username,
    DROP COLUMN IF EXISTS full_name;

-- Add the missing columns
ALTER TABLE contract.users
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(50) NOT NULL DEFAULT 'User',
    ADD COLUMN IF NOT EXISTS last_name VARCHAR(50) NOT NULL DEFAULT 'User';

-- Add comments for the new columns
COMMENT ON COLUMN contract.users.first_name IS 'User''s first name';
COMMENT ON COLUMN contract.users.last_name IS 'User''s last name';

-- Update the existing comments to match the new structure
COMMENT ON TABLE contract.users IS 'Stores user account information';
COMMENT ON COLUMN contract.users.password IS 'Hashed password';
COMMENT ON COLUMN contract.users.email IS 'User''s email address';
COMMENT ON COLUMN contract.users.company_name IS 'User''s company name';
