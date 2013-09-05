/**
 * 
 */
package com.ncsi.olr.webservice;

import com.ncsi.olr.webservice.utilities.Logger;


/**
 * @author Paul Panjikaran
 * Class that will expose the IRequest web services.
 * All methods will take as input XML Strings and respond likewise.
 *
 */
public class IRequestWebService  {
	

	
	public IRequestWebService() {
		// TODO Auto-generated constructor stub
	}
	
	public String createIncident(String aInboundXML)
	{
		Logger.getLogger().debug("IRequestWebService : createIncident starts : ");
		
		IRequestWebServiceHelper loIRequestWebServiceHelper = new IRequestWebServiceHelper();
		
		String lvOutboundXML = loIRequestWebServiceHelper.createIncident(aInboundXML);
		
		Logger.getLogger().debug("IRequestWebService : createIncident ends : "+lvOutboundXML);
		
		return  lvOutboundXML;
	}
	
	public String logNotes(String aInboundXML)
	{
		Logger.getLogger().debug("IRequestWebService : logNotes starts : ");
		
		IRequestWebServiceHelper loIRequestWebServiceHelper = new IRequestWebServiceHelper();
		
		String lvOutboundXML = loIRequestWebServiceHelper.logNotes(aInboundXML);
		
		Logger.getLogger().debug("IRequestWebService : logNotes ends : "+lvOutboundXML);
		
		return  lvOutboundXML;
	}
	
	public String selectProblemType(String aInboundXML)
	{
		Logger.getLogger().debug("IRequestWebService : selectProblemType starts : ");
		
		IRequestWebServiceHelper loIRequestWebServiceHelper = new IRequestWebServiceHelper();
		
		String lvOutboundXML = loIRequestWebServiceHelper.selectProblemType(aInboundXML);
		
		Logger.getLogger().debug("IRequestWebService : selectProblemType ends : "+lvOutboundXML);
		
		return  lvOutboundXML;
	}
	
	public String selectProduct(String aInboundXML)
	{
		Logger.getLogger().debug("IRequestWebService : selectProduct starts : ");
		
		IRequestWebServiceHelper loIRequestWebServiceHelper = new IRequestWebServiceHelper();
		
		String lvOutboundXML = loIRequestWebServiceHelper.selectProduct(aInboundXML);
		
		Logger.getLogger().debug("IRequestWebService : selectProduct ends : "+lvOutboundXML);
		
		return  lvOutboundXML;
	}



}
