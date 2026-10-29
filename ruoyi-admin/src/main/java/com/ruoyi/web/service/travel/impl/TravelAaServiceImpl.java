package com.ruoyi.web.service.travel.impl;

import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.mapper.travel.*;
import com.ruoyi.web.service.travel.ITravelAaService;
import com.ruoyi.web.service.travel.ITravelMiniappService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TravelAaServiceImpl implements ITravelAaService {

    @Autowired private XlAaTeamMapper teamMapper;
    @Autowired private XlAaTeamMemberMapper memberMapper;
    @Autowired private XlAaBillMapper billMapper;
    @Autowired private XlAaExpenseMapper expenseMapper;
    @Autowired private XlAaBillAllocationMapper allocationMapper;
    @Autowired private ITravelMiniappService miniappService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XlAaTeam createTeam(Long userId, String teamName, String travelName) {
        XlTravelUser user = miniappService.getUserById(userId);
        if (user == null) return null;

        XlAaTeam team = new XlAaTeam();
        team.setName(teamName);
        team.setTravelName(travelName);
        team.setCreatorId(userId);
        team.setStatus(0);
        team.setJoinCode(IdUtils.fastUUID().substring(0, 8));
        teamMapper.insert(team);

        XlAaTeamMember member = new XlAaTeamMember();
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setNickname(user.getNickname());
        member.setAvatar(user.getAvatar());
        member.setRole(1);
        member.setStatus(1);
        memberMapper.insert(member);

        return team;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XlAaTeam joinTeam(Long userId, String joinCode) {
        XlAaTeam team = teamMapper.selectByJoinCode(joinCode);
        if (team == null) return null;

        XlTravelUser user = miniappService.getUserById(userId);
        if (user == null) return null;

        List<XlAaTeamMember> existingMembers = memberMapper.selectByTeamId(team.getId());
        for (XlAaTeamMember member : existingMembers) {
            if (member.getUserId().equals(userId)) {
                return team;
            }
        }

        XlAaTeamMember member = new XlAaTeamMember();
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setNickname(user.getNickname());
        member.setAvatar(user.getAvatar());
        member.setRole(0);
        member.setStatus(1);
        memberMapper.insert(member);

        return team;
    }

    @Override
    public List<XlAaTeam> getMyTeams(Long userId) {
        return teamMapper.selectByUserId(userId);
    }

    @Override
    public XlAaTeam getTeamById(Long teamId) {
        return teamMapper.selectById(teamId);
    }

    @Override
    public List<XlAaTeamMember> getTeamMembers(Long teamId) {
        return memberMapper.selectByTeamId(teamId);
    }

    @Override
    public boolean updateTeamStatus(Long teamId, Integer status) {
        XlAaTeam team = teamMapper.selectById(teamId);
        if (team == null) return false;
        team.setStatus(status);
        return teamMapper.update(team) > 0;
    }

    @Override
    public boolean leaveTeam(Long teamId, Long userId) {
        List<XlAaTeamMember> members = memberMapper.selectByTeamId(teamId);
        if (members.size() <= 1) return false;

        for (XlAaTeamMember member : members) {
            if (member.getUserId().equals(userId)) {
                return memberMapper.deleteById(member.getId()) > 0;
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XlAaBill createBill(Long teamId, Long userId, String billName) {
        XlAaTeam team = teamMapper.selectById(teamId);
        if (team == null) return null;

        XlAaBill bill = new XlAaBill();
        bill.setTeamId(teamId);
        bill.setName(billName);
        bill.setTotalAmount(BigDecimal.ZERO);
        bill.setMemberCount(memberMapper.selectByTeamId(teamId).size());
        bill.setPerPersonAmount(BigDecimal.ZERO);
        bill.setStatus(0);
        bill.setCreatorId(userId);
        billMapper.insert(bill);

        return bill;
    }

    @Override
    public XlAaBill getBillById(Long billId) {
        return billMapper.selectById(billId);
    }

    @Override
    public List<XlAaBill> getTeamBills(Long teamId) {
        return billMapper.selectByTeamId(teamId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XlAaExpense addExpense(Long billId, Long teamId, Long userId, String expenseName, BigDecimal amount, String category, String description, String paymentMethod) {
        XlAaBill bill = billMapper.selectById(billId);
        if (bill == null) return null;

        XlAaExpense expense = new XlAaExpense();
        expense.setBillId(billId);
        expense.setTeamId(teamId);
        expense.setUserId(userId);
        expense.setExpenseName(expenseName);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setPaymentMethod(paymentMethod);
        expenseMapper.insert(expense);

        List<XlAaExpense> expenses = expenseMapper.selectByBillId(billId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (XlAaExpense e : expenses) {
            totalAmount = totalAmount.add(e.getAmount());
        }
        bill.setTotalAmount(totalAmount);
        int memberCount = memberMapper.selectByTeamId(teamId).size();
        bill.setPerPersonAmount(totalAmount.divide(BigDecimal.valueOf(memberCount), 2, BigDecimal.ROUND_HALF_UP));
        billMapper.update(bill);

        return expense;
    }

    @Override
    public List<XlAaExpense> getBillExpenses(Long billId) {
        return expenseMapper.selectByBillId(billId);
    }

    @Override
    public List<XlAaExpense> getTeamExpenses(Long teamId) {
        return expenseMapper.selectByTeamId(teamId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> calculateBill(Long billId) {
        XlAaBill bill = billMapper.selectById(billId);
        if (bill == null) return null;

        List<XlAaTeamMember> members = memberMapper.selectByTeamId(bill.getTeamId());
        List<XlAaExpense> expenses = expenseMapper.selectByBillId(billId);

        Map<Long, BigDecimal> paymentMap = new HashMap<>();
        for (XlAaExpense expense : expenses) {
            paymentMap.put(expense.getUserId(), paymentMap.getOrDefault(expense.getUserId(), BigDecimal.ZERO).add(expense.getAmount()));
        }

        BigDecimal perPerson = bill.getPerPersonAmount();
        Map<Long, BigDecimal> balanceMap = new HashMap<>();
        for (XlAaTeamMember member : members) {
            BigDecimal paid = paymentMap.getOrDefault(member.getUserId(), BigDecimal.ZERO);
            balanceMap.put(member.getUserId(), paid.subtract(perPerson));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bill", bill);
        result.put("members", members);
        result.put("expenses", expenses);
        result.put("paymentMap", paymentMap);
        result.put("balanceMap", balanceMap);
        result.put("perPerson", perPerson);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> generateBillAllocation(Long billId) {
        XlAaBill bill = billMapper.selectById(billId);
        if (bill == null) return null;

        List<XlAaTeamMember> members = memberMapper.selectByTeamId(bill.getTeamId());
        BigDecimal totalAmount = bill.getTotalAmount();
        BigDecimal perPerson = bill.getPerPersonAmount();

        allocationMapper.deleteByBillId(billId);

        BigDecimal totalAllocated = perPerson.multiply(BigDecimal.valueOf(members.size()));
        BigDecimal difference = totalAmount.subtract(totalAllocated);

        Long captainId = null;
        for (XlAaTeamMember member : members) {
            if (member.getRole() == 1) {
                captainId = member.getUserId();
                break;
            }
        }

        List<XlAaBillAllocation> allocations = new ArrayList<>();
        for (XlAaTeamMember member : members) {
            XlAaBillAllocation allocation = new XlAaBillAllocation();
            allocation.setBillId(billId);
            allocation.setUserId(member.getUserId());
            if (member.getUserId().equals(captainId)) {
                allocation.setAmount(perPerson.add(difference));
            } else {
                allocation.setAmount(perPerson);
            }
            allocation.setPaidAmount(BigDecimal.ZERO);
            allocation.setStatus(0);
            allocationMapper.insert(allocation);
            allocations.add(allocation);
        }

        bill.setStatus(1);
        billMapper.update(bill);

        Map<String, Object> result = new HashMap<>();
        result.put("bill", bill);
        result.put("allocations", allocations);
        result.put("perPerson", perPerson);
        result.put("difference", difference);

        return result;
    }

    @Override
    public boolean payBillAllocation(Long allocationId, BigDecimal amount) {
        XlAaBillAllocation allocation = allocationMapper.selectById(allocationId);
        if (allocation == null) return false;

        allocation.setPaidAmount(amount);
        allocation.setStatus(1);
        return allocationMapper.update(allocation) > 0;
    }

    @Override
    public Map<String, Object> getTeamStatistics(Long teamId) {
        XlAaTeam team = teamMapper.selectById(teamId);
        if (team == null) return null;

        List<XlAaBill> bills = billMapper.selectByTeamId(teamId);
        List<XlAaExpense> expenses = expenseMapper.selectByTeamId(teamId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryMap = new HashMap<>();

        for (XlAaExpense expense : expenses) {
            totalAmount = totalAmount.add(expense.getAmount());
            categoryMap.put(expense.getCategory(), categoryMap.getOrDefault(expense.getCategory(), BigDecimal.ZERO).add(expense.getAmount()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("team", team);
        result.put("totalAmount", totalAmount);
        result.put("billCount", bills.size());
        result.put("expenseCount", expenses.size());
        result.put("categoryMap", categoryMap);

        return result;
    }

    @Override
    public Map<String, Object> exportBill(Long billId) {
        XlAaBill bill = billMapper.selectById(billId);
        if (bill == null) return null;

        List<XlAaExpense> expenses = expenseMapper.selectByBillId(billId);
        List<XlAaBillAllocation> allocations = allocationMapper.selectByBillId(billId);

        Map<String, Object> result = new HashMap<>();
        result.put("bill", bill);
        result.put("expenses", expenses);
        result.put("allocations", allocations);
        result.put("exportUrl", "https://xinglv.com/export/bill/" + billId);

        return result;
    }

    @Override
    public List<XlAaTeam> getAllTeams() {
        return teamMapper.selectAll();
    }

    @Override
    public List<XlAaBill> getAllBills() {
        return billMapper.selectAll();
    }

    @Override
    public List<XlAaExpense> getAllExpenses() {
        return expenseMapper.selectAll();
    }
}
