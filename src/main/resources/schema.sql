-- Crabapple Standard MS - MySQL schema
-- 仅用于新建数据库；如需保留已有数据，请自行迁移，不要直接覆盖生产库。

CREATE TABLE IF NOT EXISTS `t_user` (
  `id_` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account_` varchar(64) NOT NULL COMMENT '登录账号',
  `username_` varchar(64) DEFAULT NULL COMMENT '显示名称',
  `password_` varchar(100) NOT NULL COMMENT 'BCrypt 口令摘要',
  `mobile_` varchar(32) DEFAULT NULL COMMENT '手机号',
  `id_number_` varchar(64) DEFAULT NULL COMMENT '证件号',
  `email_` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `gender_` varchar(16) DEFAULT NULL COMMENT '性别',
  `birthday_` varchar(32) DEFAULT NULL COMMENT '生日',
  `address_` varchar(256) DEFAULT NULL COMMENT '地址',
  `create_user_id_` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time_` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `update_user_id_` bigint DEFAULT NULL COMMENT '更新人ID',
  `update_time_` varchar(32) DEFAULT NULL COMMENT '更新时间',
  `login_count_` int DEFAULT 0 COMMENT '登录次数',
  `from_` varchar(64) DEFAULT NULL COMMENT '来源',
  `status_` varchar(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id_`),
  UNIQUE KEY `uk_user_account` (`account_`),
  UNIQUE KEY `uk_user_mobile` (`mobile_`),
  UNIQUE KEY `uk_user_email` (`email_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

CREATE TABLE IF NOT EXISTS `t_properties` (
  `id_` bigint NOT NULL COMMENT '配置ID',
  `cipher_` varchar(8) DEFAULT 'no' COMMENT '是否加密：yes/no',
  `name_` varchar(128) DEFAULT NULL COMMENT '配置名称',
  `key_` varchar(128) NOT NULL COMMENT '配置键',
  `value_` longtext COMMENT '配置值',
  `desc_` varchar(512) DEFAULT NULL COMMENT '说明',
  `create_user_id_` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time_` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `update_user_id_` bigint DEFAULT NULL COMMENT '更新人ID',
  `update_time_` varchar(32) DEFAULT NULL COMMENT '更新时间',
  `type_` varchar(64) DEFAULT NULL COMMENT '类型',
  `status_` varchar(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id_`),
  UNIQUE KEY `uk_properties_key` (`key_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统参数';

CREATE TABLE IF NOT EXISTS `t_sms` (
  `id_` bigint NOT NULL COMMENT '短信记录ID',
  `mobile_` varchar(32) NOT NULL COMMENT '手机号',
  `valid_code_` varchar(16) NOT NULL COMMENT '验证码',
  `valid_code_time_` varchar(32) DEFAULT NULL COMMENT '验证码过期时间',
  `create_user_id_` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time_` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `update_user_id_` bigint DEFAULT NULL COMMENT '更新人ID',
  `update_time_` varchar(32) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id_`),
  KEY `idx_sms_mobile` (`mobile_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短信验证码';

CREATE TABLE IF NOT EXISTS `t_systemlog` (
  `id_` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module_` varchar(64) DEFAULT NULL COMMENT '模块',
  `operation_` varchar(128) DEFAULT NULL COMMENT '操作',
  `account_` varchar(64) DEFAULT NULL COMMENT '账号',
  `username_` varchar(64) DEFAULT NULL COMMENT '用户名',
  `mobile_` varchar(32) DEFAULT NULL COMMENT '手机号',
  `content_` longtext COMMENT '内容',
  `api_name_` varchar(128) DEFAULT NULL COMMENT '接口名',
  `api_param_` longtext COMMENT '接口参数',
  `clientip_` varchar(64) DEFAULT NULL COMMENT '客户端IP',
  `user_agent_` varchar(512) DEFAULT NULL COMMENT 'User-Agent',
  `create_time_` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `create_user_id_` bigint DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id_`),
  KEY `idx_systemlog_create_time` (`create_time_`),
  KEY `idx_systemlog_module` (`module_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志';
