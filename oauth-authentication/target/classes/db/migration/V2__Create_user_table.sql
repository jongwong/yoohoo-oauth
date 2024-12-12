SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
                           `id` varchar(36) NOT NULL COMMENT '用户ID',
                           `username` varchar(100) NOT NULL COMMENT '用户名',
                           `name` varchar(100) NOT NULL COMMENT '姓名',
                           `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
                           `password` varchar(255) NOT NULL COMMENT '用户密码',
                           `mobile` varchar(15) DEFAULT NULL COMMENT '手机号',
                           `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
                           `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
                           `expired` tinyint(1) DEFAULT 0 COMMENT '是否过期：0=未过期, 1=已过期',
                           `locked` tinyint(1) DEFAULT 0 COMMENT '是否锁定：0=未锁定, 1=已锁定',
                           `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用：0=禁用, 1=启用',
                           `authorities` VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER' COMMENT '用户角色, 多个角色用逗号分隔，例如ROLE_USER,ROLE_ADMIN',
                            `last_login` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次登录时间',
                           `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `username` (`username`),
                           UNIQUE KEY `username` (`email`),
                           UNIQUE KEY `mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

SET FOREIGN_KEY_CHECKS = 1;
