package com.behaviosec.login.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Iterator;

public class BehavioSecAuthenticationFilter extends GenericFilterBean {

    public BehavioSecAuthenticationFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
        throws IOException, ServletException {
		
		Iterator itr = req.getParameterNames().asIterator();
		
		while (itr.hasNext()) {
			Object parameters = itr.next();
			if (parameters.equals("SAMLResponse")) {
				logger.info(parameters + " " + req.getParameter(parameters.toString()));				
			} 
		}
		
        filterChain.doFilter(req, res);
    }

}
