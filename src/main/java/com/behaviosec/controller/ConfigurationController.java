package com.behaviosec.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.config.Constants;
import com.behaviosec.model.Configuration;
import com.behaviosec.model.ConfigurationList;
import com.behaviosec.service.ConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class ConfigurationController {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
    private ConfigurationService configurationService;

	@GetMapping("/admin/configuration")
	public ModelAndView registration() {
		
		Configuration minimumLoginScore = configurationService.findConfigurationByName(Constants.MINIMUM_LOGIN_SCORE);
		Configuration maximumLoginRisk = configurationService.findConfigurationByName(Constants.MAXIMUM_LOGIN_RISK);
		Configuration minimumFormScore = configurationService.findConfigurationByName(Constants.MINIMUM_FORM_SCORE);
		Configuration maximumFormRisk = configurationService.findConfigurationByName(Constants.MAXIMUM_FORM_RISK);
		Configuration minimumStepUpScore = configurationService.findConfigurationByName(Constants.MINIMUM_STEP_UP_SCORE);
		Configuration maximumStepUpRisk = configurationService.findConfigurationByName(Constants.MAXIMUM_STEP_UP_RISK);
		Configuration minimumSSOScore = configurationService.findConfigurationByName(Constants.MINIMUM_SSO_SCORE);
		Configuration maximumSSORisk = configurationService.findConfigurationByName(Constants.MAXIMUM_SSO_RISK);
		
		ModelAndView modelAndView = new ModelAndView();
		ConfigurationList configurationList = new ConfigurationList();
		if (minimumLoginScore == null) {
			configurationList.setMinimumLoginScore(100);
		} else {
			configurationList.setMinimumLoginScore(Integer.parseInt(minimumLoginScore.getConfigurationValue()));
		}
		if (maximumLoginRisk == null) {
			configurationList.setMaximumLoginRisk(50);
		} else {
			configurationList.setMaximumLoginRisk(Integer.parseInt(maximumLoginRisk.getConfigurationValue()));
		}

		if (minimumStepUpScore == null) {
			configurationList.setMinimumStepUpScore(100);
		} else {
			configurationList.setMinimumStepUpScore(Integer.parseInt(minimumStepUpScore.getConfigurationValue()));
		}
		if (maximumStepUpRisk == null) {
			configurationList.setMaximumStepUpRisk(50);
		} else {
			configurationList.setMaximumStepUpRisk(Integer.parseInt(maximumStepUpRisk.getConfigurationValue()));
		}

		if (minimumFormScore == null) {
			configurationList.setMinimumFormScore(100);
		} else {
			configurationList.setMinimumFormScore(Integer.parseInt(minimumFormScore.getConfigurationValue()));
		}
		if (maximumFormRisk == null) {
			configurationList.setMaximumFormRisk(50);
		} else {
			configurationList.setMaximumFormRisk(Integer.parseInt(maximumFormRisk.getConfigurationValue()));
		}

		if (minimumSSOScore == null) {
			configurationList.setMinimumSSOScore(100);
		} else {
			configurationList.setMinimumSSOScore(Integer.parseInt(minimumSSOScore.getConfigurationValue()));
		}
		if (maximumSSORisk == null) {
			configurationList.setMaximumSSORisk(50);
		} else {
			configurationList.setMaximumSSORisk(Integer.parseInt(maximumSSORisk.getConfigurationValue()));
		}

		modelAndView.addObject("configurationList", configurationList);
		modelAndView.addObject("adminMessage", "");
		modelAndView.setViewName("/admin/configuration");
		return modelAndView;
	}

	@PostMapping("/admin/configuration")
	public ModelAndView messageSubmit(HttpServletRequest request, @Valid ConfigurationList configurationList, 
			BindingResult bindingResult) {

		if (!bindingResult.hasErrors()) {
			Configuration minimumLoginScore = new Configuration();
			minimumLoginScore.setConfigurationName(Constants.MINIMUM_LOGIN_SCORE);
			minimumLoginScore.setConfigurationValue(configurationList.getMinimumLoginScore() + "");		
			configurationService.saveConfiguration(minimumLoginScore);
			
			Configuration maximumLoginRisk = new Configuration();
			maximumLoginRisk.setConfigurationName(Constants.MAXIMUM_LOGIN_RISK);
			maximumLoginRisk.setConfigurationValue(configurationList.getMaximumLoginRisk() + "");
			configurationService.saveConfiguration(maximumLoginRisk);
			
			Configuration minimumStepUpScore = new Configuration();
			minimumStepUpScore.setConfigurationName(Constants.MINIMUM_STEP_UP_SCORE);
			minimumStepUpScore.setConfigurationValue(configurationList.getMinimumStepUpScore() + "");		
			configurationService.saveConfiguration(minimumStepUpScore);
			
			Configuration maximumStepUpRisk = new Configuration();
			maximumStepUpRisk.setConfigurationName(Constants.MAXIMUM_STEP_UP_RISK);
			maximumStepUpRisk.setConfigurationValue(configurationList.getMaximumStepUpRisk() + "");
			configurationService.saveConfiguration(maximumStepUpRisk);
			
			Configuration minimumFormScore = new Configuration();
			minimumFormScore.setConfigurationName(Constants.MINIMUM_FORM_SCORE);
			minimumFormScore.setConfigurationValue(configurationList.getMinimumFormScore() + "");		
			configurationService.saveConfiguration(minimumFormScore);
			
			Configuration maximumFormRisk = new Configuration();
			maximumFormRisk.setConfigurationName(Constants.MAXIMUM_FORM_RISK);
			maximumFormRisk.setConfigurationValue(configurationList.getMaximumLoginRisk() + "");
			configurationService.saveConfiguration(maximumFormRisk);
			
			Configuration minimumSSOScore = new Configuration();
			minimumSSOScore.setConfigurationName(Constants.MINIMUM_LOGIN_SCORE);
			minimumSSOScore.setConfigurationValue(configurationList.getMinimumLoginScore() + "");		
			configurationService.saveConfiguration(minimumSSOScore);
			
			Configuration maximumSSORisk = new Configuration();
			maximumSSORisk.setConfigurationName(Constants.MAXIMUM_LOGIN_RISK);
			maximumSSORisk.setConfigurationValue(configurationList.getMaximumLoginRisk() + "");
			configurationService.saveConfiguration(maximumSSORisk);
		}
		
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("configurationList", configurationList);
		modelAndView.setViewName("/admin/configuration");
		return modelAndView;
	}

}
