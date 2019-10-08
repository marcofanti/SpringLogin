package com.behaviosec.handler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.behaviosec.config.AppServiceConfig;
import com.behaviosec.config.BehavioSecException;
import com.behaviosec.config.Constants;
import com.behaviosec.entities.ReportRequest;
import com.behaviosec.entities.Response;
import com.behaviosec.model.User;
import com.behaviosec.service.UserService;
import com.behaviosec.utils.Helper;

import lombok.NonNull;

import com.behaviosec.config.Constants;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Value( "${behaviosec.behaviosecurl}" )
	@NonNull public String behaviosecurl;
	@Value( "${behaviosec.tenantId}" )
	public String tenantId;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private static final String TAG = SimpleAuthenticationSuccessHandler.class.getName();
    private final Logger log = LoggerFactory.getLogger(TAG);
    
    @Autowired
    private UserService userService;


	@Override
	public void onAuthenticationSuccess(HttpServletRequest arg0, HttpServletResponse arg1,
			Authentication authentication) throws IOException, ServletException {

		String redirection = "/home/homepage";
	
		Iterator <String> itr = arg0.getAttributeNames().asIterator();

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
		
		itr = arg0.getParameterNames().asIterator();
		
		String timingData = "";
		String userName = "";
		String clientIp = Helper.getClientIpAddress(arg0);
		
		while (itr.hasNext()) {
			Object parameters = itr.next();
			if (parameters.equals("bdata") || parameters.equals("other")) {
				timingData = arg0.getParameter(parameters.toString());
			} else if (parameters.equals("username")) {
				userName = arg0.getParameter(parameters.toString());
			} 
			log.info(parameters + " " + arg0.getParameter(parameters.toString()));
		}
		
        if (timingData != null && timingData.trim().length() > 0) {
        	int indexOf = timingData.indexOf("::");
        	if (indexOf > 0) {
        		userAgent = timingData.substring(0, indexOf);
        		timingData = timingData.substring(indexOf + 2);
        	}

			Response r = getResponse(behaviosecurl, tenantId, 
					clientIp, userAgent, userName, timingData, log);
			
	        User user = userService.findUserByUsername(userName);
	        
	        if(r.hasReport()){
	        	String report = r.getReponseString();
	        	userService.updateUser(user, report);
	        }
	
        }

		try {
			//if (!trained && !user.getUsername().startsWith("tr")) {
			//	redirectStrategy.sendRedirect(arg0, arg1, "/admin/home");
			//	//redirectStrategy.sendRedirect(arg0, arg1, "/admin/loginOTP");				
			//} else {
			log.info("Redirection " + redirection);		
			redirectStrategy.sendRedirect(arg0, arg1, redirection);
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static Response getResponse(String url, String tenantID, String clientIp, String agent, String userName, String bdata, Logger log) {
        Response r = null;
		com.behaviosec.client.ClientConfiguration clientConfig =
                new com.behaviosec.client.ClientConfiguration(
                        url,
                        tenantID);
        com.behaviosec.client.Client client = new com.behaviosec.client.Client(clientConfig);
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setUserIp(clientIp);
        reportRequest.setSessionId(UUID.randomUUID().toString());
        reportRequest.setUserAgent(agent);
        reportRequest.setUsername(userName);
        reportRequest.setTimingData(bdata);
        reportRequest.setOperatorFlags(Constants.FINALIZE_DIRECTLY);
	    log.info("Calling checkdata " + userName + " " + agent + " " + bdata.substring(0, 20) + "...");
        try {
            r = client.getReport(reportRequest);
        } catch (BehavioSecException e) {
            e.printStackTrace();
        }
        return r;
	}

    public void addToDB(String dbURL, String dbUser, String dbPassword, String report, String user) {
    	Connection con = null;
    	dbURL = dbURL + "&user=" + dbUser + "&password=" + dbPassword;
    	PreparedStatement stmt = null;
		try {
			log.debug("Before connection");
			con = DriverManager.getConnection(dbURL);
			log.info("Before PreparedStatement");
			stmt = con.prepareStatement(
					"update behaviosecproxyless.user set other=? where username=?");
			stmt.setString(2, user);
			stmt.setString(1, report);
			stmt.execute();
			log.info("Updated user " + user + " " + report);
			con.close();
		} catch (Exception e) {
			log.error(e.toString());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException sqlEx) { } // ignore

				con = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}
    }
    

}