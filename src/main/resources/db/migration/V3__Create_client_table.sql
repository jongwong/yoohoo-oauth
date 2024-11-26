CREATE TABLE `tb_client` (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           client_id VARCHAR(255) NOT NULL UNIQUE,
                           client_secret VARCHAR(255) NOT NULL,
                           redirect_uri VARCHAR(255) NOT NULL,
                           scope VARCHAR(255),  -- 可选，定义授权的范围
                           status TINYINT(1) DEFAULT 1,  -- 1表示有效，0表示无效
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
