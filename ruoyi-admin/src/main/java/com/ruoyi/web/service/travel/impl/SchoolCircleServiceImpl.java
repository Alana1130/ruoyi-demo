package com.ruoyi.web.service.travel.impl;

import com.ruoyi.web.domain.travel.XlSchool;
import com.ruoyi.web.domain.travel.XlSchoolPost;
import com.ruoyi.web.domain.travel.XlTravelUser;
import com.ruoyi.web.mapper.travel.XlSchoolMapper;
import com.ruoyi.web.mapper.travel.XlSchoolPostMapper;
import com.ruoyi.web.mapper.travel.XlTravelUserMapper;
import com.ruoyi.web.service.travel.ISchoolCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchoolCircleServiceImpl implements ISchoolCircleService {

    @Autowired
    private XlSchoolMapper schoolMapper;
    @Autowired
    private XlSchoolPostMapper schoolPostMapper;
    @Autowired
    private XlTravelUserMapper travelUserMapper;

    @Override
    public List<XlSchool> listSchools() {
        return schoolMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifySchool(Long userId, Long schoolId) {
        if (userId == null || schoolId == null) return false;
        if (schoolMapper.selectById(schoolId) == null) return false;
        return travelUserMapper.updateSchoolVerify(userId, schoolId) > 0;
    }

    @Override
    public Map<String, Object> getMySchool(Long userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("schoolVerified", 0);
        res.put("schoolId", null);
        res.put("schoolName", null);
        if (userId == null) return res;
        XlTravelUser user = travelUserMapper.selectById(userId);
        if (user == null || user.getSchoolVerified() == null || user.getSchoolVerified() != 1) return res;
        res.put("schoolVerified", 1);
        res.put("schoolId", user.getSchoolId());
        XlSchool school = user.getSchoolId() != null ? schoolMapper.selectById(user.getSchoolId()) : null;
        res.put("schoolName", school != null ? school.getName() : null);
        return res;
    }

    @Override
    public List<XlSchoolPost> listPosts(Long schoolId, int page, int pageSize) {
        if (schoolId == null) return Collections.emptyList();
        int size = Math.min(Math.max(1, pageSize), 50);
        int offset = Math.max(0, page) * size;
        return schoolPostMapper.selectBySchoolId(schoolId, size, offset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addPost(Long userId, Long schoolId, String content, String images) {
        if (userId == null || schoolId == null || content == null || content.trim().isEmpty()) return 0;
        XlTravelUser user = travelUserMapper.selectById(userId);
        if (user == null || user.getSchoolVerified() == null || user.getSchoolVerified() != 1
            || !schoolId.equals(user.getSchoolId())) return 0;
        XlSchoolPost post = new XlSchoolPost();
        post.setSchoolId(schoolId);
        post.setUserId(userId);
        post.setContent(content.trim());
        post.setImages(images != null ? images.trim() : "");
        schoolPostMapper.insert(post);
        return post.getId();
    }
}
