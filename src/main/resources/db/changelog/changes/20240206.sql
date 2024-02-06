--liquibase formatted sql

--changeset thoi:202402061

CREATE TABLE IF NOT EXISTS users(
    id VARCHAR(100) PRIMARY KEY NOT NULL,
    first_name VARCHAR(200) NOT NULL,
    last_name VARCHAR(200),
    status VARCHAR(20),
    mobile VARCHAR(15),
    email VARCHAR(100),
    created DATETIME NOT NULL,
    updated DATETIME
);

CREATE TABLE IF NOT EXISTS otp (
  id VARCHAR(100) PRIMARY KEY NOT NULL,
  reason VARCHAR(20) NOT NULL,
  mode VARCHAR(20) NOT NULL,
  identifier VARCHAR(100) NOT NULL,
  otp VARCHAR(20) NOT NULL,
  notification_provider VARCHAR(50) NOT NULL,
  notification_id VARCHAR(200) DEFAULT NULL,
  metadata VARCHAR(500),
  status VARCHAR(25) NOT NULL,
  expires_at DATETIME NOT NULL,
  created DATETIME NOT NULL,
  updated DATETIME
);

CREATE TABLE IF NOT EXISTS user_auth (
  id VARCHAR(100) PRIMARY KEY NOT NULL,
  provider VARCHAR(20) NOT NULL,
  ip_user_id VARCHAR(100) NOT NULL,
  user_id VARCHAR(100),
  creator_user_id VARCHAR(100),
  status VARCHAR(20),
  created DATETIME NOT NULL,
  updated DATETIME
);

CREATE TABLE IF NOT EXISTS user_session (
  id VARCHAR(100) PRIMARY KEY NOT NULL,
  session_id VARCHAR(100),
  user_id VARCHAR(100),
  active_until DATETIME,
  created DATETIME NOT NULL,
  updated DATETIME
);