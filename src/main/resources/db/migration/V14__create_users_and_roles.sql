-- Set the schema
SET search_path TO contract;

-- Create enum type for user roles
CREATE TYPE contract.user_role AS ENUM (
    'ROLE_SUPER_ADMIN',
    'ROLE_DEPUTY_ADMIN',
    'ROLE_CONTRACT_TYPE_CREATOR',
    'ROLE_LEASE_CREATOR',
    'ROLE_DEPLOYER'
);

-- Create users table
CREATE TABLE IF NOT EXISTS contract.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles join table
CREATE TABLE IF NOT EXISTS contract.user_roles (
    user_id BIGINT NOT NULL,
    role contract.user_role NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES contract.users (id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON contract.users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON contract.users (email);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON contract.user_roles (user_id);

-- Add comments for documentation
COMMENT ON TABLE contract.users IS 'Stores user account information';
COMMENT ON COLUMN contract.users.username IS 'Unique username for login';
COMMENT ON COLUMN contract.users.password IS 'Hashed password';
COMMENT ON COLUMN contract.users.full_name IS 'User''s full name';
COMMENT ON COLUMN contract.users.email IS 'User''s email address';

COMMENT ON TABLE contract.user_roles IS 'Maps users to their roles';
COMMENT ON COLUMN contract.user_roles.role IS 'Role assigned to the user';

-- Add audit triggers for tracking changes
CREATE OR REPLACE FUNCTION contract.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
BEFORE UPDATE ON contract.users
FOR EACH ROW
EXECUTE FUNCTION contract.update_updated_at_column();
