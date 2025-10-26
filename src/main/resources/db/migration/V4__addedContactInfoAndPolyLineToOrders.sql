-- ROUTE
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'route_polyline'
    ) THEN
        ALTER TABLE orders ADD COLUMN route_polyline TEXT;
    END IF;
END
$$;

-- SENDER CONTACTS
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'sender_full_name'
    ) THEN
        ALTER TABLE orders ADD COLUMN sender_full_name VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'sender_phone_number'
    ) THEN
        ALTER TABLE orders ADD COLUMN sender_phone_number VARCHAR(50);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'sender_email'
    ) THEN
        ALTER TABLE orders ADD COLUMN sender_email VARCHAR(255);
    END IF;
END
$$;

-- RECEIVER CONTACTS
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'receiver_full_name'
    ) THEN
        ALTER TABLE orders ADD COLUMN receiver_full_name VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'receiver_phone_number'
    ) THEN
        ALTER TABLE orders ADD COLUMN receiver_phone_number VARCHAR(50);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'receiver_email'
    ) THEN
        ALTER TABLE orders ADD COLUMN receiver_email VARCHAR(255);
    END IF;
END
$$;

-- Заповнюємо ті, що Null значення дефолтними значеннями (тільки якщо колонка існує)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'orders' AND column_name = 'sender_full_name') THEN
        UPDATE orders SET sender_full_name = 'N/A' WHERE sender_full_name IS NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'orders' AND column_name = 'sender_phone_number') THEN
        UPDATE orders SET sender_phone_number = '0000000000' WHERE sender_phone_number IS NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'orders' AND column_name = 'receiver_full_name') THEN
        UPDATE orders SET receiver_full_name = 'N/A' WHERE receiver_full_name IS NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'orders' AND column_name = 'receiver_phone_number') THEN
        UPDATE orders SET receiver_phone_number = '0000000000' WHERE receiver_phone_number IS NULL;
    END IF;
END
$$;

-- 3. Встановлення обмежень NOT NULL для обов'язкових полів (тільки якщо колонка існує і ще nullable)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'sender_full_name' AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE orders ALTER COLUMN sender_full_name SET NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'sender_phone_number' AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE orders ALTER COLUMN sender_phone_number SET NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'receiver_full_name' AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE orders ALTER COLUMN receiver_full_name SET NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'orders' AND column_name = 'receiver_phone_number' AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE orders ALTER COLUMN receiver_phone_number SET NOT NULL;
    END IF;
END
$$;
