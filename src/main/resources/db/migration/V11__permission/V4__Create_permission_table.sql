CREATE TABLE `tb_permission` (
                              `id` VARCHAR(36) NOT NULL COMMENT '权限ID',
                              `name` VARCHAR(255) NOT NULL COMMENT '权限名称',
                              `type` ENUM('API', 'BUTTON', 'MENU') NOT NULL COMMENT '权限类型: 接口权限, 按钮权限, 菜单权限',
                              `resource` VARCHAR(255) NOT NULL COMMENT '权限对应的资源标识，如API路径或菜单标识',
                              `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `idx_permission_resource` (`resource`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限项表';
