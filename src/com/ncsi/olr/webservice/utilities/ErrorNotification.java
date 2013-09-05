package com.ncsi.olr.webservice.utilities;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class ErrorNotification {
	
	static String ErrorCode = "1402";
	static String ErrorMessage = "";
	static String ExceptionStackTrace = "";
	static String MailingList = IRequestWSProperties.getProperty("ERROR_MAILING_LIST");
	static String FromEmailAddress = IRequestWSProperties.getProperty("MAIL_SENDER");
	static String EmailSendFlag = IRequestWSProperties.getProperty("ERROR_NOTIFICATION");
	static String ServerIP = IRequestWSProperties.getProperty("APPLICATION_SERVER_IP");
	
	public static void sendErrorNotification(String aErrorCode, String aExceptionStackTrace, String aIRequestWSMasterObjid)
	{
		Logger.getLogger().warning("ErrorNotification : sendErrorNotification : Starts ");
		
		if(EmailSendFlag != null && EmailSendFlag.equalsIgnoreCase("true"))
		{
			ErrorCode = aErrorCode;
			ExceptionStackTrace = aExceptionStackTrace;

			try {
				ArrayList lvMailingList = IRequestWSUtilities.listFromSeparated(MailingList, ';');

				String lvErrorEmailSubject = "IRequest Web Service - Error Notification Mailer - Server IP : "+ServerIP;

				StringBuffer lvErrorEmailBody = new StringBuffer();
				lvErrorEmailBody.append("Application Server ip = "+ServerIP+"\n\n");
				lvErrorEmailBody.append("IRequestWSMaster Objid = "+aIRequestWSMasterObjid+"\n\n");
				lvErrorEmailBody.append("Error Code = "+ErrorCode+"\n\n");
				ResourceBundle lvErrorCodeResourceBundle = ResourceBundle.getBundle("com.ncsi.olr.webservice.property.IRequestWSErrorCodesMapping");
				ErrorMessage = lvErrorCodeResourceBundle.getString(aErrorCode);
				lvErrorEmailBody.append("Error Message = "+ErrorMessage+"\n\n");
				lvErrorEmailBody.append("Exception StackTrace : \n\n"+ExceptionStackTrace+"\n\n");

				Mailer lvMailer = new Mailer();
				lvMailer.sendEmail(FromEmailAddress, lvMailingList, lvErrorEmailSubject, lvErrorEmailBody.toString());
			} catch (Exception e) {

				Logger.getLogger().error("ErrorNotification : sendErrorNotification : Generic Exception Block. This would be really bad... ");
				e.printStackTrace();
			}
		}
		
		Logger.getLogger().debug("ErrorNotification : sendErrorNotification : Ends ");
	}

}
