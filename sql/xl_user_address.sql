-- 用户收货地址表（单独执行此文件可修复 Table 'xl_user_address' doesn't exist）
-- 在已存在的数据库 xinglv_travel 中执行即可

CREATE TABLE IF NOT EXISTS xl_user_address (
  id                bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '主键',
  user_id           bigint(20)      NOT NULL                   COMMENT '用户id',
  receiver_name     varchar(64)     NOT NULL                   COMMENT '收货人姓名',
  receiver_phone    varchar(32)     NOT NULL                   COMMENT '收货人电话',
  receiver_address  varchar(255)    NOT NULL                   COMMENT '收货地址',
  is_default        tinyint(1)      DEFAULT 0                  COMMENT '是否默认 0否 1是',
  create_by         varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time       datetime                                  COMMENT '创建时间',
  update_by         varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time       datetime                                  COMMENT '更新时间',
  remark            varchar(500)    DEFAULT NULL              COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';
