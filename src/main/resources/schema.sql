CREATE TABLE IF NOT EXISTS accident_report (
    report_no VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    accident_at TIMESTAMP NOT NULL,
    accident_description TEXT NOT NULL,
    damage_details TEXT NOT NULL,
    accident_type VARCHAR(50) NOT NULL,
    accident_status VARCHAR(50) NOT NULL,
    accident_report_document_name VARCHAR(255),
    medical_certificate_file_name VARCHAR(255),
    claim_document_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS accident_at TIMESTAMP;
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS accident_type VARCHAR(50);
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS accident_report_document_name VARCHAR(255);
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS medical_certificate_file_name VARCHAR(255);
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS claim_document_name VARCHAR(255);
ALTER TABLE accident_report ALTER COLUMN accident_status SET DEFAULT 'RECEIVED';

UPDATE accident_report
SET accident_type = accident_status
WHERE accident_type IS NULL
  AND accident_status IN ('VEHICLE', 'PROPERTY', 'PERSONAL_INJURY', 'NATURAL_DISASTER');

UPDATE accident_report
SET accident_status = 'RECEIVED'
WHERE accident_status IS NULL
   OR accident_status IN ('VEHICLE', 'PROPERTY', 'PERSONAL_INJURY', 'NATURAL_DISASTER');
