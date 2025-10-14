
-- Function to log order status changes
CREATE OR REPLACE FUNCTION log_order_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.status IS DISTINCT FROM NEW.status) THEN
        INSERT INTO order_status_history (order_id, old_status, new_status, changed_at)
        VALUES (NEW.id, OLD.status, NEW.status, CURRENT_TIMESTAMP);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER track_order_status_changes
    AFTER UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION log_order_status_change();


-- 2. TRIGGER FOR AUTO-SYNC ORDER STATUS WITH TRIP STATUS

-- Function to sync order status when trip status changes
CREATE OR REPLACE FUNCTION sync_order_status_from_trip()
RETURNS TRIGGER AS $$
BEGIN
    -- Trip started → Order in transit
    IF NEW.status = 'IN_PROGRESS' AND OLD.status != 'IN_PROGRESS' THEN
UPDATE orders SET status = 'IN_TRANSIT' WHERE id = NEW.order_id;

-- Trip completed → Order completed
ELSIF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
UPDATE orders SET status = 'COMPLETED' WHERE id = NEW.order_id;

-- Trip cancelled → Order cancelled
ELSIF NEW.status = 'CANCELLED' AND OLD.status != 'CANCELLED' THEN
UPDATE orders SET status = 'CANCELLED' WHERE id = NEW.order_id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER sync_order_status_on_trip_change
    AFTER UPDATE ON trips
    FOR EACH ROW
    WHEN (OLD.status IS DISTINCT FROM NEW.status)
    EXECUTE FUNCTION sync_order_status_from_trip();

-- 3. UNIQUE CONSTRAINTS FOR ACTIVE TRIPS

CREATE UNIQUE INDEX idx_trips_driver_active
    ON trips(driver_id)
    WHERE status IN ('ASSIGNED', 'IN_PROGRESS');

CREATE UNIQUE INDEX idx_trips_vehicle_active
    ON trips(vehicle_id)
    WHERE status IN ('ASSIGNED', 'IN_PROGRESS');



CREATE OR REPLACE FUNCTION create_monthly_revenue_snapshot(target_year INT, target_month INT)
    RETURNS void AS $$
DECLARE
    snapshot_data JSONB;
    target_date DATE;
BEGIN
    target_date := make_date(target_year, target_month, 1);

    SELECT jsonb_build_object(
                   'year', target_year,
                   'month', target_month,
                   'total_orders', COUNT(*),
                   'completed_orders', COUNT(*) FILTER (WHERE status = 'COMPLETED'),
                   'cancelled_orders', COUNT(*) FILTER (WHERE status = 'CANCELLED'),
                   'total_revenue', COALESCE(SUM(cost) FILTER (WHERE status = 'COMPLETED'), 0),
                   'avg_order_value', COALESCE(AVG(cost) FILTER (WHERE status = 'COMPLETED'), 0),
                   'total_distance_km', COALESCE(SUM(distance_km) FILTER (WHERE status = 'COMPLETED'), 0)
           ) INTO snapshot_data
    FROM orders
    WHERE EXTRACT(YEAR FROM created_at) = target_year
      AND EXTRACT(MONTH FROM created_at) = target_month;

    INSERT INTO analytics_snapshots (snapshot_date, snapshot_type, metrics)
    VALUES (target_date, 'monthly_revenue', snapshot_data)
    ON CONFLICT (snapshot_date, snapshot_type)
        DO UPDATE SET metrics = EXCLUDED.metrics, created_at = CURRENT_TIMESTAMP;

    RAISE NOTICE 'Monthly revenue snapshot created for %-% ', target_year, target_month;
END;
$$ LANGUAGE plpgsql;