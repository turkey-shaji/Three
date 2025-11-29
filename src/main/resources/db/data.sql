-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
-- 插入测试数据
INSERT INTO `user` (`username`, `email`, `phone`) VALUES
                                                      ('zhangsan', 'zhangsan@example.com', '13800138000'),
                                                      ('lisi', 'lisi@example.com', '13900139000'),
                                                      ('wangwu', 'wangwu@example.com', '13700137000');