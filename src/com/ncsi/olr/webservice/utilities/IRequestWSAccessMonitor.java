package com.ncsi.olr.webservice.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.ncsi.olr.webservice.IRequestVO;
import com.ncsi.olr.webservice.dao.IRequestWSDAO;
import com.ncsi.olr.webservice.servlet.IRequestWSStartup;

public class IRequestWSAccessMonitor extends TimerTask {

	public void run() {
		
		Logger.getLogger().debug("Starting the IRequestWSAccessMonitor Timer Task");
		Logger.getLogger().debug("IRequestWSAccessMonitor : IRequestWSStartup.requestCounterSelect : "+IRequestWSStartup.requestCounterSelect);
		Logger.getLogger().debug("IRequestWSAccessMonitor : allowed max count select : "+IRequestWSProperties.getPropertyInt("ALLOWED_ACCESS_COUNT_SELECT"));
		Logger.getLogger().debug("IRequestWSAccessMonitor : IRequestWSStartup.requestCounterUpdate : "+IRequestWSStartup.requestCounterUpdate);
		Logger.getLogger().debug("IRequestWSAccessMonitor : allowed max count update : "+IRequestWSProperties.getPropertyInt("ALLOWED_ACCESS_COUNT_UPDATE"));
		try {
			
			IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
			int countercurrentvalselect = IRequestWSStartup.requestCounterSelect;
			
			if(countercurrentvalselect >= IRequestWSProperties.getPropertyInt("ALLOWED_ACCESS_COUNT_SELECT"))
			{
				lvIRequestWSDAO.updateCommonMaster(IRequestWSConstants.CommonMaster_Global_Access_Allowed_Select, "false");
				Logger.getLogger().error("IRequestWSAccessMonitor : IRequestWSStartup.requestCounterSelect Locking the account ");
				ErrorNotification.sendErrorNotification("1402", "Exceeded number of allowed requests - Selects", null);
			}
			else
			{
				IRequestVO lvIRequestVO = new IRequestVO();
				ArrayList lvTempList = lvIRequestWSDAO.getCommonMaster(lvIRequestVO, IRequestWSConstants.CommonMaster_Global_Access_Allowed_Select, null);
				String globalAccessAllowed = lvTempList.get(0).toString();
				if(globalAccessAllowed != null && globalAccessAllowed.equalsIgnoreCase("false"))
				{
					Logger.getLogger().error("IRequestWSAccessMonitor : IRequestWSStartup.requestCounterSelect Releasing the account ");
					lvIRequestWSDAO.updateCommonMaster(IRequestWSConstants.CommonMaster_Global_Access_Allowed_Select, "true");
					ErrorNotification.sendErrorNotification("1402", "Releasing the lock - Selects", null);
				}

			}
			IRequestWSStartup.requestCounterSelect = 0;
			
			int countercurrentvalupdate = IRequestWSStartup.requestCounterUpdate;
			if(countercurrentvalupdate >= IRequestWSProperties.getPropertyInt("ALLOWED_ACCESS_COUNT_UPDATE"))
			{
				lvIRequestWSDAO.updateCommonMaster(IRequestWSConstants.CommonMaster_Global_Access_Allowed_Update, "false");
				Logger.getLogger().error("IRequestWSAccessMonitor : IRequestWSStartup.requestCounterUpdate Locking the account ");
				ErrorNotification.sendErrorNotification("1402", "Exceeded number of allowed requests - Updates", null);
			}
			else
			{
				IRequestVO lvIRequestVO = new IRequestVO();
				ArrayList lvTempList = lvIRequestWSDAO.getCommonMaster(lvIRequestVO, IRequestWSConstants.CommonMaster_Global_Access_Allowed_Update, null);
				String globalAccessAllowed = lvTempList.get(0).toString();
				if(globalAccessAllowed != null && globalAccessAllowed.equalsIgnoreCase("false"))
				{
					Logger.getLogger().error("IRequestWSAccessMonitor : IRequestWSStartup.requestCounterUpdate Releasing the account ");
					lvIRequestWSDAO.updateCommonMaster(IRequestWSConstants.CommonMaster_Global_Access_Allowed_Update, "true");
					ErrorNotification.sendErrorNotification("1402", "Releasing the lock - Updates", null);
				}
			}
			IRequestWSStartup.requestCounterUpdate = 0;
			
			Timer AccessMonitor = new Timer();
			AccessMonitor.schedule(new IRequestWSAccessMonitor(), IRequestWSProperties.getPropertyInt("ACCESS_MONITOR_INTERVAL"));
			Calendar lvdate = Calendar.getInstance();
			lvIRequestWSDAO.updateCommonMaster(IRequestWSConstants.CommonMaster_Usage_Monitor_Last_Run_Time, lvdate.getTime().toString());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
		}
		Logger.getLogger().debug("Ending the IRequestWSAccessMonitor Timer Task");
	}

}
