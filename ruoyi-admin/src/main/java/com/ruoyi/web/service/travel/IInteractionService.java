package com.ruoyi.web.service.travel;

import java.util.List;
import java.util.Map;

/** 互动：点赞、评论、想去/去过打卡 */
public interface IInteractionService {

    /** 点赞/取消点赞，targetType: guide/companion/idle */
    Map<String, Object> likeToggle(Long userId, String targetType, Long targetId);

    /** 获取点赞数与当前用户是否已赞 */
    Map<String, Object> likeInfo(Long userId, String targetType, Long targetId);

    /** 评论列表（含回复、昵称） */
    List<Map<String, Object>> listComments(String targetType, Long targetId);

    /** 发表评论，parentId 可选（回复时传） */
    long addComment(Long userId, String targetType, Long targetId, String content, Long parentId);

    /** 想去/取消想去（目的地） */
    Map<String, Object> wantGoToggle(Long userId, Long destinationId);

    /** 目的地互动统计：想去人数、去过人数、当前用户是否想去、是否去过 */
    Map<String, Object> destinationStats(Long userId, Long destinationId);
}
