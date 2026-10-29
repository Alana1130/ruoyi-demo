package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlAaBill;

import java.util.List;

/**
 * AA记账账单Mapper
 */
public interface XlAaBillMapper {
    int insert(XlAaBill bill);
    XlAaBill selectById(Long id);
    List<XlAaBill> selectByTeamId(Long teamId);
    int update(XlAaBill bill);
    int deleteById(Long id);
    List<XlAaBill> selectAll();
}
