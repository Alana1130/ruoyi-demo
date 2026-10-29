-- 互动：点赞、评论、想去打卡（去过=已有 xl_user_checkin 点亮记录）

-- 点赞（攻略/约伴/闲置等）
DROP TABLE IF EXISTS xl_like;
CREATE TABLE xl_like (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '用户id',
  target_type       VARCHAR(32)  NOT NULL                COMMENT '对象类型: guide/companion/idle',
  target_id         BIGINT(20)   NOT NULL                COMMENT '对象id',
  create_time       DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_target (user_id, target_type, target_id),
  KEY idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 评论（攻略/约伴/闲置等，支持回复）
DROP TABLE IF EXISTS xl_comment;
CREATE TABLE xl_comment (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '用户id',
  target_type       VARCHAR(32)  NOT NULL                COMMENT '对象类型: guide/companion/idle',
  target_id         BIGINT(20)   NOT NULL                COMMENT '对象id',
  content           VARCHAR(500) NOT NULL                COMMENT '评论内容',
  parent_id         BIGINT(20)   DEFAULT NULL             COMMENT '父评论id（回复时）',
  create_time       DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_target (target_type, target_id),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 想去（目的地心愿，去过=点亮 xl_user_checkin）
DROP TABLE IF EXISTS xl_destination_wish;
CREATE TABLE xl_destination_wish (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '用户id',
  destination_id    BIGINT(20)   NOT NULL                COMMENT '目的地id',
  create_time       DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_dest (user_id, destination_id),
  KEY idx_dest (destination_id)
) ENGINE=InnoDB COMMENT='目的地想去打卡表';
