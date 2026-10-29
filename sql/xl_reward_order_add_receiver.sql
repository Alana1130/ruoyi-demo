-- 为已有表 xl_reward_order 增加收货信息字段（若表是按旧版创建的，执行本脚本）
-- 若你执行的是最新版 xinglv_travel.sql 建表，则无需执行本文件。

alter table xl_reward_order add column receiver_name varchar(64) default null comment '收货人姓名';
alter table xl_reward_order add column receiver_phone varchar(32) default null comment '收货人电话';
alter table xl_reward_order add column receiver_address varchar(255) default null comment '收货地址';
