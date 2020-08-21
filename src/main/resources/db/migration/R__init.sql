USE `sharding_jdbc_test`;

-- 不参与分表
create table if not exists `session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 参与分表（逻辑表）
create table if not exists `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_id` bigint(20) NOT NULL,
  `content` varchar(10000) NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 物理分表
create table if not exists message_0 LIKE message;
create table if not exists message_1 LIKE message;


-- 准备数据
INSERT INTO `sharding_jdbc_test`.`session`(`id`, `uuid`) VALUES (1, 0);
INSERT INTO `sharding_jdbc_test`.`session`(`id`, `uuid`) VALUES (2, 0);
INSERT INTO `sharding_jdbc_test`.`session`(`id`, `uuid`) VALUES (100, 0);
INSERT INTO `sharding_jdbc_test`.`message_0`(`id`, `session_id`, `content`, `status`, `created`, `updated`) VALUES (1, 2, 'test2', 0, '2020-08-21 11:44:48', '2020-08-21 11:46:48');
INSERT INTO `sharding_jdbc_test`.`message_0`(`id`, `session_id`, `content`, `status`, `created`, `updated`) VALUES (2, 100, 'test100', 0, '2020-08-21 11:47:35', '2020-08-21 11:47:38');
INSERT INTO `sharding_jdbc_test`.`message_1`(`id`, `session_id`, `content`, `status`, `created`, `updated`) VALUES (1, 1, 'test1', 0, '2020-08-21 11:45:13', '2020-08-21 11:46:02');

