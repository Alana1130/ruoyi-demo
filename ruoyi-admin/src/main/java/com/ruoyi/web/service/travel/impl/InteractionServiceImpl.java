package com.ruoyi.web.service.travel.impl;

import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.mapper.travel.*;
import com.ruoyi.web.service.travel.IInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InteractionServiceImpl implements IInteractionService {

    @Autowired private XlLikeMapper likeMapper;
    @Autowired private XlCommentMapper commentMapper;
    @Autowired private XlDestinationWishMapper destinationWishMapper;
    @Autowired private XlUserCheckinMapper userCheckinMapper;
    @Autowired private XlTravelUserMapper travelUserMapper;

    private static final List<String> VALID_TARGET_TYPES = Arrays.asList("guide", "companion", "idle");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> likeToggle(Long userId, String targetType, Long targetId) {
        Map<String, Object> res = new HashMap<>();
        if (userId == null || !VALID_TARGET_TYPES.contains(targetType) || targetId == null) {
            res.put("liked", false);
            res.put("likeCount", 0);
            return res;
        }
        XlLike existing = likeMapper.selectByUserAndTarget(userId, targetType, targetId);
        if (existing != null) {
            likeMapper.deleteByUserAndTarget(userId, targetType, targetId);
            int count = likeMapper.countByTarget(targetType, targetId);
            res.put("liked", false);
            res.put("likeCount", count);
            return res;
        }
        XlLike like = new XlLike();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        likeMapper.insert(like);
        int count = likeMapper.countByTarget(targetType, targetId);
        res.put("liked", true);
        res.put("likeCount", count);
        return res;
    }

    @Override
    public Map<String, Object> likeInfo(Long userId, String targetType, Long targetId) {
        Map<String, Object> res = new HashMap<>();
        int count = (targetType != null && targetId != null) ? likeMapper.countByTarget(targetType, targetId) : 0;
        res.put("likeCount", count);
        boolean liked = false;
        if (userId != null && targetType != null && targetId != null)
            liked = likeMapper.selectByUserAndTarget(userId, targetType, targetId) != null;
        res.put("liked", liked);
        return res;
    }

    @Override
    public List<Map<String, Object>> listComments(String targetType, Long targetId) {
        if (targetType == null || targetId == null) return Collections.emptyList();
        List<XlComment> list = commentMapper.selectByTarget(targetType, targetId);
        if (list == null) return Collections.emptyList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlComment c : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("userId", c.getUserId());
            m.put("content", c.getContent());
            m.put("parentId", c.getParentId());
            m.put("createTime", c.getCreateTime());
            XlTravelUser u = travelUserMapper.selectById(c.getUserId());
            m.put("nickname", u != null && u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : "用户");
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addComment(Long userId, String targetType, Long targetId, String content, Long parentId) {
        if (userId == null || !VALID_TARGET_TYPES.contains(targetType) || targetId == null || content == null || content.trim().isEmpty())
            return 0;
        XlComment c = new XlComment();
        c.setUserId(userId);
        c.setTargetType(targetType);
        c.setTargetId(targetId);
        c.setContent(content.trim());
        c.setParentId(parentId);
        commentMapper.insert(c);
        return c.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> wantGoToggle(Long userId, Long destinationId) {
        Map<String, Object> res = new HashMap<>();
        if (userId == null || destinationId == null) {
            res.put("wanted", false);
            res.put("wantCount", 0);
            return res;
        }
        XlDestinationWish existing = destinationWishMapper.selectByUserAndDestination(userId, destinationId);
        if (existing != null) {
            destinationWishMapper.deleteByUserAndDestination(userId, destinationId);
            int count = destinationWishMapper.countByDestinationId(destinationId);
            res.put("wanted", false);
            res.put("wantCount", count);
            return res;
        }
        XlDestinationWish w = new XlDestinationWish();
        w.setUserId(userId);
        w.setDestinationId(destinationId);
        destinationWishMapper.insert(w);
        int count = destinationWishMapper.countByDestinationId(destinationId);
        res.put("wanted", true);
        res.put("wantCount", count);
        return res;
    }

    @Override
    public Map<String, Object> destinationStats(Long userId, Long destinationId) {
        Map<String, Object> res = new HashMap<>();
        if (destinationId == null) return res;
        int wantCount = destinationWishMapper.countByDestinationId(destinationId);
        int goneCount = userCheckinMapper.countByDestinationId(destinationId);
        res.put("wantCount", wantCount);
        res.put("goneCount", goneCount);
        boolean userWanted = false;
        boolean userGone = false;
        if (userId != null) {
            userWanted = destinationWishMapper.selectByUserAndDestination(userId, destinationId) != null;
            userGone = userCheckinMapper.selectByUserAndDestination(userId, destinationId) != null;
        }
        res.put("userWanted", userWanted);
        res.put("userGone", userGone);
        return res;
    }
}
