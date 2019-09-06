package com.gpch.login.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.behaviosec.login.security.jwt.JwtTokenProvider;
import com.behaviosec.login.utils.Utils;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private static Utils utils = new Utils();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private static final String TAG = SimpleAuthenticationSuccessHandler.class.getName();
    private final Logger logger = LoggerFactory.getLogger(TAG);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest arg0, HttpServletResponse arg1,
			Authentication authentication) throws IOException, ServletException {

		String redirection = "";
		
		System.out.println("\nAttributes\n");
		Iterator <String> itr = arg0.getAttributeNames().asIterator();

		while (itr.hasNext()) {
			Object attribute = itr.next();
			System.out.println(attribute + " " + arg0.getAttribute(attribute.toString()));
		}
		System.out.println("\nHeaders\n");

		itr = arg0.getHeaderNames().asIterator();

		String userAgent = "";

		while (itr.hasNext()) {
			Object header = itr.next();
			String headerValue = arg0.getHeader(header.toString());
			System.out.println(header + " " + headerValue);
			if (header.toString().toLowerCase().equals("referer")) {
				int index = headerValue.indexOf("redirection=");
				if (index > 0) {
				//referer http://marcos-macbook-pro.local:9876/?redirection=http://http://marcos-macbook-pro.local:9876/damin/home
					redirection = headerValue.substring(index + "redirection=".length());
				}
			} else if (header.equals("user-agent")) {
				userAgent = headerValue;
			}
		}
		System.out.println("\nParameters\n");
		
		itr = arg0.getParameterNames().asIterator();
		
		String timingData = "";
		String userName = "";
		String ip = "";
		while (itr.hasNext()) {
			Object parameters = itr.next();
			if (parameters.equals("hidden1")) {
				timingData = arg0.getParameter(parameters.toString());
			} else if (parameters.equals("email")) {
				userName = arg0.getParameter(parameters.toString());
			} 
			logger.info(parameters + " " + arg0.getParameter(parameters.toString()));
		}
		
		List<String> roles = new ArrayList<String>();

		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
		String token = jwtTokenProvider.createToken(userName, roles);
		
		logger.error("Token = " + token);
		
		utils.checkData(userName, userAgent, ip, timingData);
		logger.info("\n\n");
		logger.info("\nRedirection " + redirection);
		try {
			if (redirection != null && redirection.trim().length() > 0) {
				Cookie cookie = new Cookie("token", token);
				arg1.addCookie(cookie);
				redirectStrategy.sendRedirect(arg0, arg1, redirection + "?" + token);
			} else {
				redirectStrategy.sendRedirect(arg0, arg1, "/admin/home");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}