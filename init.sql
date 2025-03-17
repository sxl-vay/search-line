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


create table file_transfer_record
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    deleted      INT      DEFAULT 0,
    lock_version INT      DEFAULT 0,
    gmt_create   DATETIME DEFAULT CURRENT_TIMESTAMP,
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    file_id      BIGINT,
    UNIQUE (file_id)
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



CREATE TABLE `dead_letter_record`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `message_id`    varchar(255) NOT NULL,
    `message_body`  text,
    `topic`         varchar(255),
    `retry_times`   int,
    `error_message` text,
    `create_time`   datetime,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 评论主题表
CREATE TABLE IF NOT EXISTS `comment_subject`
(
    `id`           bigint      NOT NULL COMMENT '主键',
    `obj_id`       varchar(64) NOT NULL COMMENT '评论对象ID',
    `user_id`      bigint      NOT NULL COMMENT '用户ID',
    `count`        int         NOT NULL DEFAULT 0 COMMENT '评论总数',
    `root_count`   int         NOT NULL DEFAULT 0 COMMENT '根评论数',
    `lock_version` int         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted`      tinyint     NOT NULL DEFAULT 0 COMMENT '是否删除',
    `gmt_create`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_obj_id` (`obj_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='评论主题表';

-- 评论索引表
CREATE TABLE IF NOT EXISTS `comment_index`
(
    `id`           bigint      NOT NULL COMMENT '主键',
    `obj_id`       varchar(64) NOT NULL COMMENT '评论对象ID',
    `user_id`      bigint      NOT NULL COMMENT '用户ID',
    `root_id`      bigint      NOT NULL DEFAULT 0 COMMENT '根评论ID',
    `parent_id`    bigint      NOT NULL DEFAULT 0 COMMENT '父评论ID',
    `like_count`   int         NOT NULL DEFAULT 0 COMMENT '点赞数',
    `lock_version` int         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted`      tinyint     NOT NULL DEFAULT 0 COMMENT '是否删除',
    `gmt_create`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_obj_id` (`obj_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_root_id` (`root_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='评论索引表';

-- 评论内容表
CREATE TABLE IF NOT EXISTS `comment_content`
(
    `id`               bigint   NOT NULL COMMENT '主键',
    `comment_index_id` bigint   NOT NULL COMMENT '评论索引ID',
    `content`          text     NOT NULL COMMENT '评论内容',
    `deleted`          tinyint  NOT NULL DEFAULT 0 COMMENT '是否删除',
    `gmt_create`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_comment_index_id` (`comment_index_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='评论内容表';

-- 用户点赞表
CREATE TABLE IF NOT EXISTS `user_like`
(
    `id`           bigint   NOT NULL COMMENT '主键',
    `user_id`      bigint   NOT NULL COMMENT '用户ID',
    `comment_id`   bigint   NOT NULL COMMENT '评论ID',
    `deleted`      tinyint  NOT NULL DEFAULT 0 COMMENT '是否删除',
    `gmt_create`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户点赞表';


