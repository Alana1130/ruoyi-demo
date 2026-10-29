package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlAaTeam;
import com.ruoyi.web.domain.travel.XlAaTeamMember;

import java.util.List;

/**
 * AA记账队伍Mapper
 */
public interface XlAaTeamMapper {
    int insert(XlAaTeam team);
    XlAaTeam selectById(Long id);
    XlAaTeam selectByJoinCode(String joinCode);
    int update(XlAaTeam team);
    int deleteById(Long id);
    List<XlAaTeam> selectByUserId(Long userId);
    List<XlAaTeam> selectAll();
}
