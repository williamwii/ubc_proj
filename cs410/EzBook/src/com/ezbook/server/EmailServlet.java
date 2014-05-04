package com.ezbook.server;


import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ezbook.shared.PostComponent;
import com.google.gson.Gson;

	public class EmailServlet extends HttpServlet{
		private static final long serialVersionUID = -1317906748915738951L;
	    private static final Logger log = Logger.getLogger(EmailServlet.class.getName());

		public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
			try {
				Properties props = new Properties();
				String data = request.getParameter("data");
				String emailAddr = request.getParameter("to");
				Session session = Session.getDefaultInstance(props, null);
				log.info("Sending email to " + emailAddr);
				
				Message email = new MimeMessage(session);
				email.setSubject("Thank you for choosing Bosung Books");
				email.setFrom(new InternetAddress("noreply@bosungbooks.appspotmail.com", "Bosung Books"));
				email.setRecipient(RecipientType.TO, new InternetAddress(emailAddr));
				
				String content = data;
				String type = request.getParameter("type");
				if (type.equals(EmailService.EMAIL_TYPE.SUBSCRIPTION.toString())) {
					Gson gson = new Gson();
					email.setSubject("Your subscription is here!");
					PostComponent component = gson.fromJson(data, PostComponent.class);
					
					content = "<div><a href='http://bosungbooks.appspot.com/#Marketplace:post="
							+ component.getPostID() + "'>Your subscription is here!</a></div>"
							+ "<br/>"
							+ "<div>Title: " + component.getTitle() + "</div>"
							+ "<div>ISBN: " + component.getISBN() + "</div>"
							+ "<div>Contact: " + component.getFbName() + "</div>"
							+ "<div>Email: " + component.getEmail() + "</div>"
							+ "<div>Details: " + component.getComment() + "</div>"
							+ "<div>Proposed Meetup Location: " + component.getAddress() + "</div>"
							+ "<br/>"
							+ "<div><a href=\"http://bosungbooks.appspot.com/unsubscribe?code="
							+ SecurityKeyGenerator.genUnsubcriptionKey(emailAddr, component.getISBN())
							+ "\">Click here to unsubscibe this book</a></div>"
							+ "<br/><br/>"
							+ "<div>Thank you,</div>"
							+ "<div>Team Bosung</div>";
				}
				
		        Multipart mp = new MimeMultipart();
				MimeBodyPart htmlPart = new MimeBodyPart();
		        htmlPart.setContent(content, "text/html");
		        mp.addBodyPart(htmlPart);
		        email.setContent(mp);
				Transport.send(email);
			}
			catch(AddressException e) {
				
			}
			catch (MessagingException e) {
				
			}
		}
	}
	