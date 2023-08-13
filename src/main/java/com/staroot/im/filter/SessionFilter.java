package com.staroot.im.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SessionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String[] whiteList = {"/h2-console/*",
                "/login",
                "/logout",
                "/register",
                "/css/*",
                "/images/*",
                "/social/*",
                "/favicon.ico",
                "/net/*",
                "/telegram/*",
                "/gauth/*"};
        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession(false); // 새 세션 생성을 막고 이미 존재하는 세션을 가져옴
        if(!PatternMatchUtils.simpleMatch(whiteList, requestURI)){
            logger.debug("requestURI(chk) : "+requestURI);
            if (session == null || session.getAttribute("userid") == null) {
                logger.debug("No session");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }else{
            logger.debug("requestURI(no chk) : "+requestURI);
        }


        filterChain.doFilter(request, response);
    }
}