CREATE TABLE `tb_permission_group_permission` (
                                                  `permission_group_id` VARCHAR(36) NOT NULL COMMENT '权限组ID',
                                                  `permission_id` VARCHAR(36) NOT NULL COMMENT '权限ID',
                                                  PRIMARY KEY (`permission_group_id`, `permission_id`),
                                                  CONSTRAINT `fk_permission_group` FOREIGN KEY (`permission_group_id`) REFERENCES `tb_permission_group`(`id`) ON DELETE CASCADE,
                                                  CONSTRAINT `fk_permission` FOREIGN KEY (`permission_id`) REFERENCES `tb_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限组与权限的多对多关系表';
