-- 出行倒计时、学期/假期计划、二手闲置（大学生旅游扩展）

-- 出行倒计时
DROP TABLE IF EXISTS xl_trip_countdown;
CREATE TABLE xl_trip_countdown (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '用户id',
  title             VARCHAR(128) NOT NULL                COMMENT '标题，如「西安三日游」',
  destination_id    BIGINT(20)   DEFAULT NULL            COMMENT '关联目的地id（可选）',
  trip_date         DATE         NOT NULL                COMMENT '出行日期',
  remind_days       INT(11)      DEFAULT 1              COMMENT '提前几天提醒（0不提醒）',
  memo              VARCHAR(255) DEFAULT ''              COMMENT '备注',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_trip_date (trip_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出行倒计时表';

-- 学期/假期计划
DROP TABLE IF EXISTS xl_semester_plan;
CREATE TABLE xl_semester_plan (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '用户id',
  name              VARCHAR(64)  NOT NULL                COMMENT '计划名称，如 2025寒假、五一假期',
  start_date        DATE         NOT NULL                COMMENT '开始日期',
  end_date          DATE         NOT NULL                COMMENT '结束日期',
  memo              VARCHAR(255) DEFAULT ''              COMMENT '备注',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期/假期计划表';

-- 计划关联目的地（多选）
DROP TABLE IF EXISTS xl_semester_plan_dest;
CREATE TABLE xl_semester_plan_dest (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  plan_id           BIGINT(20)   NOT NULL                COMMENT '计划id',
  destination_id    BIGINT(20)   NOT NULL                COMMENT '目的地id',
  sort_order        INT(11)      DEFAULT 0               COMMENT '排序',
  create_time       DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_plan (plan_id)
) ENGINE=InnoDB COMMENT='计划关联目的地';

-- 二手/闲置（用星星交易）
DROP TABLE IF EXISTS xl_idle_item;
CREATE TABLE xl_idle_item (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '发布者用户id',
  title             VARCHAR(128) NOT NULL                COMMENT '标题',
  category          VARCHAR(32)  DEFAULT 'other'         COMMENT '分类：textbook教材/travel_gear出行装备/other其他',
  description       VARCHAR(500) DEFAULT ''             COMMENT '描述',
  price_stars       INT(11)      NOT NULL                COMMENT '售价（星星）',
  images            VARCHAR(500) DEFAULT ''              COMMENT '图片url，多张逗号分隔',
  status            CHAR(1)      DEFAULT '0'             COMMENT '0在售 1已预订 2已售出',
  buyer_id          BIGINT(20)   DEFAULT NULL            COMMENT '买家id（成交后）',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_status (status),
  KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二手闲置表';
