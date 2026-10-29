-- 为景点、美食、住宿表增加经纬度，用于地图打点
ALTER TABLE xl_attraction ADD COLUMN latitude DOUBLE NULL COMMENT '纬度' AFTER ai_verified;
ALTER TABLE xl_attraction ADD COLUMN longitude DOUBLE NULL COMMENT '经度' AFTER latitude;

ALTER TABLE xl_food ADD COLUMN latitude DOUBLE NULL COMMENT '纬度' AFTER ai_verified;
ALTER TABLE xl_food ADD COLUMN longitude DOUBLE NULL COMMENT '经度' AFTER latitude;

ALTER TABLE xl_accommodation ADD COLUMN latitude DOUBLE NULL COMMENT '纬度' AFTER ai_verified;
ALTER TABLE xl_accommodation ADD COLUMN longitude DOUBLE NULL COMMENT '经度' AFTER latitude;
