package com.behaviosec.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.config.Constants;
import com.behaviosec.entities.Report;
import com.behaviosec.entities.Response;
import com.behaviosec.handler.SimpleAuthenticationSuccessHandler;
import com.behaviosec.model.Configuration;
import com.behaviosec.model.OTPModel;
import com.behaviosec.model.User;
import com.behaviosec.service.ConfigurationService;
import com.behaviosec.service.UserService;
import com.behaviosec.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.NonNull;

@Controller
public class OTPController {
	@Value("${behaviosec.behaviosecurl}")
	@NonNull
	public String behaviosecurl;
	@Value("${behaviosec.tenantId}")
	public String tenantId;
	@Value("${behaviosec.maxtries:1}")
	public String maxTries;
	@Value( "${behaviosec.dashboardurl}" )
	@NonNull public String dashboardUrl;
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private UserService userService;
	
	@Autowired
    private ConfigurationService configurationService;

	@RequestMapping(value="/home/loginOTP", method= RequestMethod.GET)
	public @ResponseBody void handleGET(HttpServletRequest request, HttpServletResponse response,
											@RequestHeader(value = "referer", required = false) final String referer, @Valid OTPModel otpModel,
											BindingResult bindingResult) {
		try {
			response.sendRedirect("/home/homepage");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PostMapping("/home/loginOTP")
	public ModelAndView validateOTP(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "referer", required = false) final String referer, @Valid OTPModel otpModel, 
			BindingResult bindingResult) {
		
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());

		if ("behaviosec".equals(otpModel.getType())) {
			String email = otpModel.getEmail();
			if (email == null || email.trim().length() == 0) {
				bindingResult.rejectValue("email", "error.email", "email cannot be null");
				modelAndView.addObject("message", "Please type your email");
				modelAndView.setViewName("/home/loginBehaviosec");
				return modelAndView;
			} else if (bindingResult.hasErrors()) {
				modelAndView.addObject("message", "Please type your email");
				modelAndView.setViewName("/home/loginBehaviosec");
				return modelAndView;
			}
		} else {
			String otpValue = otpModel.getOtpValue();
			if (otpValue == null || otpValue.trim().length() == 0) {
				bindingResult.rejectValue("otpValue", "error.otpValue", "OTP Value cannot be null");
				modelAndView.addObject("message", "Please enter your google authenticator code");
				modelAndView.setViewName("/home/loginOTP");
				return modelAndView;
			} else if (bindingResult.hasErrors()) {
				modelAndView.addObject("message", "Please enter your google authenticator code");
				modelAndView.setViewName("/home/loginOTP");
				return modelAndView;
			} else if (!otpValue.matches("\\d+\\d+\\d+\\d+\\d+\\d+")) {
				bindingResult.rejectValue("otpValue", "error.otpValue", "OTP Value must contain only digits");
				modelAndView.addObject("message", "Please enter your google authenticator code");
				modelAndView.setViewName("/home/loginOTP");
				return modelAndView;
			}
		}

		String userAgent = request.getHeader("User-Agent");
		String type = otpModel.getType();
		String report = null;

//		user.getOther();

		Configuration minimumStepUpScoreConfiguration = configurationService.findConfigurationByName(Constants.MINIMUM_STEP_UP_SCORE);
		Configuration maximumStepUpRiskConfiguration = configurationService.findConfigurationByName(Constants.MAXIMUM_STEP_UP_RISK);
		int minimumStepUpScoreValue = Integer.parseInt(minimumStepUpScoreConfiguration.getConfigurationValue());
		int maximumStepUpRiskValue = Integer.parseInt(maximumStepUpRiskConfiguration.getConfigurationValue());

		if (type.startsWith(Constants.OTP)) {
			String secret = user.getGoogleOauthToken();
			String totpKey = otpModel.getOtpValue();
			report = user.getOther();
			Report bhsReport = getBhsReport(report);
			Totp totp = new Totp(secret);
			try { 
				if (secret!= null && !"".equals(totpKey.trim()) && totp.verify(totpKey)) {
					modelAndView = addReport(modelAndView, null, bhsReport, null, 1, "home/homepage");
				} else {
	//				if ((Constants.OTP + maxTries).equals(type)) {
						SecurityContextHolder.getContext().setAuthentication(null);
						SecurityContextHolder.clearContext();
						HttpSession hs = request.getSession();
						Enumeration <String> e = hs.getAttributeNames();
						while (e.hasMoreElements()) {
							String attr = (String) e.nextElement();
							hs.setAttribute(attr, null);
						}
						removeCookies(request);
						hs.invalidate();
	
				        modelAndView.addObject("message", "Invalid OTP code, please login again!");
					    modelAndView.setViewName("logout");
	/*				} else {
				        if("otp".equals(type)) {
							modelAndView = addReport(modelAndView, otpModel, bhsReport, Constants.OTP, 1, "home/loginOTP");
				        } else if("otp1".equals(type)) {
							modelAndView = addReport(modelAndView, otpModel, bhsReport, Constants.OTP, 2, "home/loginOTP");
				        } else if("otp2".equals(type)) {
							modelAndView = addReport(modelAndView, otpModel, bhsReport, Constants.OTP, 3, "home/loginOTP");
				        } 
	
				        modelAndView.addObject("googleauth", "OTP (Google Authenticator)");
				        modelAndView.addObject("message", "Invalid google authenticator value, please try again!");
					} */
				}
			} catch (Exception e) {
				SecurityContextHolder.getContext().setAuthentication(null);
				SecurityContextHolder.clearContext();
				HttpSession hs = request.getSession();
				Enumeration <String> enum2 = hs.getAttributeNames();
				while (enum2.hasMoreElements()) {
					String attr = (String) enum2.nextElement();
					hs.setAttribute(attr, null);
				}
				removeCookies(request);
				hs.invalidate();

		        modelAndView.addObject("message", "Invalid OTP code, please login again!");
			    modelAndView.setViewName("logout");
			}

		} else {
			String timingData = otpModel.getOther();
			log.debug("timingData " + timingData);
			String userName = user.getUsername();
			String clientIp = Helper.getClientIpAddress(request);

			// String result = utils.checkData(userName, userAgent, clientIp, timingData);
			Report bhsReport = null;
			
			Response r = SimpleAuthenticationSuccessHandler.getResponse(behaviosecurl, tenantId, clientIp, userAgent,
					userName, timingData, log);
			if (r.hasReport()) {
				report = r.getReponseString();
				userService.updateUser(user, report);
				bhsReport = getBhsReport(report);
			}
			
			if (evaluateReport(bhsReport, Constants.OTP, minimumStepUpScoreValue, maximumStepUpRiskValue)) {
				modelAndView.addObject("adminMessage", "Email confirmed");
				modelAndView = addReport(modelAndView, null, bhsReport, null, 1, "home/homepage");
			} else {
				if ("behaviosec".equals(type)) {
					modelAndView = addReport(modelAndView, otpModel, bhsReport, Constants.OTP, 1, "home/loginOTP");
			        modelAndView.addObject("googleauth", "OTP (Google Authenticator)");
			        modelAndView.addObject("message", "Your score is still too low, please use google authenticator instead");
				} 
			}
		}
		return modelAndView;
	}
	
	public static void removeCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setMaxAge(0);
			}
		}
	}
	
	private static Report getBhsReport(String report) {
		Report bhsReport = null;
		try {
			bhsReport = objectMapper.readValue(
					report,
					Report.class
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bhsReport;
	}
	
	private ModelAndView addReport(ModelAndView modelAndView, OTPModel otpModel, Report bhsReport, String type, int iteration, String viewName) {
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
		
		if (otpModel != null && type != null) {
			otpModel.setOtpValue(null);
			otpModel.setType(type + iteration);
			modelAndView.setViewName("home/loginOTP");	
		}
		
		modelAndView.setViewName(viewName);

		return modelAndView;
	}

	private boolean evaluateReport(Report bhsReport, String phase, int minimumStepUpScoreValue, int maximumStepUpRiskValue) {
		try { 
		if (bhsReport != null) {
			log.debug("bhsReport.getScore() : " + bhsReport.getScore() + " minimumStepUpScoreValue: " + minimumStepUpScoreValue);
			log.debug("bhsReport.getRisk() : " + bhsReport.getRisk() + " maximumStepUpRiskValue: " + maximumStepUpRiskValue);
			log.debug("bhsReport.isPocAnomaly() : " + bhsReport.isPocAnomaly());
			log.debug("bhsReport.isPocAnomaly() : " + bhsReport.isCopyOrPaste());
		} else {
			log.error("Why is report null?");
		}
		if (bhsReport != null &&
				(int) bhsReport.getScore() > minimumStepUpScoreValue &&
				(int) bhsReport.getRisk() < maximumStepUpRiskValue &&
				!bhsReport.isPocAnomaly() &&
				!bhsReport.isCopyOrPaste()) {
			return true;
		} else {
			return false;
		}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}