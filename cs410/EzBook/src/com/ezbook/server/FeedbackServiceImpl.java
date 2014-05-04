package com.ezbook.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ezbook.client.FeedbackService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FeedbackServiceImpl extends RemoteServiceServlet implements
		FeedbackService {
	private static final long serialVersionUID = -4690575668149786297L;
	private static final Logger log = Logger.getLogger(FeedbackServiceImpl.class.getName());
	
	@Override
	public boolean sendFeedback(String message) {
		try {
			String sender = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			
			Message email = new MimeMessage(session);
			email.setText(sender + ":\n" + message);
			email.setSubject("Feedback");
			email.setFrom(new InternetAddress("feedback@bosungbooks.appspotmail.com", "Bosung Books"));
			email.setRecipient(Message.RecipientType.TO, new InternetAddress("admins"));
			Transport.send(email);
			return true;
		} catch (AddressException e) {
			log.warning(e.getLocalizedMessage());
			return false;
		} catch (MessagingException e) {
			log.warning(e.getLocalizedMessage());
			return false;
		} catch (UnsupportedEncodingException e) {
			log.warning(e.getLocalizedMessage());
			return false;
		}
	}

}
