package com.ncsi.olr.webservice.servlet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.ncsi.olr.webservice.utilities.ErrorNotification;
import com.ncsi.olr.webservice.utilities.IRequestWSAccessMonitor;
import com.ncsi.olr.webservice.utilities.IRequestWSProperties;
import com.ncsi.olr.webservice.utilities.IRequestWSUsageReport;
import com.ncsi.olr.webservice.utilities.IRequestWSUtilities;
import com.ncsi.olr.webservice.utilities.Logger;
import com.ncsi.olr.webservice.utilities.OLRProperties;



public class IRequestWSStartup extends HttpServlet
{

	private static final long serialVersionUID = -569288071113104821L;
	
	public static int requestCounterSelect = 0;
	
	public static int requestCounterUpdate = 0;
	
	public void init(ServletConfig config) throws ServletException
	{
		// Store the ServletConfig object and log the initialization
		super.init(config);
		try
		{
			new IRequestWSProperties();
			Logger.getLogger().debug("IRequestWSProperties Instance created successfully");
			new OLRProperties();
			Logger.getLogger().debug("OLRProperties Instance created successfully");
			Timer AccessMonitor = new Timer();
			AccessMonitor.schedule(new IRequestWSAccessMonitor(), IRequestWSProperties.getPropertyInt("ACCESS_MONITOR_INTERVAL"));
		    Calendar date = Calendar.getInstance();
		    //Logger.getLogger().debug("IRequestWSStartup lvReportScheduler : date "+date.getTime());
		    date.set(Calendar.AM_PM, Calendar.AM);
		    date.set(Calendar.HOUR, 0);
		    date.set(Calendar.MINUTE, 0);
		    date.set(Calendar.SECOND, 0);
		    date.set(Calendar.MILLISECOND, 0);
		    //Logger.getLogger().debug("IRequestWSStartup lvReportScheduler : date "+date.getTime());
		    //Logger.getLogger().debug("IRequestWSStartup Report Scheduler date : "+date.getTime());
		    String lvtempInterval = IRequestWSProperties.getProperty("REPORTING_INTERVAL");
		    ArrayList intervalList = IRequestWSUtilities.listFromSeparated(lvtempInterval, '*');
		    Iterator lvIterator = intervalList.iterator();
		    long lvReportScheduler = 604800000;
		    long i = 1;
		    while(lvIterator.hasNext())
		    {
		    	i=i*(new Integer(lvIterator.next().toString()).longValue());
		    }
		    if(i > 1)
		    {
		    	lvReportScheduler = i;
		    }
		    //Logger.getLogger().debug("IRequestWSStartup lvReportScheduler : "+lvReportScheduler);
		    int weekday = date.get(Calendar.DAY_OF_WEEK);  
		    int weekdayForRun = 1;
		    //Logger.getLogger().debug("IRequestWSStartup Report Scheduler weekday : "+weekday);
		    String lvTimeForRun = IRequestWSProperties.getProperty("REPORTING_TIME");
		    String lvDayForRun = IRequestWSProperties.getProperty("REPORTING_DAY");
		    if(lvDayForRun.equalsIgnoreCase("Sunday"))
		    {
		    	weekdayForRun = 1;
		    }
		    else if(lvDayForRun.equalsIgnoreCase("Monday"))
		    {
		    	weekdayForRun = 2;
		    }
		    else if(lvDayForRun.equalsIgnoreCase("Tuesday"))
		    {
		    	weekdayForRun = 3;
		    } 
		    else if(lvDayForRun.equalsIgnoreCase("Wednesday"))
		    {
		    	weekdayForRun = 4;
		    }
		    else if(lvDayForRun.equalsIgnoreCase("Thursday"))
		    {
		    	weekdayForRun = 5;
		    }
		    else if(lvDayForRun.equalsIgnoreCase("Friday"))
		    {
		    	weekdayForRun = 6;
		    }
		    else if(lvDayForRun.equalsIgnoreCase("Saturday"))
		    {
		    	weekdayForRun = 7;
		    }
		    else
		    {
		    	weekdayForRun = 1;
		    }
		    ArrayList lvTimeForRunList = IRequestWSUtilities.listFromSeparated(lvTimeForRun, ':');
		    int hour = new Integer(lvTimeForRunList.get(0).toString()).intValue();
		    int minute = new Integer(lvTimeForRunList.get(1).toString()).intValue();
		    int second = new Integer(lvTimeForRunList.get(2).toString()).intValue();
		    //Logger.getLogger().debug("IRequestWSStartup Report Scheduler hour : "+hour+" : minute : "+minute+" : second : "+second);
		    
		    int days = weekdayForRun - weekday;  
		    //Logger.getLogger().debug("IRequestWSStartup Report Scheduler days : "+days);
		    if (days < 0)  
		    {  
		        // this will usually be the case since Calendar.SUNDAY is the smallest  
		        days += 7;  
		    }  
		    
		    date.add(Calendar.DAY_OF_YEAR, days);
		    //Logger.getLogger().debug("IRequestWSStartup Report Scheduler days : "+days);
		    //date.set(Calendar.HOUR, 24);
		    //date.set(Calendar.MINUTE, 0);
		    //date.set(Calendar.SECOND, 0);
		    //date.set(Calendar.MILLISECOND, 0);
		    date.set(Calendar.HOUR, hour);
		    date.set(Calendar.MINUTE, minute);
		    date.set(Calendar.SECOND, second);
		    date.set(Calendar.MILLISECOND, 0);
		    Logger.getLogger().warning("IRequestWSStartup Report Scheduler First Run Time : "+date.getTime());
		    Logger.getLogger().warning("IRequestWSStartup Report Scheduler Interval : "+lvReportScheduler);
		    AccessMonitor.scheduleAtFixedRate(
		      new IRequestWSUsageReport(),
		      date.getTime(),
		      lvReportScheduler
		    );
			Logger.getLogger().debug("IRequestWSStartup IRequestWSAccessMonitor timer task initialized successfully");
			
			Logger.getLogger().warning("=================================================================");
			Logger.getLogger().warning("=                                                               =");
			Logger.getLogger().warning("=                    IRequestWebService                         =");
			Logger.getLogger().warning("=                         Clarify                               =");
			Logger.getLogger().warning("=                                                               =");
			Logger.getLogger().warning("=================================================================");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.getLogger().debug(e.getMessage());
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
		}
	}
}
