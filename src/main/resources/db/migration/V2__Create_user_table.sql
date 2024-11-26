CREATE TABLE `tb_user` (
                        `id` VARCHAR(36) NOT NULL COMMENT '用户ID',
                        `username` VARCHAR(255) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(255) NOT NULL COMMENT '用户密码',
                        `mobile_phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                        `e_mail` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
                        `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
                        `expired` TINYINT(1) DEFAULT 0 COMMENT '是否过期',
                        `locked` TINYINT(1) DEFAULT 0 COMMENT '是否锁定',
                        `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
                        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
