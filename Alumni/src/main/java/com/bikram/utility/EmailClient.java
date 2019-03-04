package com.bikram.utility;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bikram.beans.AppConfigManager;

@Component
public class EmailClient {
	/**
	 * Utility method to send simple HTML email
	 * 
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	@Autowired
	AppConfigManager manager;
	public void sendEmail(Session session, String toEmail, String subject, String body) {
		try {
			MimeMessage msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(manager.getEmailUserName(), manager.getOrgPrefix()));

			msg.setReplyTo(InternetAddress.parse(manager.getEmailUserName(), false));

			msg.setSubject(subject, "UTF-8");

			// msg.setText(body, "UTF-8");
			msg.setContent(body, "text/html; charset=utf-8");

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			System.out.println("Message is ready");
			Transport.send(msg);

			System.out.println("EMail Sent Successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String toEmail, String subject, String body) {
		final String fromEmail = manager.getEmailUserName();
		final String password = manager.getEmailPassword();
		Properties props = new Properties();
		props.put("mail.smtp.host", manager.getEmailHost()); // SMTP Host
		props.put("mail.smtp.port", manager.getEmailPort()); // TLS Port
		props.put("mail.smtp.auth", "true"); // enable authentication
		props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

		// create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getInstance(props, auth);
		sendEmail(session, toEmail, subject, body);

	}

	public static void main(String[] args) {
		final String fromEmail = "support@kvpurialumni.com"; // requires valid
																// gmail id
		final String password = "admin@admin"; // correct password for gmail id
		final String toEmail = "bikram.aditya2099@gmail.com"; // can be any
																// email id

		System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.kvpurialumni.com"); // SMTP Host
		props.put("mail.smtp.port", "587"); // TLS Port
		props.put("mail.smtp.auth", "true"); // enable authentication
		props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

		// create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getInstance(props, auth);

		// EmailClient.sendEmail(session, toEmail,"TLSEmail Testing Subject",
		// "<h1>Hello</h1>");

	}
}
