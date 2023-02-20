package com.cai.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.cai.reggie.common.BaseContext;
import com.cai.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg",
                "/reggie_take_out/front/page/login.html",
                "/favicon.ico"
        };
        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要处理直接放行
        if (check){
            log.info("本次请求:{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4.1如果需要处理，则判断是否已经登录，是则放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已经登录，用户id为:{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //4.2这是判断移动端的如果需要处理，则判断是否已经登录，是则放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已经登录，用户id为:{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //如果没登录则进行监听，通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，判断是否可以放行
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
