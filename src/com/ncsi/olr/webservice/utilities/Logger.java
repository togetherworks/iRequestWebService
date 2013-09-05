/**
 * 
 */
package com.ncsi.olr.webservice.utilities;

import weblogic.logging.NonCatalogLogger;

/**
 * @author Paul Panjikaran
 * This class will be used for all logging for the IRequest web service.
 *
 */
public class Logger {

	private static Logger appLogger = null;
    private NonCatalogLogger wlLogger;
    private static int csIRequestSeverity = IRequestWSProperties.getPropertyInt("LOGGING_LEVEL");
    

    public static void initialize(String s)
    {
        appLogger = new Logger(s);
    }

    private Logger(String s)
    {
        wlLogger = null;
        wlLogger = new NonCatalogLogger(s);
        System.out.println(s + " IRequestWS Logger Initialized .......");
        wlLogger.info(s + " IRequestWS Logger Initialized ...");
    }

    public static Logger getLogger()
    {
        if(appLogger == null)
        {
            initialize("IRequestWS");
        }
        return appLogger;
    }

    public void debug(String s)
    {
        if(csIRequestSeverity == 256)
        {
            wlLogger.debug(s);
        }
    }

    public void debug(String s, Throwable throwable)
    {
        if(csIRequestSeverity == 256)
        {
            throwable.printStackTrace();
            wlLogger.debug(s, throwable);
        }
    }

    public void info(String s)
    {
    	if(csIRequestSeverity == 256 || csIRequestSeverity == 16)
        {
            wlLogger.info(s);
        }
    }

    public void info(String s, Throwable throwable)
    {
    	if(csIRequestSeverity == 256 || csIRequestSeverity == 16)
        {
            throwable.printStackTrace();
            wlLogger.info(s, throwable);
        }
    }

    public void warning(String s)
    {
    	if(csIRequestSeverity == 256 || csIRequestSeverity == 16 || csIRequestSeverity == 4)
        {
            wlLogger.warning(s);
        }
    }

    public void warning(String s, Throwable throwable)
    {
    	if(csIRequestSeverity == 256 || csIRequestSeverity == 16 || csIRequestSeverity == 4)
        {
            throwable.printStackTrace();
            wlLogger.warning(s, throwable);
        }
    }

    public void error(String s)
    {
    	if(csIRequestSeverity == 256 || csIRequestSeverity == 16 || csIRequestSeverity == 4 || csIRequestSeverity == 2)
        {
            wlLogger.error(s);
        }
    }

    public void error(String s, Throwable throwable)
    {
    	if(csIRequestSeverity == 256 || csIRequestSeverity == 16 || csIRequestSeverity == 4 || csIRequestSeverity == 2)
        {
            throwable.printStackTrace();
            wlLogger.error(s, throwable);
        }
    }

    public static void main(String args[])
    {
        initialize("irequestws");
        getLogger().debug("Hello");
    }

    public static void setSeverity(int i)
    {
    	csIRequestSeverity = i;
        System.out.println("Severity Level ->" + csIRequestSeverity);
    }

}
