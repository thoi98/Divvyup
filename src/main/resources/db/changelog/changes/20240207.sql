--liquibase formatted sql

--changeset thoi:202402071

CREATE TABLE IF NOT EXISTS `groups`(
    id VARCHAR(100) PRIMARY KEY NOT NULL,
    name VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL,
    metadata BLOB,
    created DATETIME NOT NULL,
    updated DATETIME
);

CREATE TABLE IF NOT EXISTS user_groups(
    id VARCHAR(100) PRIMARY KEY NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    group_id VARCHAR(100) NOT NULL,
    created DATETIME NOT NULL,
    updated DATETIME
);