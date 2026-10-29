package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlUserPet;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlUserPetMapper {
    int insert(XlUserPet pet);
    XlUserPet selectById(Long id);
    List<XlUserPet> selectByUserId(Long userId);
    int updateOwner(@Param("id") Long id, @Param("userId") Long userId);
    int updateFeed(@Param("id") Long id, @Param("hunger") Integer hunger, @Param("exp") Integer exp, @Param("level") Integer level, @Param("evolutionStage") Integer evolutionStage);
    int updatePlay(@Param("id") Long id, @Param("mood") Integer mood, @Param("exp") Integer exp, @Param("level") Integer level, @Param("evolutionStage") Integer evolutionStage);
    int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);
    int countByUserAndDestination(@Param("userId") Long userId, @Param("destinationId") Long destinationId);
}
