CREATE TABLE `tb_product_images` (
                                  `id` CHAR(36) PRIMARY KEY,                       -- 图片ID，使用UUID
                                  `product_id` CHAR(36),                           -- 商品ID（外键，关联 `products`）
                                  `image_url` VARCHAR(255) NOT NULL,                -- 图片URL
                                  `image_type` ENUM('main', 'gallery', 'thumbnail') NOT NULL,  -- 图片类型（'main' = 主图，'gallery' = 其他图，'thumbnail' = 缩略图）
                                  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 图片上传时间
                                  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 图片更新时间
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
