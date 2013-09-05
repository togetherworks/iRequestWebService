package com.ncsi.olr.webservice.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class OLRProperties {

	private static Properties coOLRSysProperties = new Properties();

	private FileInputStream coPropFile = null;

	public OLRProperties()
	{
		Logger.getLogger().debug("Into the Properties file");
		loadOLRProperties();
	}

	private void loadOLRProperties(){
		long lvStartTime = System.currentTimeMillis();
		Logger.getLogger().debug("Enters the method loadOLRProperties");
		try {
		String propFile = System.getProperty("OLR.prop");
		Logger.getLogger().debug("loadOLRProperties : propFile : "+propFile);
		
			coPropFile = new FileInputStream(propFile);
			coOLRSysProperties.load(coPropFile);
			Logger.getLogger().debug("After successfully loading the file");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger().debug("Inside the FilenotFound Exception");
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(ex), null);
		}
		catch(IOException IOex)
		{
			IOex.printStackTrace();
			Logger.getLogger().debug("Inside the ioException");
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(IOex), null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
		}

		Logger.getLogger().debug("Leaves the method loadOLRProperties");
		long lvEndTime = System.currentTimeMillis();
		Logger.getLogger().debug("Time Taken in the method loadOLRProperties" + (lvEndTime-lvStartTime));
	} // end of method loadKWGProperties

	public static String getProperty(String psKey)
	{
		String wsValue = (String) coOLRSysProperties.get(psKey);
		return wsValue;
	}

	public static int getPropertyInt(String psKey) {
		int wsValue = Integer.parseInt((String) coOLRSysProperties.get(psKey));
		return wsValue;
	}

}
