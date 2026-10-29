-- 学校、学校圈、用户扩展（注册/学校认证）
-- 执行前请确保 xl_travel_user 已存在；若已有表可只执行 ALTER 与新建表部分

-- 1) 用户表扩展：手机号、密码、学校、认证状态
ALTER TABLE xl_travel_user
  ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT '手机号（注册后唯一）' AFTER stars,
  ADD COLUMN password VARCHAR(128) DEFAULT NULL COMMENT '密码（BCrypt）' AFTER phone,
  ADD COLUMN school_id BIGINT(20) DEFAULT NULL COMMENT '认证学校id' AFTER password,
  ADD COLUMN school_verified TINYINT(1) DEFAULT 0 COMMENT '学校认证 0否 1是' AFTER school_id;
ALTER TABLE xl_travel_user ADD UNIQUE KEY uk_phone (phone);

-- 2) 学校表
DROP TABLE IF EXISTS xl_school;
CREATE TABLE xl_school (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  name        VARCHAR(128) NOT NULL                COMMENT '学校名称',
  region      VARCHAR(64)  DEFAULT ''             COMMENT '地区',
  create_time DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='学校表';

-- 3) 学校圈帖子
DROP TABLE IF EXISTS xl_school_post;
CREATE TABLE xl_school_post (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  school_id   BIGINT(20)   NOT NULL                COMMENT '学校id',
  user_id     BIGINT(20)   NOT NULL                COMMENT '发帖用户id',
  content     TEXT         NOT NULL                COMMENT '内容',
  images      VARCHAR(500) DEFAULT ''             COMMENT '图片url，逗号分隔',
  create_time DATETIME                             COMMENT '发帖时间',
  PRIMARY KEY (id),
  KEY idx_school_time (school_id, create_time)
) ENGINE=InnoDB COMMENT='学校圈帖子';

-- 示例学校数据
INSERT INTO xl_school (name, region, create_time) VALUES
('北京大学', '北京', now()),
('清华大学', '北京', now()),
('复旦大学', '上海', now()),
('上海交通大学', '上海', now()),
('浙江大学', '浙江', now()),
('南京大学', '江苏', now()),
('武汉大学', '湖北', now()),
('西安交通大学', '陕西', now());
