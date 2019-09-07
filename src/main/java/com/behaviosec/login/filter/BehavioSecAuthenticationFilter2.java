package com.behaviosec.login.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BehavioSecAuthenticationFilter2 extends GenericFilterBean {

    public BehavioSecAuthenticationFilter2() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
        throws IOException, ServletException {
    	
		String redirection = "";
		
		System.out.println("\nAttributes\n");
		Iterator <String> itr = req.getAttributeNames().asIterator();

		while (itr.hasNext()) {
			Object attribute = itr.next();
			System.out.println(attribute + " " + req.getAttribute(attribute.toString()));
		}
		System.out.println("\nHeaders\n");
/*
		itr = req.getHeaderNames().asIterator();

		String userAgent = "";

		while (itr.hasNext()) {
			Object header = itr.next();
			String headerValue = req.getHeader(header.toString());
			System.out.println(header + " " + headerValue);
			if (header.equals("user-agent")) {
				userAgent = headerValue;
			}
		} */
		System.out.println("\nParameters\n");
		
		itr = req.getParameterNames().asIterator();
		
		String timingData = "";
		String userName = "";
		String ip = "";
		while (itr.hasNext()) {
			Object parameters = itr.next();
			if (parameters.equals("hidden1")) {
				timingData = req.getParameter(parameters.toString());
			} else if (parameters.equals("username")) {
				userName = req.getParameter(parameters.toString());
			} 
			logger.info(parameters + " " + req.getParameter(parameters.toString()));
		}
		
		List<String> roles = new ArrayList<String>();
		
		//utils.checkData(userName, userAgent, ip, timingData);

    	req.getAttributeNames().toString();
        filterChain.doFilter(req, res);
    }

}
