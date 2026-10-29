package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlAaTeamMember;

import java.util.List;

/**
 * AA记账队伍成员Mapper
 */
public interface XlAaTeamMemberMapper {
    int insert(XlAaTeamMember member);
    XlAaTeamMember selectById(Long id);
    List<XlAaTeamMember> selectByTeamId(Long teamId);
    List<XlAaTeamMember> selectByUserId(Long userId);
    int update(XlAaTeamMember member);
    int deleteById(Long id);
    int deleteByTeamId(Long teamId);
}
