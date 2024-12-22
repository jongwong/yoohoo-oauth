CREATE TABLE `tb_product_images` (
                                     `id` char(36) NOT NULL COMMENT '主键ID，UUID格式',
                                     `product_id` char(36) NOT NULL COMMENT '关联商品ID，UUID格式',
                                     `name` varchar(100) DEFAULT NULL COMMENT '图片名称（不超过100个字符）',
                                     `url` varchar(2083) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片访问的URL地址，符合URL最大长度标准',
                                     `image_type` tinyint UNSIGNED NOT NULL COMMENT '图片类型：1=主图，2=缩略图，3=轮播图，4=其他图片',
                                     `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                     `created_by` char(36) NOT NULL COMMENT '记录创建人ID，UUID格式',
                                     `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
                                     `updated_by` char(36) DEFAULT NULL COMMENT '记录最后更新人ID，UUID格式',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片表，用于存储商品的图片信息';
