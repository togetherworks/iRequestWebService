package com.ncsi.olr.webservice.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimerTask;

import com.ncsi.olr.webservice.IRequestVO;
import com.ncsi.olr.webservice.dao.IRequestWSDAO;

public class IRequestWSUsageReport extends TimerTask {

	public void run() {
		
		Logger.getLogger().error("Starting the IRequestWSUsageReport Timer Task");

		IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
		HashMap lvMasterMap = new HashMap();

		IRequestVO lvIRequestVO = new IRequestVO();
		try {
			
			String lvReportFlag = IRequestWSProperties.getProperty("REPORT_FLAG");
			
			if(lvReportFlag.equalsIgnoreCase("true"))
			{
				//last 7 days
				lvIRequestWSDAO.generateUsageReport(7, 0, lvMasterMap);
				Logger.getLogger().debug("IRequestWSUsageReport lvMasterMap : after last 7 days : "+lvMasterMap);
				
				//daily for the past week
				for(int i = 0;i < 7;i++)
				{
					lvIRequestWSDAO.generateUsageReport(i, i, lvMasterMap);
				}
				Logger.getLogger().debug("RequestWSUsageReport lvMasterMap : after daily : "+lvMasterMap);
				
				//last 30 days
				lvIRequestWSDAO.generateUsageReport(30, 0, lvMasterMap);
				Logger.getLogger().debug("IRequestWSUsageReport lvMasterMap : after last 30 days : "+lvMasterMap);
				
				//last 90 days
				lvIRequestWSDAO.generateUsageReport(90, 0, lvMasterMap);
				Logger.getLogger().debug("IRequestWSUsageReport lvMasterMap : after last 90 days : "+lvMasterMap);
				
				StringBuffer lvReport = new StringBuffer();
				//Last 24 hours
				lvReport.append("<font size = \"3\">");
				lvReport.append("<font face = \"Calibri\">");
				lvReport.append("<b> Usage Report for the I-Request Web Service </b> <br><br> ");
				lvReport.append("<u>Usage statistics for the last 1 day</u> <br><br> ");
				lvReport.append("<table>");
				lvReport.append("<tr><u><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Service</td><td width=\"150\">Success</td><td width=\"150\">Error</td><td width=\"150\">Common Error Code</td></font></font></u></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Create Incident</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerCreateIncident+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerCreateIncident+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerCreateIncident+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Log Notes</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerLogNotes+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerLogNotes+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerLogNotes+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Product</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerSelectProduct+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerSelectProduct+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerSelectProduct+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Problem Type</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerSelectProblemType+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerSelectProblemType+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get("0-0")).get(IRequestWSConstants.CallerSelectProblemType+"ErrorCode")+"</td></tr>");
				lvReport.append("</table>");
				lvReport.append("</font>");
				lvReport.append("</font>");
				lvReport.append("<br> <br>");
				
				//Last 7 days
				lvReport.append("<font size = \"3\">");
				lvReport.append("<font face = \"Calibri\">");
				lvReport.append("<u>Usage statistics for the last 7 days</u> <br><br> ");
				lvReport.append("<table>");
				lvReport.append("<tr><u><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Service</td><td width=\"150\">Success</td><td width=\"150\">Error</td><td width=\"150\">Common Error Code</td></font></font></u></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Create Incident</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerCreateIncident+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerCreateIncident+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerCreateIncident+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Log Notes</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerLogNotes+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerLogNotes+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerLogNotes+"Error")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Product</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerSelectProduct+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerSelectProduct+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerSelectProduct+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Problem Type</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerSelectProblemType+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerSelectProblemType+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_7_Days)).get(IRequestWSConstants.CallerSelectProblemType+"Error")+"</td></tr>");
				lvReport.append("</table>");
				lvReport.append("</font>");
				lvReport.append("</font>");
				lvReport.append("<br> <br>");
				
				//Last 30 days
				lvReport.append("<font size = \"3\">");
				lvReport.append("<font face = \"Calibri\">");
				lvReport.append("<u>Usage statistics for the last 30 days</u> <br><br> ");
				lvReport.append("<table>");
				lvReport.append("<tr><u><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Service</td><td width=\"150\">Success</td><td width=\"150\">Error</td><td width=\"150\">Common Error Code</td></font></font></u></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Create Incident</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerCreateIncident+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerCreateIncident+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerCreateIncident+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Log Notes</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerLogNotes+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerLogNotes+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerLogNotes+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Product</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerSelectProduct+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerSelectProduct+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerSelectProduct+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Problem Type</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerSelectProblemType+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerSelectProblemType+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_1_Month)).get(IRequestWSConstants.CallerSelectProblemType+"ErrorCode")+"</td></tr>");
				lvReport.append("</table>");
				lvReport.append("</font>");
				lvReport.append("</font>");
				lvReport.append("<br> <br>");
				
				//Last 90 days
				lvReport.append("<font size = \"3\">");
				lvReport.append("<font face = \"Calibri\">");
				lvReport.append("<u>Usage statistics for the last 90 days</u> <br><br> ");
				lvReport.append("<table>");
				lvReport.append("<tr><u><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Service</td><td width=\"150\">Success</td><td width=\"150\">Error</td><td width=\"150\">Common Error Code</td></font></font></u></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Create Incident</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerCreateIncident+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerCreateIncident+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerCreateIncident+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Log Notes</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerLogNotes+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerLogNotes+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerLogNotes+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Product</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerSelectProduct+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerSelectProduct+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerSelectProduct+"ErrorCode")+"</td></tr>");
				lvReport.append("<tr><font size = \"3\"><font face = \"Calibri\"><td width=\"200\">Select Problem Type</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerSelectProblemType+"Success")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerSelectProblemType+"Error")+"</td><td width=\"150\">"+((HashMap)lvMasterMap.get(IRequestWSConstants.Usage_Report_Last_3_Months)).get(IRequestWSConstants.CallerSelectProblemType+"ErrorCode")+"</td></tr>");
				lvReport.append("</table>");
				lvReport.append("</font>");
				lvReport.append("</font>");
				lvReport.append("<br> <br>");
				
				Logger.getLogger().debug("IRequestWSUsageReport lvReport : "+lvReport);
				
				Mailer lvMailer = new Mailer();
				ArrayList lvMailList = new ArrayList();
				lvMailList = lvIRequestWSDAO.getCommonMaster(lvIRequestVO, IRequestWSConstants.CommonMaster_Report_Mailing_List, null);
				lvMailList = IRequestWSUtilities.listFromSeparated(lvMailList.get(0).toString(), ';');
				String lvSender = IRequestWSProperties.getProperty("MAIL_SENDER");
				lvMailer.sendEmail(lvSender, lvMailList, "IRequest Web Service Usage Report", lvReport.toString());
				Calendar lvdate = Calendar.getInstance();
				lvIRequestWSDAO.updateCommonMaster(IRequestWSConstants.CommonMaster_Report_Last_Run_Time, lvdate.getTime().toString());
			}
		} catch (Exception e) {
			ErrorNotification.sendErrorNotification("1402", "Exception while generating the usage report.", null);
			e.printStackTrace();
		}

		Logger.getLogger().error("Ending the IRequestWSUsageReport Timer Task");
	}

}
