create database if not exists `search_line` default character set utf8mb4;

use `search_line`;
drop table if exists s_line_file;
CREATE TABLE s_line_file
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner        BIGINT,
    deleted      INT      DEFAULT 0,
    lock_version INT      DEFAULT 0,
    gmt_create   DATETIME DEFAULT CURRENT_TIMESTAMP,
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    name         VARCHAR(255),
    file_service VARCHAR(255),
    suffix       VARCHAR(255),
    file_size    BIGINT
);


