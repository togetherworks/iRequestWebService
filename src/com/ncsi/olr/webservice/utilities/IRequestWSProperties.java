package com.ncsi.olr.webservice.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class IRequestWSProperties {

	private static Properties coOLRSysProperties = new Properties();

	private FileInputStream coPropFile = null;

	public IRequestWSProperties()
	{
		//Logger.getLogger().debug("Into the Properties file");
		loadIRequestProperties();
	}

	private void loadIRequestProperties(){
		//Logger.getLogger().debug("Enters the method loadIRequestProperties");
		try {
		String propFile = System.getProperty("IRequestWS.prop");
		//Logger.getLogger().debug("loadIRequestProperties : propFile : "+propFile);
		
			coPropFile = new FileInputStream(propFile);
			coOLRSysProperties.load(coPropFile);
			//Logger.getLogger().debug("After successfully loading the file");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(ex), null);
		}
		catch(IOException IOex)
		{
			IOex.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(IOex), null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
		}
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
