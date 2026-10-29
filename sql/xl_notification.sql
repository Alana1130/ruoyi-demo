-- 消息/通知
CREATE TABLE IF NOT EXISTS xl_notification (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id bigint NOT NULL COMMENT '接收用户id',
  type varchar(32) NOT NULL DEFAULT 'system' COMMENT 'discussion_reply|order_ship|system',
  title varchar(128) NOT NULL COMMENT '标题',
  content varchar(500) DEFAULT NULL COMMENT '内容',
  related_id bigint DEFAULT NULL COMMENT '关联id(如留言id/订单id)',
  related_type varchar(32) DEFAULT NULL COMMENT '关联类型 chat|order',
  is_read tinyint NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
  create_time datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通知';
