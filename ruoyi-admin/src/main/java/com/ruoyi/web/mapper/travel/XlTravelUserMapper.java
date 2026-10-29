package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlTravelUser;
import org.apache.ibatis.annotations.Param;

/**
 * 旅游用户 数据层
 */
public interface XlTravelUserMapper {
    XlTravelUser selectById(Long id);
    XlTravelUser selectByOpenid(String openid);
    XlTravelUser selectByPhone(String phone);
    int insert(XlTravelUser user);
    int updateStars(@Param("id") Long id, @Param("addStars") int addStars);
    int updateProfile(XlTravelUser user);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    int updatePhoneAndPassword(@Param("id") Long id, @Param("phone") String phone, @Param("password") String password);
    int updateSchoolVerify(@Param("id") Long id, @Param("schoolId") Long schoolId);
}
