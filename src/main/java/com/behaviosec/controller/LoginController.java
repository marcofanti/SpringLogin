package com.behaviosec.controller;

import com.behaviosec.entities.Report;
import com.behaviosec.model.OTPModel;
import com.behaviosec.model.User;
import com.behaviosec.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {
	@Value( "${spring.datasource.url}" )
	private String dbURL;
	@Value( "${spring.datasource.username}" )
	private String dbUser;
	@Value( "${spring.datasource.password}" )
	private String dbPwd;
	@Value( "${behaviosec.loginControllerRedirectionurl}" )
	public String loginControllerRedirectionurl;

	private Log log = LogFactory.getLog(this.getClass());
	private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;
        
    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value={"/success"}, method = RequestMethod.GET)
    public ModelAndView logout(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("success");
        return modelAndView;
    }

    @RequestMapping(value={"/saml/SSO"}, method = RequestMethod.POST)
    public RedirectView ssoPOST(HttpServletRequest request,
    	      HttpServletResponse response) throws IOException {

		String SAMLResponse = request.getParameter("SAMLResponse");
		
		byte[] saml = Base64.decodeBase64(SAMLResponse);

		String xmlToken = new String(saml);
		String beginning = "<saml:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">";
		int firstIndex = xmlToken.indexOf(beginning);
		int lastIndex = xmlToken.indexOf("</saml:NameID>");
		String userName = xmlToken.substring(firstIndex + beginning.length(), lastIndex);

    	String secret = getSecret(userName);
    	
		log.info("SECRET: " + secret);
    	Cookie c = new Cookie("JSESSIONID", secret);
		c.setPath("/");
		c.setHttpOnly(true);
		response.addCookie(c);
		InetAddress ip;
		String hostname="";
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("Your current IP address : " + ip);
			System.out.println("Your current Hostname : " + hostname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		RedirectView redirectView = new RedirectView();
	    redirectView.setUrl(loginControllerRedirectionurl + "?a=" + secret);
	    return redirectView;
    }

    @RequestMapping(value="/home/homepage", method = RequestMethod.GET)
    public ModelAndView home(@RequestHeader(value = "referer", required = false) final String referer){
    	
    	log.debug(referer);
    	
        ModelAndView modelAndView = new ModelAndView();
        
        if (referer != null && referer.endsWith("/login")) {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUsername(auth.getName());
	        
	        String result = user.getOther();
			Report bhsReport = null;
			try {
				bhsReport = objectMapper.readValue(
						result,
						Report.class
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			modelAndView.addObject("userName", user.getName() + " " + user.getLastName() +
		        		" (" + user.getUsername() + ")");
			modelAndView.addObject("adminMessage", "<b>This is your BehavioSec report:</b><br>");
			
			if (bhsReport != null) {
				int score = (int) bhsReport.getScore();
				if (score < 60) {
					modelAndView.addObject("score", "<b>Behavioral Score: </b>" + ((int) bhsReport.getScore()));
					modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());
			        modelAndView.addObject("googleauth", "OTP (Google Authenticator)");
			        modelAndView.addObject("googleauth", "Please TYPE (don't cut and paste) your email");
			        modelAndView.addObject("message", "Your score is too low");
					OTPModel otpModel = new OTPModel();
					otpModel.setType("otp");
					modelAndView.addObject("OTPModel", otpModel);
				    modelAndView.setViewName("home/loginOTP");	
				} else if (!bhsReport.isTrained()) {
					modelAndView.addObject("score", "<b>Behavioral Score: </b>" + ((int) bhsReport.getScore()));
					modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());
			        modelAndView.addObject("googleauth", "OTP (Google Authenticator)");
			        modelAndView.addObject("message", "You are not trained yet");
					OTPModel otpModel = new OTPModel();
					otpModel.setType("behaviosec");
					modelAndView.addObject("OTPModel", otpModel);
				    modelAndView.setViewName("home/loginOTP");	
				} else {				
					modelAndView.addObject("score", "<b>Behavioral Score: </b>" + ((int) bhsReport.getScore()));
					modelAndView.addObject("risk", "<b>Risk: </b>" + ((int) bhsReport.getRisk()));
					modelAndView.addObject("deviceChange", "<b>Device Changed: </b>" + bhsReport.isDeviceChanged());
					modelAndView.addObject("ipChange", "<b>Ip Changed: </b>" + bhsReport.isIpChanged());
					modelAndView.addObject("bot", "<b>Bot: </b>" + bhsReport.isBot());
					modelAndView.addObject("replay", "<b>Replay Attack: </b>" + bhsReport.isReplay());
					modelAndView.addObject("remoteAccess", "<b>Remote Access: </b>" + bhsReport.isRemoteAccess());
					modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());
					modelAndView.setViewName("home/homepage");
				}
			}
	    } else {
		    modelAndView.setViewName("home/homemenu");
	    }
        return modelAndView;
    }
    
    private String getSecret(String userName) {    	
    	String secret = null;		
		log.info("getSecret for " + userName);
        Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt  = null;
		
		try {
			con = DriverManager.getConnection(dbURL, dbUser, dbPwd);
			log.info("Before PreparedStatement");
			stmt = con.prepareStatement("select secret from behaviosecproxyless.user where username=?");
			stmt.setString(1, userName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				secret = rs.getString(1);
				log.info("Secret " + secret);
			}
			stmt.close();
			stmt = null;
			rs.close();
			rs = null;
			con.close();
			con = null;
			log.info(" Closing DB connection ");
		} catch (Exception e) {
			log.error(e);	
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException sqlEx) { } // ignore

				con = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore

				stmt = null;
			}
		}
		return secret;

    }
}
