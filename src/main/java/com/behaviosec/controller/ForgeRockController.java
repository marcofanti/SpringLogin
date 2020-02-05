package com.behaviosec.controller;

import com.behaviosec.entities.Report;
import com.behaviosec.model.User;
import com.behaviosec.service.ConfigurationService;
import com.behaviosec.service.LocalizedMessagesService;
import com.behaviosec.service.UserService;
import com.behaviosec.utils.ForgeRockUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ForgeRockController {
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
    
    @RequestMapping(value={"/fr/sso"}, method = RequestMethod.GET)
    public RedirectView ssoGET(HttpServletRequest request,
    	      HttpServletResponse response) throws IOException {
    	
    	Iterator<String> itr = request.getHeaderNames().asIterator();

    	String cookie = null;
		while (itr.hasNext()) {
			Object header = itr.next();
			String headerValue = request.getHeader(header.toString());
			System.out.println(header + " " + headerValue);
			if (header.equals("cookie")) {
				cookie = headerValue;
			}
		}

        int ind = cookie.indexOf("DirectoryPro");
        cookie = cookie.substring(ind + "DirectoryPro=".length());
        ind = cookie.indexOf(";");
        
        String userTokenId = cookie;
        
        if (ind > 0) {
        	userTokenId = cookie.substring(0, ind);
        }
        
        log.debug("TokenId = " + userTokenId);

        ForgeRockUtils forgeRockUtils = new ForgeRockUtils();
        
        String tokenId = forgeRockUtils.doLogin(forgerockAdmin, forgerockAdminPassword, forgerockurl);
        
        String userId = forgeRockUtils.getSessionInfo(tokenId, userTokenId, forgerockurl);
        
        String secret = forgeRockUtils.doStandaloneLogin(userId, "password", standaloneurl);
    	log.debug("User from forgeRock = " + userId + " secret = " + secret);
    	
    	
    	Cookie c = new Cookie("JSESSIONID", secret);
    	c.setDomain("openam2.example.com");
		c.setPath("/");
		c.setHttpOnly(true);
		response.addCookie(c);

    	RedirectView redirectView = new RedirectView();
	    //redirectView.setUrl(loginControllerRedirectionurl + "?a=" + secret);
    	redirectView.setUrl("http://openam2.example.com:8090/fr/homepage");
	    log.debug("Redirecting to " + redirectView.getUrl());
	    return redirectView;
    }

    @RequestMapping(value={"fr/homepage"}, method = RequestMethod.GET)
    public ModelAndView frhome(HttpServletRequest request,
  	      HttpServletResponse response){
    	String cookie = null;
    	Iterator<String> itr = request.getHeaderNames().asIterator();
		while (itr.hasNext()) {
			Object header = itr.next();
			String headerValue = request.getHeader(header.toString());
			System.out.println(header + " " + headerValue);
			if (header.equals("cookie")) {
				cookie = headerValue;
			}
		}

		ModelAndView modelAndView = new ModelAndView();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ("anonymousUser".equals(auth.getName())) {
        	modelAndView.setViewName("fr/sso");
            return modelAndView;
        }
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
		
		String link = "<a href=\"" + dashboardUrl +
				"BehavioSenseDashboard/usertimeline.jsp?userid="
				+ bhsReport.getUserid() +
				"&sessionid=" +bhsReport.getSessionid()+
				"\">View on the dashboard</a>";

		String dashboard = dashboardUrl +
				"BehavioSenseDashboard/usertimeline.jsp?userid="
				+ bhsReport.getUserid() +
				"&sessionid=" +bhsReport.getSessionid();

		log.info("Dashboard url: " + dashboard);modelAndView.addObject("dashboardUrl", dashboard);
		modelAndView.addObject("adminMessage", "<b>Behavioral Report: </b>" + link);
		modelAndView.addObject("score", "<b>Behavioral Score: </b>" + ((int) bhsReport.getScore()));
		modelAndView.addObject("risk", "<b>Risk: </b>" + ((int) bhsReport.getRisk()));
		modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());

		// RISK flags
		modelAndView.addObject("pocAnomaly", "<b>Cut and Paste Anomaly: </b>" + bhsReport.isPocAnomaly());

		if( bhsReport.hasDataIntegrityFlags()){
			log.info("diData flags detected");
			String data = "";
			for(int flag: bhsReport.getDiDesc()){
				if( flag != 1 || flag != 2 || flag != 10) {
					data += bhsReport.getDataIntegrityReason(flag) + "</br>";
				}
			}
			log.debug("diData print: " + data);
			modelAndView.addObject("diDec",  data);

		} else {
			log.debug("No diDec flags");
		}
		modelAndView.addObject("deviceChange", "<b>Device Changed: </b>" + bhsReport.isDeviceChanged());
		modelAndView.addObject("ipChange", "<b>Ip Changed: </b>" + bhsReport.isIpChanged());
		modelAndView.addObject("bot", "<b>Bot: </b>" + bhsReport.isBot());
		modelAndView.addObject("replay", "<b>Replay Attack: </b>" + bhsReport.isReplay());
		modelAndView.addObject("remoteAccess", "<b>Remote Access: </b>" + bhsReport.isRemoteAccess());
		modelAndView.addObject("isTrained", "<b>Is Trained: </b>" + bhsReport.isTrained());
		


        log.debug("going to fr/homepage");
        modelAndView.setViewName("fr/homepage");
        return modelAndView;
    }

    @RequestMapping(value={"fr/success"}, method = RequestMethod.GET)
    public ModelAndView frsuccess(){
        ModelAndView modelAndView = new ModelAndView();
        log.debug("going to fr/success");
        modelAndView.setViewName("fr/success");
        return modelAndView;
    }

    @RequestMapping(value={"fr/logout"}, method = RequestMethod.GET)
    public RedirectView frlogout(HttpServletRequest request, HttpServletResponse response){
    	Iterator<String> itr = request.getHeaderNames().asIterator();

    	String cookie = null;
		while (itr.hasNext()) {
			Object header = itr.next();
			String headerValue = request.getHeader(header.toString());
			System.out.println(header + " " + headerValue);
			if (header.equals("cookie")) {
				cookie = headerValue;
			}
		}

        int ind = cookie.indexOf("DirectoryPro");
        cookie = cookie.substring(ind + "DirectoryPro=".length());
        ind = cookie.indexOf(";");
        
        String userTokenId = cookie;
        
        if (ind > 0) {
        	userTokenId = cookie.substring(0, ind);
        }
        
        log.debug("TokenId = " + userTokenId);

        ForgeRockUtils forgeRockUtils = new ForgeRockUtils();
        
        String tokenId = forgeRockUtils.doLogout("", forgerockurl);    	
    	
    	Cookie c = new Cookie("iPlanetDirectoryPro", "");
    	c.setDomain("openam2.example.com");
		c.setPath("/");
		c.setMaxAge(0);
		c.setHttpOnly(true);
		response.addCookie(c);

    	RedirectView redirectView = new RedirectView();
	    //redirectView.setUrl(loginControllerRedirectionurl + "?a=" + secret);
    	redirectView.setUrl("http://openam.example.com:19876/fr/success");
	    log.debug("Redirecting to " + redirectView.getUrl());
	    return redirectView;
    }

    @RequestMapping(value="/fr/profile", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("/fr/profile");
        return modelAndView;
    }
    
    @RequestMapping(value="/fr/profile", method = RequestMethod.POST)
    public ModelAndView registrationSave(@Valid User user, BindingResult bindingResult){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("/fr/profile");
        return modelAndView;
    }
}
