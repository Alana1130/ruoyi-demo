package com.ruoyi.web.service.travel;

import com.ruoyi.web.domain.travel.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * AA记账服务接口
 */
public interface ITravelAaService {
    // 队伍管理
    XlAaTeam createTeam(Long userId, String teamName, String travelName);
    XlAaTeam joinTeam(Long userId, String joinCode);
    List<XlAaTeam> getMyTeams(Long userId);
    XlAaTeam getTeamById(Long teamId);
    List<XlAaTeamMember> getTeamMembers(Long teamId);
    boolean updateTeamStatus(Long teamId, Integer status);
    boolean leaveTeam(Long teamId, Long userId);

    // 账单管理
    XlAaBill createBill(Long teamId, Long userId, String billName);
    XlAaBill getBillById(Long billId);
    List<XlAaBill> getTeamBills(Long teamId);

    // 消费管理
    XlAaExpense addExpense(Long billId, Long teamId, Long userId, String expenseName, BigDecimal amount, String category, String description, String paymentMethod);
    List<XlAaExpense> getBillExpenses(Long billId);
    List<XlAaExpense> getTeamExpenses(Long teamId);

    // 账单结算
    Map<String, Object> calculateBill(Long billId);
    Map<String, Object> generateBillAllocation(Long billId);
    boolean payBillAllocation(Long allocationId, BigDecimal amount);

    // 统计和导出
    Map<String, Object> getTeamStatistics(Long teamId);
    Map<String, Object> exportBill(Long billId);
    
    // 管理端方法
    List<XlAaTeam> getAllTeams();
    List<XlAaBill> getAllBills();
    List<XlAaExpense> getAllExpenses();
}
