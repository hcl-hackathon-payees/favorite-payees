-- =========================
-- 1. CUSTOMERS
-- =========================
--INSERT INTO customers (id, name) VALUES (123456, 'Aditya');
--INSERT INTO customers (id, name) VALUES (234567, 'Rahul');

-- =========================
-- 2. BANK MAPPING
-- =========================
INSERT INTO bank_code_mapping (code, bank_name) VALUES ('2134', 'Nairobi Bank');
INSERT INTO bank_code_mapping (code, bank_name) VALUES ('4954', 'Denver Bank');
INSERT INTO bank_code_mapping (code, bank_name) VALUES ('1236', 'Moscow Bank');
INSERT INTO bank_code_mapping (code, bank_name) VALUES ('1237', 'Tokio Bank');
--
---- =========================
---- 3. FAVORITE PAYEES
---- =========================
--INSERT INTO favorite_accounts (customer_id, account_name, iban, bank_name, created_at, updated_at)
--VALUES
--    -- Aditya's accounts
--    (123456, 'Aditya Savings', 'ES50 2134 4954 4443 2222', 'Nairobi Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--
--    -- Rahul's accounts
--    (234567, 'Rahul Office', 'ES50 4954 2134 4443 1111', 'Denver Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--    (234567, 'Rahul Friend', 'ES50 2134 4954 9999 8888', 'Nairobi Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);