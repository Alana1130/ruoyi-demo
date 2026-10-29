-- 讨论/留言：目的地、线路下的互动
CREATE TABLE IF NOT EXISTS xl_chat_message (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  topic_type varchar(32) NOT NULL COMMENT 'destination | route',
  topic_id bigint NOT NULL COMMENT '目的地id或线路id',
  user_id bigint NOT NULL COMMENT '用户id',
  nickname varchar(64) DEFAULT NULL COMMENT '发帖时昵称快照',
  content varchar(1000) NOT NULL COMMENT '内容',
  create_time datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_topic (topic_type, topic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论留言(目的地/线路)';
