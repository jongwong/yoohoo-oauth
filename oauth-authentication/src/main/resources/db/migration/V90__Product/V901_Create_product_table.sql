CREATE TABLE `tb_products` (
                            `id` CHAR(36) PRIMARY KEY,                        -- 商品ID，使用UUID
                            `name` VARCHAR(255) NOT NULL,                     -- 商品名称
                            `description` TEXT,                              -- 商品描述
                            `short_description` VARCHAR(255),                 -- 商品简短描述
                            `price` DECIMAL(10, 2) NOT NULL,                  -- 商品价格
                            `cost_price` DECIMAL(10, 2),                      -- 商品成本价格
                            `sku` VARCHAR(50) UNIQUE,                         -- 商品的库存单位（SKU），唯一
                            `barcode` VARCHAR(50) DEFAULT NULL,               -- 商品条形码（可选字段）
                            `category_id` INT,                                -- 商品分类ID（外键）
                            `brand_id` INT,                                   -- 商品品牌ID（外键）
                            `status` TINYINT(1) DEFAULT 1,                    -- 商品状态（1=可用，2=不可用，3=停售）
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 商品创建时间
                            `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 商品更新时间
                            `meta_title` VARCHAR(255),                        -- SEO优化的标题
                            `meta_description` TEXT,                          -- SEO优化的描述
                            `meta_keywords` TEXT,                             -- SEO优化的关键词
                            `archived_status` TINYINT(2) DEFAULT 10,          -- 建档状态（10=草稿，20=审核中，30=审核拒绝，40=建档完成）
                            `listed_status` TINYINT(1) DEFAULT 0              -- 上架状态（0=未上架，1=已上架）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
