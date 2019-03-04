package com.bikram.service;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bikram.beans.LoginBean;
import com.bikram.beans.ResponseBean;
import com.bikram.dao.DAO;
import com.bikram.dao.beans.RoleBean;
import com.bikram.dao.beans.UserBean;
import com.bikram.exception.ApplicationEnum;
import com.bikram.exception.KvpalException;
import com.bikram.utility.BadgeGenerator;
import com.bikram.utility.Encryption;
import com.bikram.utility.FileStorageService;
import com.bikram.utility.TokenManager;

@Controller
public class RegistrationService {
	@Autowired
	private DAO dao;
	@Autowired
	Encryption encryption;

	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	private TokenManager manager;
	@Autowired
	private BadgeGenerator generator;
	private final Logger LOGGER = Logger.getLogger(RegistrationService.class);

	@PostMapping("/uploadFile")
	public ResponseEntity<ResponseBean> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestHeader("sso") String token,HttpServletRequest request) {
		try {
			UserBean userBean = dao.getUserBySSO(token);
			String fileName = fileStorageService.storeFile(file);
			userBean.setImageLocation(fileName);
			String badge=fileStorageService.storeFile(generator.createImage(userBean, request));
			userBean.setAlumniBadge(badge);
			dao.updateUser(userBean);
			ResponseBean responseBean = new ResponseBean();
			responseBean.setMesssage(fileName);
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.valueOf(200));
		} catch (KvpalException e) {
			ResponseBean responseBean = new ResponseBean();
			responseBean.setInternalCode(e.getInternalCode());
			responseBean.setMesssage(e.getMessage());
			responseBean.setData(e.getData());
			responseBean.setStatus(e.getStatus());
			return new ResponseEntity<ResponseBean>(responseBean, e.getHttpStatus());
		}

	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> register(@RequestBody UserBean bean) {
		RoleBean role = dao.getRoleById(3);
		bean.setRole(role);
		bean.setIsActive(false);
		bean.setRegistration_date(new Date());
		bean.setPassword(encryption.encrypt(bean.getPassword())[0]);
		try {
			dao.createUser(bean);
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

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> login(@RequestBody LoginBean bean) {
		try {
			dao.checkLogin(bean);
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
	
	@RequestMapping(value = "/getUser", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> getUser(@RequestHeader("sso") String token) {
		try {
			dao.getUserDetail(token);
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
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<ResponseBean> logout(@RequestHeader("sso") String token) {
		try {
			dao.logoutUser(token);
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

}
