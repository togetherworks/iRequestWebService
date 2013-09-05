package com.ncsi.olr.webservice.utilities;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import java.util.ArrayList;

public class IRequestWSErrorHandler implements ErrorHandler {

	public ArrayList lvArrayList = new ArrayList();


	public void error(SAXParseException e) 
			throws SAXException {
		Logger.getLogger().warning( "IRequestWSErrorHandler : Error Line " + e.getLineNumber() + "..." );
		lvArrayList.add("Error on line "+e.getLineNumber() + "..."+e.getMessage());
		Logger.getLogger().error( e.getMessage() );
	}

	public void fatalError(SAXParseException e) 
			throws SAXException {
		Logger.getLogger().warning( "IRequestWSErrorHandler : Fatal Error Line " + e.getLineNumber() + "..." );
		lvArrayList.add("Error on line "+e.getLineNumber() + "..."+e.getMessage());
		Logger.getLogger().error( e.getMessage() );
	}

	public void warning(SAXParseException e) 
			throws SAXException {
		Logger.getLogger().warning( "IRequestWSErrorHandler : Warning Line " + e.getLineNumber() + "..." );
		//lvArrayList.add("Error on line "+e.getLineNumber() + "..."+e.getMessage());
		Logger.getLogger().error( e.getMessage() );
	}

	public ArrayList getErrorList()
	{
		return this.lvArrayList;
	}

}
