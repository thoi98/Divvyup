--liquibase formatted sql

--changeset thoi:0

CREATE TABLE IF NOT EXISTS test(
    id VARCHAR(100) PRIMARY KEY NOT NULL,
    details VARCHAR(500) DEFAULT NULL,
    created DATETIME NOT NULL,
    updated DATETIME DEFAULT NULL
);