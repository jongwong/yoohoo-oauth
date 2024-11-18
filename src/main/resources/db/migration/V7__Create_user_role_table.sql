CREATE TABLE `tb_user_role` (
                             `id` VARCHAR(36) NOT NULL COMMENT '关联ID',
                             `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
                             `role_id` VARCHAR(36) NOT NULL COMMENT '角色ID',
                             `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             PRIMARY KEY (`id`),
                             FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
                             FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';
