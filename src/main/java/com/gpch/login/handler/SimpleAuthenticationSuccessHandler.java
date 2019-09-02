package com.gpch.login.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

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
			}
		}
		System.out.println("\nParameters\n");
		
		itr = arg0.getParameterNames().asIterator();
		
		while (itr.hasNext()) {
			Object parameters = itr.next();
			System.out.println(parameters + " " + arg0.getParameter(parameters.toString()));
		}
		System.out.println("\n\n");
		System.out.println("\nRedirection " + redirection);

		try {
			if (redirection != null && redirection.trim().length() > 0) {
				redirectStrategy.sendRedirect(arg0, arg1, redirection);
			} else {
				redirectStrategy.sendRedirect(arg0, arg1, "/admin/home");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}