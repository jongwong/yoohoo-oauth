CREATE TABLE `tb_user_wechat` (
                               `id` VARCHAR(36) NOT NULL COMMENT 'ID',
                               `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
                               `wechat_openid` VARCHAR(255) NOT NULL COMMENT '微信OpenId',
                               `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `idx_user_wechat` (`user_id`, `wechat_openid`),
                               FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与微信账号关联表';
