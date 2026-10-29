package com.ruoyi.web.filter;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 管理端AA记账接口权限验证过滤器
 * 只对 /travel/aa/** 路径生效，使用若依默认的登录和权限验证机制
 */
public class TravelAdminAuthFilter implements Filter {

    private final TokenService tokenService;

    public TravelAdminAuthFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取请求路径
        String path = httpRequest.getRequestURI();
        System.out.println("[TravelAdminAuthFilter] 处理请求: " + path);
        
        // 只处理/travel/aa/**路径的请求，其他路径直接放行
        if (!path.startsWith("/travel/aa/")) {
            System.out.println("[TravelAdminAuthFilter] 放行非/travel/aa/路径: " + path);
            chain.doFilter(request, response);
            return;
        }
        
        System.out.println("[TravelAdminAuthFilter] 验证/travel/aa/路径的权限: " + path);
        
        // 验证token（使用若依默认的token验证机制）
        LoginUser loginUser = tokenService.getLoginUser(httpRequest);
        if (loginUser == null) {
            System.out.println("[TravelAdminAuthFilter] token验证失败，返回401");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = httpResponse.getWriter();
            try {
                ObjectMapper mapper = new ObjectMapper();
                writer.write(mapper.writeValueAsString(AjaxResult.error(401, "未授权，请重新登录")));
            } catch (Exception e) {
                e.printStackTrace();
                writer.write("{\"code\":401,\"msg\":\"未授权，请重新登录\"}");
            }
            writer.flush();
            writer.close();
            return;
        }
        
        System.out.println("[TravelAdminAuthFilter] 权限验证成功，用户: " + loginUser.getUsername());
        chain.doFilter(request, response);
    }
}
