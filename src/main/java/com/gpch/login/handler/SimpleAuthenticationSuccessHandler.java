package com.gpch.login.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.behaviosec.login.utils.Utils;
import com.gpch.login.model.User;
import com.gpch.login.service.UserService;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private static Utils utils = new Utils();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private static final String TAG = SimpleAuthenticationSuccessHandler.class.getName();
    private final Logger logger = LoggerFactory.getLogger(TAG);
    
    @Autowired
    private UserService userService;


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
			if (header.equals("user-agent")) {
				userAgent = headerValue;
			}
		}
		System.out.println("\nParameters\n");
		
		itr = arg0.getParameterNames().asIterator();
		
		String timingData = "";
		String userName = "";
		String ip = "1.1.1.1";
		while (itr.hasNext()) {
			Object parameters = itr.next();
			if (parameters.equals("bdata") || parameters.equals("other")) {
				timingData = arg0.getParameter(parameters.toString());
			} else if (parameters.equals("username")) {
				userName = arg0.getParameter(parameters.toString());
			} 
			logger.info(parameters + " " + arg0.getParameter(parameters.toString()));
		}
		
        if (timingData != null && timingData.trim().length() > 0) {
        	int indexOf = timingData.indexOf("::");
        	if (indexOf > 0) {
        		userAgent = timingData.substring(0, indexOf);
        		timingData = timingData.substring(indexOf + 2);
        	}
        }

		List<String> roles = new ArrayList<String>();
		
		String result = utils.checkData(userName, userAgent, ip, timingData);
		
        User user = userService.findUserByUsername(userName);
        
//        user.setOther(result);
        userService.updateUser(user, result);

		logger.info("\n\n");
		logger.info("\nRedirection " + redirection);
		try {
			redirectStrategy.sendRedirect(arg0, arg1, "/admin/home");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}