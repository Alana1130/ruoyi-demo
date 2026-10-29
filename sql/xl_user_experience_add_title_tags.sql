-- 攻略/游记：体验表增加标题与话题标签
alter table xl_user_experience add column title varchar(200) default null comment '攻略标题（可选，用于目的地级游记）' after content;
alter table xl_user_experience add column tags varchar(200) default '' comment '话题标签，逗号分隔，如 穷游,周末两日游' after title;
