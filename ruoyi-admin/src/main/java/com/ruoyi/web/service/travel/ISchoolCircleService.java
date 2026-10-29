package com.ruoyi.web.service.travel;

import com.ruoyi.web.domain.travel.XlSchool;
import com.ruoyi.web.domain.travel.XlSchoolPost;

import java.util.List;
import java.util.Map;

/**
 * 学校圈：学校列表、认证、帖子
 */
public interface ISchoolCircleService {
    List<XlSchool> listSchools();

    /** 当前用户进行学校认证（选择学校即视为认证通过，简化流程） */
    boolean verifySchool(Long userId, Long schoolId);

    /** 当前用户认证信息：schoolId, schoolName, schoolVerified */
    Map<String, Object> getMySchool(Long userId);

    /** 某学校圈帖子列表，按时间倒序 */
    List<XlSchoolPost> listPosts(Long schoolId, int page, int pageSize);

    /** 发帖：仅认证用户且学校一致可发 */
    long addPost(Long userId, Long schoolId, String content, String images);
}
