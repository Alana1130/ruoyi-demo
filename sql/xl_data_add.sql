-- 增量数据：更多目的地、景点、美食、住宿、线路、打车、门票、小世界宠物/经营权
-- 执行前请确保已执行 xinglv_travel.sql 和 xl_small_world.sql（若用小世界）

-- 新增目的地（id 4~7）
INSERT INTO xl_destination (id, name, region, description, star_reward, create_time) VALUES
(4, '北京', '华北', '故宫、长城、颐和园，首都文化游', 3, now()),
(5, '上海', '华东', '外滩、迪士尼、豫园，魔都打卡', 3, now()),
(6, '西安', '西北', '兵马俑、大雁塔、回民街，古都美食', 3, now()),
(7, '厦门', '华东', '鼓浪屿、曾厝垵、南普陀，海滨文艺', 2, now())
ON DUPLICATE KEY UPDATE name = VALUES(name), region = VALUES(region), description = VALUES(description), star_reward = VALUES(star_reward);

-- 新增景点（destination_id 4~7，各 2 个）
INSERT INTO xl_attraction (id, destination_id, name, ticket_price, description, ai_verified, create_time) VALUES
(7, 4, '故宫', 60, '明清皇宫，需预约', 1, now()),
(8, 4, '长城八达岭', 40, '不到长城非好汉', 1, now()),
(9, 5, '外滩', 0, '万国建筑博览', 1, now()),
(10, 5, '迪士尼乐园', 399, '上海迪士尼度假区', 1, now()),
(11, 6, '兵马俑', 120, '世界第八大奇迹', 1, now()),
(12, 6, '大雁塔', 50, '唐代名塔', 1, now()),
(13, 7, '鼓浪屿', 0, '岛上联票另购', 1, now()),
(14, 7, '南普陀寺', 0, '免费，香火旺盛', 1, now())
ON DUPLICATE KEY UPDATE destination_id = VALUES(destination_id), name = VALUES(name), ticket_price = VALUES(ticket_price), description = VALUES(description);

-- 新增美食
INSERT INTO xl_food (id, destination_id, name, price_range, description, ai_verified, create_time) VALUES
(5, 4, '全聚德', '120-180/人', '北京烤鸭', 1, now()),
(6, 4, '护国寺小吃', '20-40/人', '豆汁、驴打滚', 1, now()),
(7, 5, '小杨生煎', '25-45/人', '生煎包', 1, now()),
(8, 5, '老正兴', '80-120/人', '本帮菜', 1, now()),
(9, 6, '回民街肉夹馍', '15-25/人', '腊汁肉夹馍', 1, now()),
(10, 6, '羊肉泡馍', '30-50/人', '老孙家等', 1, now()),
(11, 7, '沙茶面', '20-35/人', '厦门特色', 1, now()),
(12, 7, '海蛎煎', '25-40/人', '闽南小吃', 1, now())
ON DUPLICATE KEY UPDATE destination_id = VALUES(destination_id), name = VALUES(name), price_range = VALUES(price_range), description = VALUES(description);

-- 新增住宿
INSERT INTO xl_accommodation (id, destination_id, name, type, price_per_night, ai_verified, create_time) VALUES
(5, 4, '前门青旅', '青旅', 100, 1, now()),
(6, 4, '如家北京站店', '经济型', 220, 1, now()),
(7, 5, '外滩青旅', '青旅', 120, 1, now()),
(8, 5, '汉庭南京路店', '经济型', 280, 1, now()),
(9, 6, '钟楼青旅', '青旅', 85, 1, now()),
(10, 6, '如家钟楼店', '经济型', 180, 1, now()),
(11, 7, '曾厝垵民宿', '民宿', 150, 1, now()),
(12, 7, '鼓浪屿青旅', '青旅', 130, 1, now())
ON DUPLICATE KEY UPDATE destination_id = VALUES(destination_id), name = VALUES(name), type = VALUES(type), price_per_night = VALUES(price_per_night);

-- 新增线路
INSERT INTO xl_route (id, destination_id, name, days, total_budget_min, total_budget_max, description, create_time) VALUES
(4, 4, '北京两日经典', 2, 500, 800, '故宫+长城+烤鸭', now()),
(5, 5, '上海两日打卡', 2, 600, 1000, '外滩+迪士尼+本帮菜', now()),
(6, 6, '西安两日古都', 2, 450, 700, '兵马俑+大雁塔+回民街', now()),
(7, 7, '厦门两日海滨', 2, 400, 650, '鼓浪屿+曾厝垵+南普陀', now())
ON DUPLICATE KEY UPDATE destination_id = VALUES(destination_id), name = VALUES(name), days = VALUES(days), total_budget_min = VALUES(total_budget_min), total_budget_max = VALUES(total_budget_max), description = VALUES(description);

-- 线路节点（route_id 4~7，对应上面景点/美食 id）
INSERT INTO xl_route_item (route_id, day_no, item_type, item_id, sort_order, transport_approx, create_time) VALUES
(4, 1, 'attraction', 7, 0, '地铁1号线', now()),(4, 1, 'food', 5, 1, '地铁', now()),
(4, 2, 'attraction', 8, 0, '旅游专线/高铁', now()),(4, 2, 'food', 6, 1, '公交', now()),
(5, 1, 'attraction', 9, 0, '地铁', now()),(5, 1, 'food', 7, 1, '地铁', now()),
(5, 2, 'attraction', 10, 0, '地铁11号线', now()),(5, 2, 'food', 8, 1, '地铁', now()),
(6, 1, 'attraction', 11, 0, '旅游专线', now()),(6, 1, 'food', 9, 1, '公交', now()),
(6, 2, 'attraction', 12, 0, '地铁', now()),(6, 2, 'food', 10, 1, '步行', now()),
(7, 1, 'attraction', 13, 0, '轮渡', now()),(7, 1, 'food', 11, 1, '步行', now()),
(7, 2, 'attraction', 14, 0, '公交', now()),(7, 2, 'food', 12, 1, '公交', now())
ON DUPLICATE KEY UPDATE route_id = VALUES(route_id), day_no = VALUES(day_no), item_type = VALUES(item_type), item_id = VALUES(item_id);

-- 打车/出行
INSERT INTO xl_taxi (destination_id, name, app_type, description, price_approx, create_time) VALUES
(4, '北京景区用车', '滴滴/高德', '故宫、长城等可约', '市区约20-50元，长城往返约200元', now()),
(5, '上海市区及迪士尼', '滴滴/高德', '外滩、迪士尼均可达', '市区约15-40元，迪士尼约50-80元', now()),
(6, '西安市区及兵马俑', '滴滴/高德/直通车', '兵马俑有景区直通车', '市区约15-30元，兵马俑往返约60元', now()),
(7, '厦门环岛及鼓浪屿', '滴滴/公交/轮渡', '鼓浪屿需轮渡', '环岛约20-40元，轮渡35往返', now());

-- 门票
INSERT INTO xl_ticket (destination_id, attraction_id, name, price, original_price, buy_link, description, create_time) VALUES
(4, 7, '故宫门票', 60, 60, '', '需提前预约', now()),
(4, 8, '八达岭长城', 40, 45, '', '可买缆车联票', now()),
(5, 10, '上海迪士尼一日票', 399, 435, '', '官方或各大平台', now()),
(6, 11, '兵马俑门票', 120, 120, '', '含秦始皇陵', now()),
(6, 12, '大雁塔登塔', 50, 50, '', '大慈恩寺内', now()),
(7, 13, '鼓浪屿联票', 90, 100, '', '含五大景点', now());

-- 轮播图（多几条）
INSERT INTO xl_banner (title, image, link, sort_order, status, create_time) VALUES
('小世界 · 解锁地区得宠物', '', '/pages/small-world/index', 4, '0', now()),
('约伴出行', '', '/pages/companion/list', 5, '0', now()),
('攻略·游记', '', '/pages/guide/index', 6, '0', now());

-- 兑换商品（多几样）
INSERT INTO xl_reward_item (name, type, stars_cost, price, description, partner_name, stock, allow_purchase, create_time) VALUES
('故宫文创书签', 'culture_tourism', 8, 22.00, '故宫联名书签', '故宫文创', 150, 1, now()),
('西安兵马俑小摆件', 'culture_tourism', 12, 35.00, '迷你兵马俑', '陕西文旅', 80, 1, now()),
('厦门鼓浪屿明信片', 'culture_tourism', 5, 12.00, '鼓浪屿风景明信片', '厦门文旅', 200, 1, now());

-- 小世界：北京、上海、西安、厦门 宠物与经营权（需已建小世界表）
INSERT INTO xl_pet_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(4, '故宫喵', '🐱', '故宫里的御猫后代', 1, now(), now()),
(4, '长城龙', '🐉', '长城守护小青龙', 2, now(), now()),
(5, '外滩鸥', '🕊️', '外滩江鸥', 1, now(), now()),
(5, '迪士尼达菲', '🐻', '迪士尼达菲熊', 2, now(), now()),
(6, '兵马俑小兵', '🗿', '迷你兵马俑', 1, now(), now()),
(6, '大雁塔雁', '🦆', '大雁塔下的小雁', 2, now(), now()),
(7, '鼓浪屿章鱼', '🐙', '鼓浪屿海边小章鱼', 1, now(), now()),
(7, '南普陀龟', '🐢', '南普陀放生池小龟', 2, now(), now());

INSERT INTO xl_food_license_template (destination_id, name, icon, description, sort_order, create_time, update_time) VALUES
(4, '北京烤鸭', '🦆', '北京烤鸭经营权', 1, now(), now()),
(4, '驴打滚', '🥮', '老北京驴打滚经营权', 2, now(), now()),
(5, '生煎包', '🥟', '上海生煎经营权', 1, now(), now()),
(5, '本帮菜', '🍲', '本帮菜经营权', 2, now(), now()),
(6, '肉夹馍', '🥙', '西安肉夹馍经营权', 1, now(), now()),
(6, '羊肉泡馍', '🍜', '羊肉泡馍经营权', 2, now(), now()),
(7, '沙茶面', '🍜', '厦门沙茶面经营权', 1, now(), now()),
(7, '海蛎煎', '🦪', '海蛎煎经营权', 2, now(), now());

-- 修正自增（避免主键冲突；若表已有更大 id 请酌情调大）
ALTER TABLE xl_destination AUTO_INCREMENT = 8;
ALTER TABLE xl_attraction AUTO_INCREMENT = 15;
ALTER TABLE xl_food AUTO_INCREMENT = 13;
ALTER TABLE xl_accommodation AUTO_INCREMENT = 13;
ALTER TABLE xl_route AUTO_INCREMENT = 8;
