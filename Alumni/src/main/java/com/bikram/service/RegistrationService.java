package com.bikram.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RegistrationService {
	
	private final Logger LOGGER = Logger.getLogger(RegistrationService.class);
	@RequestMapping(value="/register",method=RequestMethod.GET,produces = "application/json")
	@ResponseBody
	public Object register()
	{
		LOGGER.info("Login Process initiated");
		return "Registered Successfully";
	}
}
