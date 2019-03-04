package com.bikram.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bikram.beans.AppConfigManager;

@Component
public class EmailBodyGenerator {
	@Autowired
	AppConfigManager manager;

	public String generateEmailBody(String name,String message){
		String body="";
		body=body.concat(EmailEnum.HI.getValue()+" "+name+",<br>");
		body=body.concat("<h4>"+message+"</h4>");
		body=body.concat("<br><br>");
		body=body.concat(EmailEnum.REGARDS.getValue()+",<br>");
		body=body.concat(manager.getOrgPrefix());
		return body;
	}
}
