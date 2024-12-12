CREATE TABLE `tb_role_permission_group` (
                                         `id` VARCHAR(36) NOT NULL COMMENT '关联ID',
                                         `role_id` VARCHAR(36) NOT NULL COMMENT '角色ID',
                                         `permission_group_id` VARCHAR(36) NOT NULL COMMENT '权限组ID',
                                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`),
                                         FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE,
                                         FOREIGN KEY (`permission_group_id`) REFERENCES `tb_permission_group` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与权限组关联表';
