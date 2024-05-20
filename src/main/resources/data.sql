-- Create the Wallet table
CREATE TABLE Wallet (
                        id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
                        balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
                        user_id UUID NOT NULL,
                        currency_code VARCHAR(3) NOT NULL,
                        version BIGINT NOT NULL,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert default data into Wallet
INSERT INTO Wallet (id, balance, user_id, currency_code, version, created_at, updated_at)
VALUES
    ('9f725ab9-77dc-4a0d-ae24-1b0e193ac97c', 100.00, 'a1e425b4-8299-4e30-b6af-5d3eb10dc3fd', 'USD', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d3f2a7b6-97f4-4a72-b4b3-3a0e5e1dba97', 200.00, 'b2f3179d-8a4e-4f21-8e33-0f5de6a87a21', 'EUR', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create the Transaction table
CREATE TABLE Transaction (
                             id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
                             amount DECIMAL(19, 2) NOT NULL,
                             type VARCHAR(50) NOT NULL,  -- Adjust the length based on your needs
                             status VARCHAR(50) NOT NULL,  -- Adjust the length based on your needs
                             paymentId VARCHAR(255),
                             createdAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             updatedAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             wallet_id UUID NOT NULL,
                             CONSTRAINT fk_wallet FOREIGN KEY (wallet_id) REFERENCES Wallet (id)
);