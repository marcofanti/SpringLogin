package com.behaviosec.controller;

import com.behaviosec.config.Constants;
import com.behaviosec.entities.Report;
import com.behaviosec.model.Configuration;
import com.behaviosec.model.OTPModel;
import com.behaviosec.model.Role;
import com.behaviosec.model.User;
import com.behaviosec.service.ConfigurationService;
import com.behaviosec.service.LocalizedMessagesService;
import com.behaviosec.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

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
	@Value( "${behaviosec.loginControllerRedirectionurlAdmin}" )
	public String loginControllerRedirectionurlAdmin;
	@Value( "${behaviosec.forgerockadmin}" )
	public String forgerockAdmin;
	@Value( "${behaviosec.forgerockadminpassword}" )
	public String forgerockAdminPassword;
	@Value( "${behaviosec.forgerockurl}" )
	@NonNull public String forgerockurl;
	@Value( "${behaviosec.standaloneurl}" )
	@NonNull public String standaloneurl;
	@Value( "${behaviosec.dashboardurl}" )
	@NonNull public String dashboardUrl;
	
	@Autowired
    private ConfigurationService configurationService;

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
    	Cookie cookie = new Cookie("saml", "ping");
    	cookie.setPath("/");
    	cookie.setHttpOnly(true);
		response.addCookie(cookie);

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
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response, 
    		@RequestHeader(value = "referer", required = false) final String referer){
    	
    	Cookie [] cookies = request.getCookies();
    	for (Cookie cookie : cookies) {
    	     if ("saml".equals(cookie.getName())) {
    	          String value = cookie.getValue();
    	         //do something with the cookie's value.
    	     }
    	}
    	
    	log.debug("referer = " + referer);
    	
        ModelAndView modelAndView = new ModelAndView();
        
		String homePage = "home/homepage";

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());

		String result = user.getOther();
		Report bhsReport = null;
		try {
			bhsReport = objectMapper.readValue(result, Report.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		modelAndView.addObject("userName", user.getName() + " " + user.getLastName() + " (" + user.getUsername() + ")");
		String message = LocalizedMessagesService.getMessage("message.behaviosec.login.report");
		modelAndView.addObject("adminMessage", "<b>" + message + "</b><br>");

		if (referer != null && (!referer.contains("9031") && !referer.contains("18080"))) {
				
				//referer.endsWith("19876/") || referer.endsWith("/login") || referer.endsWith("loginOTP")
				//|| referer.endsWith("logout"))) {

			if (bhsReport != null) {
				int score = (int) bhsReport.getScore();
				int risk = (int) bhsReport.getRisk();
				boolean isCopyOrPaste = bhsReport.isCopyOrPaste();
				Configuration minimumLoginScoreConfiguration = configurationService
						.findConfigurationByName(Constants.MINIMUM_LOGIN_SCORE);
				Configuration maximumLoginRiskConfiguration = configurationService
						.findConfigurationByName(Constants.MAXIMUM_LOGIN_RISK);
				int minimumLoginScoreValue = Integer.parseInt(minimumLoginScoreConfiguration.getConfigurationValue());
				int maximumLoginRiskValue = Integer.parseInt(maximumLoginRiskConfiguration.getConfigurationValue());
				
				if (!bhsReport.isTrained()) {
					log.info("Not trained");
					modelAndView = addReport(modelAndView, bhsReport, "home/loginOTP");
					modelAndView.addObject("adminMessage", "Your profile is still in training");
					OTPModel otpModel = new OTPModel();
					otpModel.setType("otp");
					modelAndView.addObject("OTPModel", otpModel);
				} else if (isCopyOrPaste || score < minimumLoginScoreValue || risk > maximumLoginRiskValue) {
					log.info("pocAnomaly " + isCopyOrPaste + " score " + score + " risk " + risk);
					modelAndView.addObject("googleauth", "email address");
					OTPModel otpModel = new OTPModel();
					otpModel.setType("otp");
					if (isCopyOrPaste) {
						log.info("We detected cut and paste, please type your email");
						modelAndView.addObject("message", "We detected cut and paste, please type your email");
//						modelAndView.addObject("message", "We detected cut and paste, please enter your Google Authenticator code");
						modelAndView = addReport(modelAndView, bhsReport, "home/loginBehaviosec");
						otpModel.setType("behaviosec");
					} else if (score < minimumLoginScoreValue) {
						log.info("Your score is to low, please type your email");
//						modelAndView.addObject("message", "Your score is to low, please type your email");
						modelAndView.addObject("message", "Your score is to low, please enter your Google Authenticator code");
						modelAndView = addReport(modelAndView, bhsReport, "home/loginOTP");
					} else if (risk > maximumLoginRiskValue) {
						log.info("Your risk is too high, please type your email");
						modelAndView.addObject("message", "Your score is to low, please enter your Google Authenticator code");
						modelAndView = addReport(modelAndView, bhsReport, "home/loginOTP");
//						modelAndView.addObject("message", "Your risk is too high, please type your email");
					}
					modelAndView.addObject("OTPModel", otpModel);
				} else {
					modelAndView = addReport(modelAndView, bhsReport, homePage);
				}
			}
		} else {
			log.info("addReport" + bhsReport + " homepage " + homePage);
			modelAndView = addReport(modelAndView, bhsReport, homePage);
		}
		return modelAndView;
    }
    
	private ModelAndView addReport(ModelAndView modelAndView, Report bhsReport, String viewName) {
		String link = "<a href=\"" + dashboardUrl +
				"BehavioSenseDashboard/usertimeline.jsp?userid="
				+ bhsReport.getUserid() +
				"&sessionid=" +bhsReport.getSessionid()+
				"\">View on the dashboard</a>";

		String dashboard = dashboardUrl +
				"BehavioSenseDashboard/usertimeline.jsp?userid="
				+ bhsReport.getUserid() +
				"&sessionid=" +bhsReport.getSessionid();
		log.error("Building OTP View: " + dashboard);

		log.error("Dashboard url: " + dashboard);
		log.error("isCopyorPaste: " + bhsReport.isCopyOrPaste());

		modelAndView.addObject("dashboardUrl", dashboard);
		modelAndView.addObject("userName", bhsReport.getUserid());
		modelAndView.addObject("adminMessage", "<b>Behavioral Report: </b>" + link);
		modelAndView.addObject("score", "<b>Behavioral Score: </b>" + ((int) bhsReport.getScore()));
		modelAndView.addObject("risk", "<b>Risk: </b>" + ((int) bhsReport.getRisk()));
		modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());

		// RISK flags
		modelAndView.addObject("copyPaste", "<b>Cut or Paste detected: </b>" + bhsReport.isCopyOrPaste());
		modelAndView.addObject("deviceChange", "<b>Device Changed: </b>" + bhsReport.isDeviceChanged());
		modelAndView.addObject("ipChange", "<b>Ip Changed: </b>" + bhsReport.isIpChanged());
		modelAndView.addObject("bot", "<b>Bot: </b>" + bhsReport.isBot());
		modelAndView.addObject("replay", "<b>Replay Attack: </b>" + bhsReport.isReplay());
		modelAndView.addObject("remoteAccess", "<b>Remote Access: </b>" + bhsReport.isRemoteAccess());
		modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());
		
		modelAndView.setViewName(viewName);

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
