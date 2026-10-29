package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlAaExpense;

import java.util.List;

/**
 * AA记账消费明细Mapper
 */
public interface XlAaExpenseMapper {
    int insert(XlAaExpense expense);
    XlAaExpense selectById(Long id);
    List<XlAaExpense> selectByBillId(Long billId);
    List<XlAaExpense> selectByTeamId(Long teamId);
    int update(XlAaExpense expense);
    int deleteById(Long id);
    List<XlAaExpense> selectAll();
}
