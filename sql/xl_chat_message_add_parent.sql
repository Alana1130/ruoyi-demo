-- 讨论支持回复（楼中楼）
ALTER TABLE xl_chat_message ADD COLUMN parent_id bigint DEFAULT NULL COMMENT '回复的留言id' AFTER user_id;
ALTER TABLE xl_chat_message ADD KEY idx_parent (parent_id);
