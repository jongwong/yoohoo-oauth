-- 插入初始用户数据
INSERT INTO tb_user (id, username, password, mobile_phone, e_mail, avatar, expired, locked, enabled, created_at, updated_at) VALUES
                                                                                                                                 (UUID(), 'user1', '$2a$10$CjfF7eRlYoSI5m3O62pR2uktwNjZyjc3xMiyZg6FMx6pmeia7L65q', '18060601823', 'user1@example.com', 'https://example.com/avatar1.jpg', 0, 0, 1, NOW(), NOW()),
                                                                                                                                 (UUID(), 'user2', '$2a$10$CjfF7eRlYoSI5m3O62pR2uktwNjZyjc3xMiyZg6FMx6pmeia7L65q', '13800000002', 'user2@example.com', 'https://example.com/avatar2.jpg', 0, 0, 1, NOW(), NOW());
