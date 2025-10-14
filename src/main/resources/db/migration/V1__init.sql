CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,

                       full_name VARCHAR(255),
                       company_name VARCHAR(255),
                       phone VARCHAR(50),
                       address TEXT,

                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_role CHECK (role IN ('CLIENT', 'ADMIN', 'DRIVER'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);


CREATE TABLE drivers (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,

                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         license_number VARCHAR(100) UNIQUE NOT NULL,
                         phone VARCHAR(50) NOT NULL,

                         status VARCHAR(50) DEFAULT 'AVAILABLE',
                         hire_date DATE,

                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT chk_driver_status CHECK (status IN ('AVAILABLE', 'BUSY', 'ON_TRIP', 'OFF_DUTY'))
);

CREATE INDEX idx_drivers_user_id ON drivers(user_id);
CREATE INDEX idx_drivers_status ON drivers(status);
CREATE INDEX idx_drivers_license ON drivers(license_number);



CREATE TABLE vehicles (
                          id BIGSERIAL PRIMARY KEY,
                          license_plate VARCHAR(50) UNIQUE NOT NULL,
                          trailer_type VARCHAR(100) NOT NULL,
                          capacity_kg DECIMAL(10,2) NOT NULL,

                          status VARCHAR(50) DEFAULT 'AVAILABLE',

                          manufacture_year INT,
                          last_maintenance_date DATE,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT chk_vehicle_status CHECK (status IN ('AVAILABLE', 'IN_USE', 'MAINTENANCE', 'OUT_OF_SERVICE')),
                          CONSTRAINT chk_trailer_type CHECK (trailer_type IN ('TANKER', 'FLATBED', 'REFRIGERATED', 'BOX', 'BULK_CARRIER'))
);

CREATE INDEX idx_vehicles_license_plate ON vehicles(license_plate);
CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_vehicles_trailer_type ON vehicles(trailer_type);



CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,

                        client_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

                        cargo_type VARCHAR(100) NOT NULL,
                        cargo_weight_kg DECIMAL(10,2),
                        cargo_description TEXT,
                        trailer_type VARCHAR(100) NOT NULL,

                        origin_address TEXT NOT NULL,
                        origin_latitude DECIMAL(10,8),
                        origin_longitude DECIMAL(11,8),

                        destination_address TEXT NOT NULL,
                        destination_latitude DECIMAL(10,8),
                        destination_longitude DECIMAL(11,8),

                        distance_km DECIMAL(10,2),
                        estimated_duration_minutes INT,
                        cost DECIMAL(10,2) NOT NULL,

                        status VARCHAR(50) DEFAULT 'PENDING',

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        scheduled_pickup_date TIMESTAMP,

                        CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_TRANSIT', 'COMPLETED', 'CANCELLED')),
                        CONSTRAINT chk_cargo_type CHECK (cargo_type IN ('LIQUID', 'SOLID', 'BULK', 'FRAGILE', 'PERISHABLE')),
                        CONSTRAINT chk_cost CHECK (cost >= 0)
);

CREATE INDEX idx_orders_client_id ON orders(client_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_cargo_type ON orders(cargo_type);


CREATE TABLE trips (
                       id BIGSERIAL PRIMARY KEY,

                       order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
                       driver_id BIGINT NOT NULL REFERENCES drivers(id) ON DELETE RESTRICT,
                       vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,

                       assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       started_at TIMESTAMP,
                       completed_at TIMESTAMP,

                       actual_distance_km DECIMAL(10,2),
                       delivery_time_minutes INT,

                       rating DECIMAL(3,2),
                       client_comment TEXT,

                       status VARCHAR(50) DEFAULT 'ASSIGNED',
                       notes TEXT,

                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_trip_status CHECK (status IN ('ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
                       CONSTRAINT chk_trip_dates CHECK (
                           (started_at IS NULL OR started_at >= assigned_at) AND
                           (completed_at IS NULL OR completed_at >= started_at)
                           ),
                       CONSTRAINT chk_rating CHECK (rating IS NULL OR (rating >= 0 AND rating <= 5))
);

CREATE INDEX idx_trips_order_id ON trips(order_id);
CREATE INDEX idx_trips_driver_id ON trips(driver_id);
CREATE INDEX idx_trips_vehicle_id ON trips(vehicle_id);
CREATE INDEX idx_trips_status ON trips(status);
CREATE INDEX idx_trips_completed_at ON trips(completed_at);


CREATE TABLE order_status_history (
                                      id BIGSERIAL PRIMARY KEY,
                                      order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                                      old_status VARCHAR(50),
                                      new_status VARCHAR(50) NOT NULL,
                                      changed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                      changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      comment TEXT
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_changed_at ON order_status_history(changed_at);


CREATE TABLE analytics_snapshots (
                                     id BIGSERIAL PRIMARY KEY,
                                     snapshot_date DATE NOT NULL,
                                     snapshot_type VARCHAR(100) NOT NULL,
                                     metrics JSONB NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT unique_snapshot UNIQUE (snapshot_date, snapshot_type),
                                     CONSTRAINT chk_snapshot_type CHECK (
                                         snapshot_type IN (
                                                           'monthly_revenue',
                                                           'driver_kpi_monthly',
                                                           'delivery_time_trend',
                                                           'fleet_utilization_monthly',
                                                           'top_clients_monthly'
                                             )
                                         )
);

CREATE INDEX idx_snapshots_date_type ON analytics_snapshots(snapshot_date, snapshot_type);
CREATE INDEX idx_snapshots_type ON analytics_snapshots(snapshot_type);
CREATE INDEX idx_snapshots_date ON analytics_snapshots(snapshot_date DESC);
CREATE INDEX idx_snapshots_metrics ON analytics_snapshots USING GIN (metrics);