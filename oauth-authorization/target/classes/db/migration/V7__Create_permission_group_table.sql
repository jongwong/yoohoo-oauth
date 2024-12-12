CREATE TABLE `tb_permission_group` (
                                       `id` VARCHAR(36) NOT NULL COMMENT '权限组ID',
                                       `name` VARCHAR(255) NOT NULL COMMENT '权限组名称',
                                       `description` TEXT COMMENT '权限组描述',
                                       `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限组表';
