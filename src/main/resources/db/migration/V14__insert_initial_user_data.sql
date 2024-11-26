-- 插入初始用户数据
INSERT INTO tb_user (id, username, password, mobile_phone, e_mail, avatar, expired, locked, enabled, created_at, updated_at) VALUES
                                                                                                                                 (UUID(), 'admin', '{bcrypt}$2a$10$z0kuK6OSOx6IFrfJE4VSY.p54.pI1I86i1eTw/paeFCZWe1cRIgny', '18060601823', 'jongwong360.@qq.com', 'https://example.com/avatar1.jpg', 0, 0, 1, NOW(), NOW()),
                                                                                                                                 (UUID(), 'user1', '{bcrypt}$2a$10$z0kuK6OSOx6IFrfJE4VSY.p54.pI1I86i1eTw/paeFCZWe1cRIgny', '18060601823', 'user1.@yoohoo.cn', 'https://example.com/avatar2.jpg', 0, 0, 1, NOW(), NOW());
