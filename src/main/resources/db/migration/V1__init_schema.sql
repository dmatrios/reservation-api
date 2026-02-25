-- V1: Initial schema for Reservation API
-- MySQL 8.0 / InnoDB / utf8mb4

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(120) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  dni VARCHAR(15) NOT NULL,
  phone VARCHAR(30) NOT NULL,
  role VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE rooms (
  id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(40) NOT NULL,
  capacity INT NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT pk_rooms PRIMARY KEY (id),
  CONSTRAINT uk_rooms_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE service_slot_config (
  id BIGINT NOT NULL AUTO_INCREMENT,
  service_type VARCHAR(20) NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  capacity INT NOT NULL,
  active BIT(1) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT pk_service_slot_config PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE reservations (
  id BIGINT NOT NULL AUTO_INCREMENT,

  user_id BIGINT NOT NULL,

  dni_snapshot VARCHAR(15) NOT NULL,
  phone_snapshot VARCHAR(30) NOT NULL,
  email_snapshot VARCHAR(120) NOT NULL,
  full_name_snapshot VARCHAR(120) NOT NULL,

  party_size INT NOT NULL,
  note VARCHAR(500),

  service_type VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,

  deleted_at TIMESTAMP(6) NULL,

  room_id BIGINT NULL,
  check_in_date DATE NULL,
  check_out_date DATE NULL,

  slot_config_id BIGINT NULL,
  reservation_date DATE NULL,

  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  CONSTRAINT pk_reservations PRIMARY KEY (id),

  CONSTRAINT fk_reservations_user
    FOREIGN KEY (user_id) REFERENCES users(id),

  CONSTRAINT fk_reservations_room
    FOREIGN KEY (room_id) REFERENCES rooms(id),

  CONSTRAINT fk_reservations_slot_config
    FOREIGN KEY (slot_config_id) REFERENCES service_slot_config(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Indexes for performance (core rules later use these)
-- HOTEL overlap checks: filter by room + dates + active (status + deleted_at)
CREATE INDEX idx_res_room_dates_active
  ON reservations (room_id, check_in_date, check_out_date, status, deleted_at);

-- Slot capacity checks: filter by slot + date + active
CREATE INDEX idx_res_slot_date_active
  ON reservations (slot_config_id, reservation_date, status, deleted_at);

-- Ownership queries (my reservations, admin filters)
CREATE INDEX idx_res_user_active
  ON reservations (user_id, status, deleted_at);

-- Optional: filter by service type quickly
CREATE INDEX idx_res_service_type
  ON reservations (service_type);