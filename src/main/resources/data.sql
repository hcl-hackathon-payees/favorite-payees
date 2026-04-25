-- Customers
INSERT INTO customers (id, name) VALUES (1, 'Aditya');
INSERT INTO customers (id, name) VALUES (2, 'Rahul');

-- Bank mapping
INSERT INTO bank_code_mapping (code, bank_name) VALUES ('2134', 'Nairobi Bank');
INSERT INTO bank_code_mapping (code, bank_name) VALUES ('4954', 'Denver Bank');

-- Favorite accounts
INSERT INTO favorite_accounts (id, customer_id, account_name, iban, bank_name, created_at, updated_at)
VALUES
    (1, 1, 'My Savings', 'ES50 2134 4954 4443 2222', 'Nairobi Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (2, 1, 'Office Account', 'ES50 4954 2134 4443 1111', 'Denver Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (3, 2, 'Friend Transfer', 'ES50 2134 4954 9999 8888', 'Nairobi Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);