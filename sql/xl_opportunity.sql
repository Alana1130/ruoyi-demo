-- 实践机会（兼职/志愿），可与目的地关联，旅行场景下「边玩边实践」
CREATE TABLE IF NOT EXISTS xl_opportunity (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  destination_id bigint DEFAULT NULL COMMENT '关联目的地（可选）',
  region varchar(64) DEFAULT NULL COMMENT '地区，如 北京、杭州',
  type varchar(32) NOT NULL COMMENT 'part_time=兼职, volunteer=志愿',
  title varchar(128) NOT NULL COMMENT '标题',
  description text COMMENT '描述',
  contact_info varchar(512) DEFAULT NULL COMMENT '报名/联系方式',
  create_by varchar(64) DEFAULT '',
  create_time datetime DEFAULT NULL,
  update_by varchar(64) DEFAULT '',
  update_time datetime DEFAULT NULL,
  remark varchar(500) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_destination (destination_id),
  KEY idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实践机会（兼职/志愿）';
