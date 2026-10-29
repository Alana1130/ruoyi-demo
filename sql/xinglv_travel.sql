-- ----------------------------
-- 大学生旅游小程序 - 业务表（每表末尾含 create_time, update_time, create_by, update_by）
-- ----------------------------

-- 小程序用户（大学生/游客）
drop table if exists xl_travel_user;
create table xl_travel_user (
  id                bigint(20)      not null auto_increment    comment '主键',
  openid            varchar(64)     default ''                  comment '微信openid',
  nickname          varchar(64)     default ''                  comment '昵称',
  avatar            varchar(255)     default ''                  comment '头像',
  stars             int(11)         default 0                   comment '星星数',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  unique key uk_openid (openid)
) engine=innodb comment = '旅游小程序用户表';

-- 目的地
drop table if exists xl_destination;
create table xl_destination (
  id                bigint(20)      not null auto_increment    comment '主键',
  name              varchar(64)     not null                   comment '目的地名称',
  region            varchar(32)     default ''                  comment '区域',
  description       varchar(500)    default ''                  comment '描述',
  cover_image       varchar(255)    default ''                  comment '封面图',
  star_reward       int(11)         default 1                   comment '点亮可获得星星数',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id)
) engine=innodb comment = '目的地表';

-- 景点
drop table if exists xl_attraction;
create table xl_attraction (
  id                bigint(20)      not null auto_increment    comment '主键',
  destination_id     bigint(20)      not null                   comment '目的地id',
  name              varchar(64)     not null                   comment '景点名称',
  ticket_price      decimal(10,2)   default 0                   comment '门票价格',
  opening_hours     varchar(128)    default ''                  comment '开放时间',
  description       varchar(500)    default ''                  comment '描述',
  image             varchar(255)    default ''                  comment '图片',
  ai_verified       tinyint(1)      default 0                   comment 'AI核验(0否1是)',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_dest (destination_id)
) engine=innodb comment = '景点表';

-- 美食
drop table if exists xl_food;
create table xl_food (
  id                bigint(20)      not null auto_increment    comment '主键',
  destination_id    bigint(20)      not null                   comment '目的地id',
  name              varchar(64)     not null                   comment '名称',
  price_range       varchar(64)     default ''                  comment '价格区间',
  address           varchar(255)    default ''                  comment '地址',
  description       varchar(500)    default ''                  comment '描述',
  image             varchar(255)    default ''                  comment '图片',
  ai_verified       tinyint(1)      default 0                   comment 'AI核验(0否1是)',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_dest (destination_id)
) engine=innodb comment = '美食表';

-- 住宿
drop table if exists xl_accommodation;
create table xl_accommodation (
  id                bigint(20)      not null auto_increment    comment '主键',
  destination_id    bigint(20)      not null                   comment '目的地id',
  name              varchar(64)     not null                   comment '名称',
  type              varchar(32)     default ''                  comment '类型(青旅/经济型等)',
  price_per_night   decimal(10,2)  not null                   comment '每晚价格',
  address           varchar(255)    default ''                  comment '地址',
  description       varchar(500)    default ''                  comment '描述',
  image             varchar(255)    default ''                  comment '图片',
  ai_verified       tinyint(1)      default 0                   comment 'AI核验(0否1是)',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_dest (destination_id)
) engine=innodb comment = '住宿表';

-- 线路
drop table if exists xl_route;
create table xl_route (
  id                bigint(20)      not null auto_increment    comment '主键',
  destination_id    bigint(20)      not null                   comment '目的地id',
  name              varchar(128)    not null                   comment '线路名称',
  days              int(11)         default 1                   comment '天数',
  total_budget_min  decimal(10,2)  default null                comment '预算下限',
  total_budget_max  decimal(10,2)  default null                comment '预算上限',
  description       varchar(500)    default ''                  comment '描述',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_dest (destination_id)
) engine=innodb comment = '线路表';

-- 线路节点（某天的景点/美食/住宿顺序）
drop table if exists xl_route_item;
create table xl_route_item (
  id                bigint(20)      not null auto_increment    comment '主键',
  route_id           bigint(20)      not null                   comment '线路id',
  day_no             int(11)         not null                   comment '第几天',
  item_type         varchar(32)     not null                   comment '类型(attraction/food/accommodation)',
  item_id           bigint(20)      not null                   comment '关联id',
  sort_order        int(11)         default 0                   comment '排序',
  transport_approx  varchar(128)   default ''                  comment '交通说明',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_route (route_id)
) engine=innodb comment = '线路节点表';

-- 用户点亮目的地记录
drop table if exists xl_user_checkin;
create table xl_user_checkin (
  id                bigint(20)      not null auto_increment    comment '主键',
  user_id           bigint(20)      not null                   comment '用户id',
  destination_id    bigint(20)      not null                   comment '目的地id',
  stars_earned      int(11)         not null                   comment '获得星星数',
  checkin_at        datetime                                   comment '点亮时间',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  unique key uk_user_dest (user_id, destination_id),
  key idx_user (user_id)
) engine=innodb comment = '用户点亮记录表';

-- 可兑换商品（文旅/公益/自营）
drop table if exists xl_reward_item;
create table xl_reward_item (
  id                bigint(20)      not null auto_increment    comment '主键',
  name              varchar(128)    not null                   comment '商品名称',
  type              varchar(32)     not null                   comment '类型(culture_tourism/public_welfare/self_operated)',
  stars_cost        int(11)         not null                   comment '所需星星',
  price             decimal(10,2)   default null                comment '现金售价（可空，支持直接购买）',
  description       varchar(500)    default ''                  comment '描述',
  image             varchar(255)    default ''                  comment '图片',
  stock             int(11)         default -1                  comment '库存(-1不限)',
  allow_purchase    tinyint(1)      default 0                   comment '是否支持直接购买0否1是',
  partner_name      varchar(64)     default null                comment '合作方名称',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id)
) engine=innodb comment = '星星兑换/购买商品表';

-- 用户兑换记录
drop table if exists xl_user_exchange;
create table xl_user_exchange (
  id                bigint(20)      not null auto_increment    comment '主键',
  user_id           bigint(20)      not null                   comment '用户id',
  reward_item_id    bigint(20)      not null                   comment '商品id',
  exchanged_at      datetime                                   comment '兑换时间',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_user (user_id)
) engine=innodb comment = '用户兑换记录表';

-- 用户出行体验（供AI真实性校验）
drop table if exists xl_user_experience;
create table xl_user_experience (
  id                bigint(20)      not null auto_increment    comment '主键',
  user_id           bigint(20)      not null                   comment '用户id',
  target_type       varchar(32)     not null                   comment '对象类型(attraction/food等)',
  target_id         bigint(20)      not null                   comment '对象id',
  content           text             not null                   comment '体验内容',
  ai_verified       tinyint(1)      default 0                   comment 'AI核验(0否1是)',
  ai_score         decimal(3,2)     default null                comment 'AI真实性得分',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_target (target_type, target_id)
) engine=innodb comment = '用户出行体验表';

-- 用户预算规划方案
drop table if exists xl_budget_plan;
create table xl_budget_plan (
  id                bigint(20)      not null auto_increment    comment '主键',
  user_id           bigint(20)      default null                comment '用户id',
  destination_id    bigint(20)      not null                   comment '目的地id',
  total_budget      decimal(10,2)  not null                    comment '总预算',
  days              int(11)         not null                   comment '天数',
  accommodation_id bigint(20)      default null                comment '住宿id',
  route_id          bigint(20)      default null                comment '线路id',
  notes             varchar(500)    default ''                  comment '备注',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_user (user_id)
) engine=innodb comment = '预算规划方案表';

-- 轮播图（装饰）
drop table if exists xl_banner;
create table xl_banner (
  id                bigint(20)      not null auto_increment    comment '主键',
  title             varchar(128)    default ''                  comment '标题',
  image             varchar(255)    not null                   comment '图片地址',
  link              varchar(255)    default ''                  comment '跳转链接',
  sort_order        int(11)         default 0                   comment '排序',
  status            char(1)         default '0'                 comment '状态0正常1停用',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id)
) engine=innodb comment = '轮播图表';

-- 打车/出行服务（按目的地）
drop table if exists xl_taxi;
create table xl_taxi (
  id                bigint(20)      not null auto_increment    comment '主键',
  destination_id    bigint(20)      not null                   comment '目的地id',
  name              varchar(64)     not null                   comment '服务名称',
  app_type          varchar(32)     default ''                  comment '平台(滴滴/高德/曹操等)',
  description       varchar(255)    default ''                  comment '说明',
  phone             varchar(32)     default ''                  comment '联系电话',
  link              varchar(255)    default ''                  comment '跳转链接',
  price_approx      varchar(64)     default ''                  comment '参考价说明',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_dest (destination_id)
) engine=innodb comment = '打车/出行服务表';

-- 门票（景点门票、联票等，可带购买链接）
drop table if exists xl_ticket;
create table xl_ticket (
  id                bigint(20)      not null auto_increment    comment '主键',
  destination_id    bigint(20)      not null                   comment '目的地id',
  attraction_id     bigint(20)      default null                comment '关联景点id',
  name              varchar(128)    not null                   comment '门票名称',
  price             decimal(10,2)   not null                   comment '价格',
  original_price    decimal(10,2)   default null                comment '原价',
  buy_link          varchar(255)    default ''                  comment '购买链接',
  description       varchar(500)    default ''                  comment '描述',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_dest (destination_id)
) engine=innodb comment = '门票表';

-- 兑换/购买记录（统一：星星兑换或现金购买）
drop table if exists xl_reward_order;
create table xl_reward_order (
  id                bigint(20)      not null auto_increment    comment '主键',
  user_id           bigint(20)      not null                   comment '用户id',
  reward_item_id    bigint(20)      not null                   comment '商品id',
  order_type        varchar(20)     not null                   comment 'star_exchange-星星兑换 purchase-直接购买',
  stars_used        int(11)         default 0                  comment '消耗星星数',
  amount_paid       decimal(10,2)   default null                comment '实付金额',
  status            char(1)         default '1'                 comment '状态1已完成',
  receiver_name     varchar(64)     default null                comment '收货人姓名',
  receiver_phone    varchar(32)     default null                comment '收货人电话',
  receiver_address  varchar(255)    default null                comment '收货地址',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_user (user_id)
) engine=innodb comment = '商品兑换/购买记录表';

-- 若表已存在，可执行以下语句增加收货信息字段：
-- alter table xl_reward_order add column receiver_name varchar(64) default null comment '收货人姓名';
-- alter table xl_reward_order add column receiver_phone varchar(32) default null comment '收货人电话';
-- alter table xl_reward_order add column receiver_address varchar(255) default null comment '收货地址';

-- 用户收货地址（多地址、可选默认）
drop table if exists xl_user_address;
create table xl_user_address (
  id                bigint(20)      not null auto_increment    comment '主键',
  user_id           bigint(20)      not null                   comment '用户id',
  receiver_name     varchar(64)     not null                   comment '收货人姓名',
  receiver_phone    varchar(32)     not null                   comment '收货人电话',
  receiver_address  varchar(255)    not null                   comment '收货地址',
  is_default        tinyint(1)      default 0                   comment '是否默认 0否 1是',
  create_by         varchar(64)     default ''                   comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                   comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null                comment '备注',
  primary key (id),
  key idx_user (user_id)
) engine=innodb comment = '用户收货地址表';

-- 初始化示例数据
insert into xl_destination (name, region, description, star_reward, create_time) values
('杭州', '华东', '西湖、灵隐、宋城，适合2-3天短途', 2, now()),
('南京', '华东', '夫子庙、中山陵、玄武湖', 2, now()),
('成都', '西南', '宽窄巷子、熊猫基地、火锅', 3, now());

insert into xl_attraction (destination_id, name, ticket_price, description, ai_verified, create_time) values
(1, '西湖', 0, '免费环湖，断桥、雷峰塔', 1, now()),(1, '灵隐寺', 75, '飞来峰+灵隐寺联票', 1, now()),
(2, '夫子庙', 0, '秦淮风光带', 1, now()),(2, '中山陵', 0, '需预约', 1, now()),
(3, '宽窄巷子', 0, '历史文化街区', 1, now()),(3, '熊猫基地', 58, '上午看熊猫更活跃', 1, now());

insert into xl_food (destination_id, name, price_range, description, ai_verified, create_time) values
(1, '楼外楼', '80-150/人', '西湖醋鱼、东坡肉', 1, now()),(1, '知味观', '30-60/人', '小笼、片儿川', 1, now()),
(2, '南京大牌档', '50-80/人', '盐水鸭、美龄粥', 1, now()),(3, '小龙坎火锅', '80-120/人', '成都火锅', 1, now());

insert into xl_accommodation (destination_id, name, type, price_per_night, ai_verified, create_time) values
(1, '西湖边青旅', '青旅', 80, 1, now()),(1, '如家西湖店', '经济型', 200, 1, now()),
(2, '夫子庙附近青旅', '青旅', 70, 1, now()),(3, '春熙路青旅', '青旅', 90, 1, now());

insert into xl_route (destination_id, name, days, total_budget_min, total_budget_max, description, create_time) values
(1, '西湖两日经济线', 2, 400, 600, '住宿青旅+西湖+灵隐+本地小吃', now()),
(2, '南京一日精华', 1, 200, 350, '夫子庙+中山陵+南京大牌档', now()),
(3, '成都两日吃货线', 2, 500, 800, '宽窄巷子+熊猫基地+火锅', now());

insert into xl_route_item (route_id, day_no, item_type, item_id, sort_order, transport_approx, create_time) values
(1, 1, 'attraction', 1, 0, '步行/公交', now()),(1, 1, 'food', 2, 1, '公交', now()),
(1, 2, 'attraction', 2, 0, '公交7路', now()),(1, 2, 'food', 1, 1, '公交', now()),
(2, 1, 'attraction', 3, 0, '地铁', now()),(2, 1, 'attraction', 4, 1, '地铁+观光车', now()),(2, 1, 'food', 3, 2, '地铁', now()),
(3, 1, 'attraction', 5, 0, '地铁', now()),(3, 1, 'food', 4, 1, '步行', now()),(3, 2, 'attraction', 6, 0, '地铁+景区直通车', now());

insert into xl_banner (title, image, link, sort_order, status, create_time) values
('大学生旅游 · 预算规划', '/static/banner1.png', '', 1, '0', now()),
('点亮目的地 赢星星好礼', '/static/banner2.png', '/pages/checkin/index', 2, '0', now()),
('文旅好物 星星兑换', '/static/banner3.png', '/pages/rewards/index', 3, '0', now());

insert into xl_taxi (destination_id, name, app_type, description, price_approx, create_time) values
(1, '西湖景区周边用车', '滴滴/高德', '景区内可叫网约车，建议提前约', '视距离约15-40元', now()),
(2, '南京市区打车', '滴滴/高德/曹操', '夫子庙、中山陵等均可达', '市区约10-30元', now()),
(3, '成都市区及熊猫基地', '滴滴/高德', '宽窄巷子、熊猫基地直通车', '市区约15-25元，基地约30元', now());

insert into xl_ticket (destination_id, attraction_id, name, price, original_price, buy_link, description, create_time) values
(1, 2, '灵隐寺+飞来峰联票', 75, 80, '', '官方或各大平台可购', now()),
(1, 1, '西湖游船', 50, 55, '', '西湖码头现场或线上购', now()),
(2, 4, '中山陵预约', 0, 0, 'https://mp.weixin.qq.com/', '免费需预约', now()),
(3, 6, '熊猫基地门票', 58, 58, '', '建议上午入园', now());

insert into xl_reward_item (name, type, stars_cost, price, description, partner_name, stock, allow_purchase, create_time) values
('杭州文旅帆布包', 'culture_tourism', 10, 29.00, '杭州文旅合作周边', '杭州文旅', 100, 1, now()),
('熊猫明信片套装', 'culture_tourism', 5, 15.00, '成都熊猫基地联名', '成都文旅', 200, 1, now()),
('公益手作香囊', 'public_welfare', 8, 18.00, '乡村妇女手作', '某某公益', 50, 1, now()),
('自营旅行收纳包', 'self_operated', 15, null, '平台自营实用好物', null, 80, 0, now());
