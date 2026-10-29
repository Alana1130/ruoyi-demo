package com.ruoyi.web.controller.travel;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.service.travel.ITravelAaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/travel/aa")
public class TravelAaController extends BaseController {

    @Autowired
    private ITravelAaService travelAaService;

    @GetMapping("/teams")
    public AjaxResult listTeams() {
        List<XlAaTeam> teams = travelAaService.getAllTeams();
        return success(teams);
    }

    @GetMapping("/teams/{id}")
    public AjaxResult getTeam(@PathVariable Long id) {
        XlAaTeam team = travelAaService.getTeamById(id);
        if (team == null) {
            return error("队伍不存在");
        }
        List<XlAaTeamMember> members = travelAaService.getTeamMembers(id);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("team", team);
        result.put("members", members);
        return success(result);
    }

    @GetMapping("/bills")
    public AjaxResult listBills() {
        List<XlAaBill> bills = travelAaService.getAllBills();
        return success(bills);
    }

    @GetMapping("/bills/{id}")
    public AjaxResult getBill(@PathVariable Long id) {
        XlAaBill bill = travelAaService.getBillById(id);
        if (bill == null) {
            return error("账单不存在");
        }
        List<XlAaExpense> expenses = travelAaService.getBillExpenses(id);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("bill", bill);
        result.put("expenses", expenses);
        return success(result);
    }

    @GetMapping("/expenses")
    public AjaxResult listExpenses() {
        List<XlAaExpense> expenses = travelAaService.getAllExpenses();
        return success(expenses);
    }

    @PostMapping("/bills/{id}/calculate")
    public AjaxResult calculateBill(@PathVariable Long id) {
        Map<String, Object> result = travelAaService.calculateBill(id);
        if (result == null) {
            return error("账单不存在");
        }
        return success(result);
    }

    @PostMapping("/bills/{id}/allocate")
    public AjaxResult generateAllocation(@PathVariable Long id) {
        Map<String, Object> result = travelAaService.generateBillAllocation(id);
        if (result == null) {
            return error("账单不存在");
        }
        return success(result);
    }

    @GetMapping("/teams/{id}/statistics")
    public AjaxResult getTeamStatistics(@PathVariable Long id) {
        Map<String, Object> statistics = travelAaService.getTeamStatistics(id);
        if (statistics == null) {
            return error("队伍不存在");
        }
        return success(statistics);
    }

    @GetMapping("/bills/{id}/export")
    public AjaxResult exportBill(@PathVariable Long id) {
        Map<String, Object> result = travelAaService.exportBill(id);
        if (result == null) {
            return error("账单不存在");
        }
        return success(result);
    }
}
