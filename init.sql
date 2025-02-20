-- 创建数据库

drop database if exists search_line;

create database if not exists `search_line` default character set utf8mb4;
-- 使用数据库
use `search_line`;
-- 创建文件表
CREATE TABLE s_line_file
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner        BIGINT,
    deleted      INT      DEFAULT 0,
    lock_version INT      DEFAULT 0,
    gmt_create   DATETIME DEFAULT CURRENT_TIMESTAMP,
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    name         VARCHAR(255),
    store_type   VARCHAR(255),
    store_path   text,
    suffix       VARCHAR(255),
    file_size    BIGINT
);

-- 用户信息表
CREATE TABLE `users`
(
    `id`                bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID（自增主键）',
    `gmt_create`        datetime        NOT NULL COMMENT '创建时间',
    `gmt_modified`      datetime        NOT NULL COMMENT '最后更新时间',
    `nick_name`         varchar(255)                       DEFAULT NULL COMMENT '用户昵称',
    `password_hash`     varchar(255)                       DEFAULT NULL COMMENT '密码哈希',
    `state`             varchar(64)                        DEFAULT NULL COMMENT '用户状态（ACTIVE，FROZEN）',
    `invite_code`       varchar(255)                       DEFAULT NULL COMMENT '邀请码',
    `telephone`         varchar(20)                        DEFAULT NULL COMMENT '手机号码',
    `inviter_id`        varchar(255)                       DEFAULT NULL COMMENT '邀请人用户ID',
    `last_login_time`   datetime                           DEFAULT NULL COMMENT '最后登录时间',
    `profile_photo_url` varchar(255)                       DEFAULT NULL COMMENT '用户头像URL',
    `certification`     tinyint(1)                         DEFAULT NULL COMMENT '实名认证状态（TRUE或FALSE）',
    `real_name`         varchar(255)                       DEFAULT NULL COMMENT '真实姓名',
    `id_card_no`        varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '身份证no',
    `user_role`         varchar(128)                       DEFAULT NULL COMMENT '用户角色',
    `deleted`           int                                DEFAULT NULL COMMENT '是否逻辑删除，0为未删除，非0为已删除',
    `lock_version`      int                                DEFAULT NULL COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户信息表'
;

INSERT INTO `users` (`id`, `gmt_create`, `gmt_modified`, `nick_name`, `password_hash`, `state`, `invite_code`,
                     `telephone`, `inviter_id`, `last_login_time`, `profile_photo_url`, `certification`, `real_name`,
                     `id_card_no`, `user_role`, `deleted`,
                     `lock_version`)
VALUES (29, '2024-05-26 12:07:38', '2024-06-10 14:14:20', '藏家_zH9sQA0bob1', 'e7beea81b7a03b38508428fbeeb3c69a',
        'ACTIVE', null, '18000000000', null, null,
        'https://nfturbo-file.oss-cn-hangzhou.aliyuncs.com/profile/29/O1CN014qjUuW1IKL1Ur3fGI_!!2213143710874.jpg_Q75.jpg_.avif',
        1, '446ad47811888a04c6610741aff349c1',
        '670c02c9ce418d783fad1622c007ace8ac5f47acb1a393455f794d541f80d58c', 'CUSTOMER', 0, 10);





