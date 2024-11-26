CREATE TABLE `tb_secret` (
                             `id` INT NOT NULL AUTO_INCREMENT,
                             `secret_id` VARCHAR(255) NOT NULL COMMENT '其他服务的密钥 ID',
                             `secret_key` VARCHAR(255) NOT NULL COMMENT '其他服务的密钥 Key',
                             `secret_name` VARCHAR(255) NOT NULL COMMENT '密钥名称或标识，供识别用途',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储其他服务的密钥信息';
