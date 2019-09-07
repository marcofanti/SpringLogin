package com.behaviosec.login.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class BehavioSecAuthenticationFilter extends GenericFilterBean {

    public BehavioSecAuthenticationFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
        throws IOException, ServletException {
    	
        filterChain.doFilter(req, res);
    }

}
