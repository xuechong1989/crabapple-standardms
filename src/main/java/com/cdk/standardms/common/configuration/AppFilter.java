package com.cdk.standardms.common.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.servlet.ServletUtil;
import xc.crabapple.ssm.spring.SpringUtil;
import xc.crabapple.ssm.spring.SsmFilter;
import xc.crabapple.ssm.spring.SsmRequestUtil;
import xc.crabapple.ssm.spring.SsmRequestWrapper;

/**
 * xuechong 2025-02-11
 */
@Component
public class AppFilter extends SsmFilter {

    /**
     * xuechong 2025-02-11
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try{
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            SsmRequestWrapper request = SsmRequestUtil.getMyHttpServletRequestWrapper(httpServletRequest, response);
            SpringUtil.setRequestResponse(request, response);
            //
            final String requestURI = request.getRequestURI();
            //
            if("/favicon.ico".equals(requestURI)){
                return;
            }
            //
            String method = httpServletRequest.getMethod();
            method = StringUtil.toUpperCase(method);
            if(StringUtil.containOfOne(method, "OPTIONS")){
                filterChain.doFilter(request, response);
                return;
            }
            //
            String requestStatus = request.getStatus();
            if(!"success".equals(requestStatus)){
                LogUtil.error("AppFilter_doFilter_1", "系统可能被非正常访问", requestURI, requestStatus);
                AppResponseBodyAdvice.sendJson2client(false, requestStatus, "系统可能被非正常访问");
                return;
            }
            //
            if(true){
                filterChain.doFilter(request, response);
            }
        }catch (Throwable throwable){
            final String requestURI = ServletUtil.getRequestURI();
            LogUtil.error("AppFilter_doFilter_t1", throwable, "过滤器异常", requestURI);
            AppResponseBodyAdvice.sendJson2client(false, "filter_exception", "系统可能被非正常访问");
        }
    }

}