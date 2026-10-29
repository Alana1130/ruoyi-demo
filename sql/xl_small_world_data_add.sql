-- 小世界增量数据：为各地增加更多宠物与美食经营权模板（不指定 id，用自增）
-- 执行前请确保已执行 xl_small_world.sql（及若已跑 xl_data_add 则已有北京上海西安厦门各 2 只宠物+2 经营权）
-- 本脚本为 7 个目的地各再增加 2 只宠物 + 2 种经营权，解锁时随机池更丰富

-- 杭州(1) 多 2 宠 + 2 经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(1, '雷峰塔小蛇', '🐍', '雷峰塔下白娘子同款', 3, now(), now()),
(1, '宋城小戏精', '🎭', '宋城演艺小吉祥物', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(1, '西湖醋鱼', '🐟', '楼外楼西湖醋鱼经营权', 3, now(), now()),
(1, '片儿川', '🍜', '杭州片儿川经营权', 4, now(), now());

-- 南京(2) 多 2 宠 + 2 经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(2, '玄武湖鸭', '🦆', '玄武湖绿头鸭', 3, now(), now()),
(2, '明城墙砖', '🧱', '明城墙小砖灵', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(2, '鸭血粉丝', '🍲', '南京鸭血粉丝经营权', 3, now(), now()),
(2, '糖芋苗', '🍮', '桂花糖芋苗经营权', 4, now(), now());

-- 成都(3) 多 2 宠 + 2 经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(3, '锦里小熊猫', '🐼', '锦里古街小熊猫', 3, now(), now()),
(3, '九眼桥鱼', '🐟', '九眼桥下锦鲤', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(3, '串串香', '🍢', '成都串串经营权', 3, now(), now()),
(3, '冰粉', '🍧', '手搓冰粉经营权', 4, now(), now());

-- 北京(4) 多 2 宠 + 2 经营权（若尚未有 4 的数据可先执行 xl_data_add.sql 中小世界部分）
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(4, '颐和园鹤', '🦩', '颐和园十七孔桥仙鹤', 3, now(), now()),
(4, '胡同喜鹊', '🐦', '老北京胡同喜鹊', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(4, '炸酱面', '🍝', '老北京炸酱面经营权', 3, now(), now()),
(4, '豆汁焦圈', '🥣', '豆汁焦圈经营权', 4, now(), now());

-- 上海(5) 多 2 宠 + 2 经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(5, '豫园金鱼', '🐠', '豫园九曲桥金鱼', 3, now(), now()),
(5, '陆家嘴云', '☁️', '陆家嘴小云朵', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(5, '小笼包', '🥟', '南翔小笼经营权', 3, now(), now()),
(5, '葱油拌面', '🍜', '本帮葱油拌面经营权', 4, now(), now());

-- 西安(6) 多 2 宠 + 2 经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(6, '城墙朱雀', '🐦', '西安城墙朱雀门小雀', 3, now(), now()),
(6, '钟楼鼓娃', '🥁', '钟鼓楼小鼓娃', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(6, '凉皮', '🥢', '陕西凉皮经营权', 3, now(), now()),
(6, '甑糕', '🍮', '回民街甑糕经营权', 4, now(), now());

-- 厦门(7) 多 2 宠 + 2 经营权
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(7, '曾厝垵猫', '🐱', '曾厝垵文艺猫', 3, now(), now()),
(7, '环岛路海星', '⭐', '环岛路海边小海星', 4, now(), now());
INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(7, '花生汤', '🥜', '厦门花生汤经营权', 3, now(), now()),
(7, '土笋冻', '🪱', '闽南土笋冻经营权', 4, now(), now());
