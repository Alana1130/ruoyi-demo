-- 小世界：地区解锁得宠物/美食经营权，星星交易，饲养宠物

-- 地区宠物模板（每个目的地可配置多只）
DROP TABLE IF EXISTS xl_pet_template;
CREATE TABLE xl_pet_template (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  destination_id    BIGINT(20)   NOT NULL                COMMENT '目的地id',
  name              VARCHAR(64)  NOT NULL                COMMENT '宠物名称',
  icon              VARCHAR(128) DEFAULT ''              COMMENT '图标(emoji或url)',
  description       VARCHAR(255) DEFAULT ''              COMMENT '描述',
  sort_order        INT(11)      DEFAULT 0               COMMENT '排序',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_dest (destination_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区宠物模板表';

-- 地区美食经营权模板（每个目的地可配置多种）
DROP TABLE IF EXISTS xl_food_license_template;
CREATE TABLE xl_food_license_template (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  destination_id    BIGINT(20)   NOT NULL                COMMENT '目的地id',
  name              VARCHAR(64)  NOT NULL                COMMENT '经营权名称(如西湖龙井)',
  icon              VARCHAR(128) DEFAULT ''               COMMENT '图标',
  description       VARCHAR(255) DEFAULT ''              COMMENT '描述',
  sort_order        INT(11)      DEFAULT 0               COMMENT '排序',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_dest (destination_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区美食经营权模板表';

-- 用户拥有的宠物（可饲养、可交易）
DROP TABLE IF EXISTS xl_user_pet;
CREATE TABLE xl_user_pet (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '所属用户id',
  pet_template_id   BIGINT(20)   NOT NULL                COMMENT '宠物模板id',
  nickname          VARCHAR(64)  DEFAULT ''               COMMENT '用户起的昵称',
  hunger            INT(11)      DEFAULT 80              COMMENT '饱食度0-100',
  mood              INT(11)      DEFAULT 80              COMMENT '心情0-100',
  level             INT(11)      DEFAULT 1               COMMENT '等级1-10',
  exp               INT(11)      DEFAULT 0                COMMENT '当前经验值',
  evolution_stage  INT(11)      DEFAULT 0                COMMENT '进化阶段0幼年1成长2成熟3完全体',
  last_feed_at      DATETIME     DEFAULT NULL            COMMENT '上次喂食时间',
  last_play_at      DATETIME     DEFAULT NULL            COMMENT '上次互动时间',
  got_at            DATETIME                             COMMENT '获得时间',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_template (pet_template_id)
) ENGINE=InnoDB COMMENT='用户宠物表';

-- 用户拥有的美食经营权（可展示、可交易）
DROP TABLE IF EXISTS xl_user_food_license;
CREATE TABLE xl_user_food_license (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id           BIGINT(20)   NOT NULL                COMMENT '所属用户id',
  license_template_id BIGINT(20) NOT NULL                COMMENT '经营权模板id',
  got_at            DATETIME                             COMMENT '获得时间',
  create_time       DATETIME                             COMMENT '创建时间',
  update_time       DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_template (license_template_id)
) ENGINE=InnoDB COMMENT='用户美食经营权表';

-- 星星交易（挂单与成交记录）
DROP TABLE IF EXISTS xl_star_trade;
CREATE TABLE xl_star_trade (
  id                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  seller_id         BIGINT(20)   NOT NULL                COMMENT '卖家用户id',
  buyer_id          BIGINT(20)   DEFAULT NULL            COMMENT '买家用户id(挂单时为空)',
  item_type         VARCHAR(32)  NOT NULL                COMMENT 'pet/food_license',
  item_id           BIGINT(20)   NOT NULL                COMMENT 'user_pet.id 或 user_food_license.id',
  stars_amount      INT(11)      NOT NULL                COMMENT '星星数量',
  status            CHAR(1)      DEFAULT '0'             COMMENT '0挂单中 1已完成 2已取消',
  created_at        DATETIME                             COMMENT '创建时间',
  completed_at      DATETIME     DEFAULT NULL            COMMENT '成交时间',
  PRIMARY KEY (id),
  KEY idx_seller (seller_id),
  KEY idx_buyer (buyer_id),
  KEY idx_status (status)
) ENGINE=InnoDB COMMENT='星星交易表';

-- 示例数据：杭州、南京、成都各 2 只宠物 + 2 种经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(1, '西湖小鲤', '🐟', '西湖边的小鲤鱼', 1, NOW(), NOW()),
(1, '灵隐松鼠', '🐿️', '灵隐寺旁的小松鼠', 2, NOW(), NOW()),
(2, '秦淮灯宠', '🏮', '夫子庙秦淮灯会吉祥物', 1, NOW(), NOW()),
(2, '中山陵鹿', '🦌', '钟山小鹿', 2, NOW(), NOW()),
(3, '熊猫崽', '🐼', '成都熊猫基地同款', 1, NOW(), NOW()),
(3, '宽窄猫', '🐱', '宽窄巷子猫掌柜', 2, NOW(), NOW());

INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(1, '西湖龙井', '🍵', '杭州西湖龙井茶经营权', 1, NOW(), NOW()),
(1, '知味小笼', '🥟', '知味观小笼包经营权', 2, NOW(), NOW()),
(2, '盐水鸭', '🦆', '南京盐水鸭经营权', 1, NOW(), NOW()),
(2, '美龄粥', '🥣', '美龄粥经营权', 2, NOW(), NOW()),
(3, '火锅底料', '🍲', '成都火锅底料经营权', 1, NOW(), NOW()),
(3, '兔头铺', '🌶️', '成都兔头经营权', 2, NOW(), NOW());
