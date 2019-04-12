package com.bikram.service;

import javax.websocket.server.PathParam;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bikram.beans.EventRequestBean;
import com.bikram.beans.ResponseBean;
import com.bikram.dao.DAO;
import com.bikram.exception.KvpalException;
import com.bikram.utility.Encryption;
import com.bikram.utility.FileStorageService;
import com.bikram.utility.TokenManager;

@Controller
public class AdminControllerService {
	
	@Autowired
	private DAO dao;
	@Autowired
	Encryption encryption;

	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	private TokenManager manager;
	private final Logger LOGGER = Logger.getLogger(AdminControllerService.class);

	@RequestMapping(value = "/getAllUser", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> getAllUser(@RequestHeader("sso") String token) {
		try {
			dao.getAllUserDetail(token);
			return null;
		} catch (KvpalException e) {
			ResponseBean responseBean = new ResponseBean();
			responseBean.setInternalCode(e.getInternalCode());
			responseBean.setMesssage(e.getMessage());
			responseBean.setData(e.getData());
			responseBean.setStatus(e.getStatus());
			return new ResponseEntity<ResponseBean>(responseBean, e.getHttpStatus());
		}
	}
	
	@RequestMapping(value = "/setUserStatus/{userId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> setUserStatus(@RequestHeader("sso") String token,@PathVariable("userId") int userId) {
		try {
			dao.setUserStatus(token, Integer.valueOf(userId));
			return null;
		} catch (KvpalException e) {
			ResponseBean responseBean = new ResponseBean();
			responseBean.setInternalCode(e.getInternalCode());
			responseBean.setMesssage(e.getMessage());
			responseBean.setData(e.getData());
			responseBean.setStatus(e.getStatus());
			return new ResponseEntity<ResponseBean>(responseBean, e.getHttpStatus());
		}
	}
	
	@RequestMapping(value = "/createevent", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> createEvent(@RequestHeader("sso") String token,@RequestBody EventRequestBean requestBean) {
		try {
			dao.createEvent(token, requestBean.getName());
			return null;
		} catch (KvpalException e) {
			ResponseBean responseBean = new ResponseBean();
			responseBean.setInternalCode(e.getInternalCode());
			responseBean.setMesssage(e.getMessage());
			responseBean.setData(e.getData());
			responseBean.setStatus(e.getStatus());
			return new ResponseEntity<ResponseBean>(responseBean, e.getHttpStatus());
		}
	}
	
	@RequestMapping(value = "/event/{userId}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> pushAttendant(@RequestHeader("sso") String token,@RequestHeader("eventId") long eventId,@PathVariable("userId") String userId) {
		try {
			dao.pushAttendant(token, userId, eventId);
			return null;
		} catch (KvpalException e) {
			ResponseBean responseBean = new ResponseBean();
			responseBean.setInternalCode(e.getInternalCode());
			responseBean.setMesssage(e.getMessage());
			responseBean.setData(e.getData());
			responseBean.setStatus(e.getStatus());
			return new ResponseEntity<ResponseBean>(responseBean, e.getHttpStatus());
		}
	}
	
	 @RequestMapping( {"/test", "/home"} )
	 @ResponseBody
	    public String home() {
	        return "Hello, World!";
	    }
	 
	
}
