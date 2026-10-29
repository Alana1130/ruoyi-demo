-- 约伴/组队 帖子表
drop table if exists xl_companion_post;
create table xl_companion_post (
  id                  bigint(20)      not null auto_increment    comment '主键',
  user_id             bigint(20)      not null                   comment '发布用户id',
  destination_id      bigint(20)      default null               comment '目的地id（可选）',
  title               varchar(128)   not null                   comment '标题',
  content             varchar(1000)  default ''                  comment '内容说明',
  travel_date_start   date            default null                comment '出发日期起',
  travel_date_end     date            default null                comment '出发日期止',
  budget_range        varchar(64)     default ''                  comment '预算区间如 500-1000',
  contact_remark       varchar(128)   default ''                  comment '联系方式说明',
  status              char(1)         default '0'                 comment '0开放 1已关闭',
  create_by           varchar(64)    default ''                  comment '创建者',
  create_time         datetime                                   comment '创建时间',
  update_by           varchar(64)    default ''                  comment '更新者',
  update_time         datetime                                   comment '更新时间',
  remark              varchar(500)   default null                comment '备注',
  primary key (id),
  key idx_user (user_id),
  key idx_dest (destination_id),
  key idx_dates (travel_date_start, travel_date_end),
  key idx_status (status)
) engine=innodb comment = '约伴/组队帖子表';
