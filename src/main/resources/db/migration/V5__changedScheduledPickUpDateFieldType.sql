BEGIN;
ALTER TABLE orders
    ALTER COLUMN scheduled_pickup_date TYPE date
        USING scheduled_pickup_date::date;
COMMIT;