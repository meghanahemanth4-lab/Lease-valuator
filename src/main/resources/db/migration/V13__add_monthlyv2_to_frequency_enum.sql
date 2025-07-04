-- Add MONTHLY_V2 to frequency enum type
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_enum 
        WHERE enumlabel = 'MONTHLY_V2' 
        AND enumtypid = (SELECT oid FROM pg_type WHERE typname = 'frequency')
    ) THEN
        ALTER TYPE frequency ADD VALUE 'MONTHLY_V2';
    END IF;
END$$;