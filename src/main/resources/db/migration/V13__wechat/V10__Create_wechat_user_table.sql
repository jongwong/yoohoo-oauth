CREATE TABLE `tb_wechat_user` (
                               `id` VARCHAR(36) NOT NULL COMMENT '记录ID',
                               `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
                               `wechat_openid` VARCHAR(255) NOT NULL COMMENT '微信OpenId',
                               `access_token` VARCHAR(255) DEFAULT NULL COMMENT '微信Access Token',
                               `refresh_token` VARCHAR(255) DEFAULT NULL COMMENT '微信Refresh Token',
                               `expires_in` INT DEFAULT NULL COMMENT 'Access Token过期时间',
                               `union_id` VARCHAR(255) DEFAULT NULL COMMENT '微信UnionID（针对同一微信用户的多个账号唯一标识）',
                               `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
                               UNIQUE KEY `idx_wechat_openid` (`wechat_openid`),
                               UNIQUE KEY `idx_union_id` (`union_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信第三方登录关联表';
