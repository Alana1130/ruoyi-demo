-- 宠物经验与进化：为已有 xl_user_pet 表增加 exp、evolution_stage 列（若已执行过 xl_small_world.sql 新版本可跳过）
-- 执行方式：在已有库上执行以下 ALTER，无则忽略报错

ALTER TABLE xl_user_pet ADD COLUMN exp INT(11) DEFAULT 0 COMMENT '当前经验值' AFTER level;
ALTER TABLE xl_user_pet ADD COLUMN evolution_stage INT(11) DEFAULT 0 COMMENT '进化阶段0幼年1成长2成熟3完全体' AFTER exp;
