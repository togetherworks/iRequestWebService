package com.ncsi.olr.webservice.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

	Properties fMailServerConfig = new Properties();

	public Mailer()
	{
		fMailServerConfig.put("mail.host", IRequestWSProperties.getProperty("MAIL_HOST"));
		fMailServerConfig.put("mail.transport.protocol", IRequestWSProperties.getProperty("MAIL_PROTOCOL"));
	}

	public void sendEmail(
			String aFromEmailAddr, ArrayList aToEmailAddr,
			String aSubject, String aBody
			){

		Logger.getLogger().debug("Mailer : sendEmail : Starts ");
		Session session = Session.getDefaultInstance( fMailServerConfig, null );
		MimeMessage message = new MimeMessage( session );
		try {
			message.setFrom( new InternetAddress(aFromEmailAddr));
			Iterator lvIterator = aToEmailAddr.iterator();
			while(lvIterator.hasNext())
			{
				message.addRecipient(
						Message.RecipientType.TO, new InternetAddress(lvIterator.next().toString())

						);
			}
			message.setSubject(aSubject);
			message.setContent(aBody, "text/html");
			Transport.send( message );
		}
		catch (MessagingException ex){
			Logger.getLogger().error("Mailer : sendEmail : MessagingException: Cannot send email. " + ex);
			ex.printStackTrace();
		}

		Logger.getLogger().debug("Mailer : sendEmail : Ends ");
	}
}
