CREATE TABLE `tb_role_role_group` (
                                   `id` VARCHAR(36) NOT NULL COMMENT '关联ID',
                                   `role_id` VARCHAR(36) NOT NULL COMMENT '角色ID',
                                   `role_group_id` VARCHAR(36) NOT NULL COMMENT '角色组ID',
                                   `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE,
                                   FOREIGN KEY (`role_group_id`) REFERENCES `tb_role_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与角色组关联表';
