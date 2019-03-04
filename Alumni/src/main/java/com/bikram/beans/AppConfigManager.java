package com.bikram.beans;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class AppConfigManager {

	@Value("${app.roles}")
	private String roles;
	@Value("${app.delim}")
	private String delim;
	@Value("${app.badgeLoc}")
	private String badgeTempLoc;
	@Value("${app.access}")
	private String appAccess;
	@Value("${app.secret}")
	private String appSecret;
	@Value("${app.bucket}")
	private String s3Bucket;
	@Value("${app.orgPrefix}")
	private String orgPrefix;
	@Value("${app.logo}")
	private String logo;
	@Value("${app.email}")
	private String emailUserName;
	@Value("${app.password}")
	private String emailPassword;
	@Value("${app.emailhost}")
	private String emailHost;
	@Value("${app.emailport}")
	private String emailPort;
	
	
	

	public String getEmailUserName() {
		return emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public String getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(String emailPort) {
		this.emailPort = emailPort;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getOrgPrefix() {
		return orgPrefix;
	}

	public void setOrgPrefix(String orgPrefix) {
		this.orgPrefix = orgPrefix;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

	public String getAppAccess() {
		return appAccess;
	}

	public void setAppAccess(String appAccess) {
		this.appAccess = appAccess;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getBadgeTempLoc() {
		return badgeTempLoc;
	}

	public void setBadgeTempLoc(String badgeTempLoc) {
		this.badgeTempLoc = badgeTempLoc;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getDelim() {
		return delim;
	}

	public void setDelim(String delim) {
		this.delim = delim;
	}
	
	
	
	
}
