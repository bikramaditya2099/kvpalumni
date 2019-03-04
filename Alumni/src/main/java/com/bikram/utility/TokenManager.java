package com.bikram.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bikram.beans.AppConfigManager;
import com.bikram.exception.ApplicationEnum;
import com.bikram.exception.KvpalException;
@Component
public class TokenManager {

	@Autowired
	AppConfigManager manager;
	@Autowired
	Encryption encryption;
	
	private String userName;
	//contains encrypted password
	private String password;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String generateToken(String userName,String password){
		String token=userName.concat(manager.getDelim()).concat(password);
		String encryptedToken[]=encryption.encrypt(token);
		return encryptedToken[0].replaceAll("[\\t\\n\\r]+","");
	}
	
	public TokenManager getUserNameAndPasswordFromToken(String token) throws KvpalException{
		System.out.println(token);
		try {
			String credentials=encryption.decrypt(token);
			this.userName=credentials.split(manager.getDelim())[0];
			//contains encrypted password
			this.password=credentials.split(manager.getDelim())[1];
			return this;
		} catch (Exception e) {
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
		}
		
		
	}
}
