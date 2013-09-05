package com.ncsi.olr.webservice.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ncsi.olr.webservice.IRequestVO;
import com.ncsi.olr.webservice.dao.IRequestWSDAO;



public class IRequestWSUtilities {
	
	static Logger ioLogger = Logger.getLogger();
	
	public static Document convertStringToDoc(String aInboundXML) throws ParserConfigurationException, SAXException, SAXParseException, IOException, Exception
	{
    	ioLogger.debug("IRequestWSUtilities : convertStringToDoc : Starts ");
        Document loXMLDocument = null;

        DocumentBuilderFactory loFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder loBuilder = loFactory.newDocumentBuilder();
        loXMLDocument = loBuilder.parse(new InputSource(new StringReader(aInboundXML)));
       
        ioLogger.debug("IRequestWSUtilities : convertStringToDoc : Ends ");
        return loXMLDocument;
		
	}
	
	public static boolean validateXMLWithXSD(String lvIRequestXMLDocument, IRequestVO lvIRequestVO) throws SAXException , IOException
	{
		ioLogger.debug("IRequestWSUtilities : validateXMLWithXSD : Starts ");
		boolean validateXML = true;

		String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		String W3C_XML_SCHEMA =    "http://www.w3.org/2001/XMLSchema";

		//new IRequestWSProperties();
		String lvSchemaUrl = IRequestWSProperties.getProperty("IREQUEST_XSD_PATH")+IRequestWSConstants.IRequestWSSchema;
		ioLogger.debug("IRequestWSUtilities : validateXMLWithXSD : lvSchemaUrl "+lvSchemaUrl);

		DocumentBuilderFactory loDocBuildFactory = DocumentBuilderFactory.
				newInstance();

		loDocBuildFactory.setNamespaceAware(true);
		loDocBuildFactory.setValidating(true);
		loDocBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		loDocBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE,lvSchemaUrl);

		IRequestWSErrorHandler lvIRequestWSErrorHandler = new IRequestWSErrorHandler();

		DocumentBuilder docBuilder = null;
		try {
			docBuilder = loDocBuildFactory.newDocumentBuilder();
			docBuilder.setErrorHandler (lvIRequestWSErrorHandler);
		} catch (ParserConfigurationException e) {
			ioLogger.error("IRequestWSUtilities : lvIRequestWSErrorHandler : ParserConfigurationException : ");
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), lvIRequestVO.getIRequestWSMasterObjId());
		}
		try {
			docBuilder.parse(new InputSource(new StringReader(lvIRequestXMLDocument)));
		} catch (Exception e) {
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), lvIRequestVO.getIRequestWSMasterObjId());
		}

		if(lvIRequestWSErrorHandler != null && lvIRequestWSErrorHandler.getErrorList() != null && lvIRequestWSErrorHandler.getErrorList().size() > 0)
		{
			ioLogger.warning("IRequestWSUtilities : lvIRequestWSErrorHandler : lvIRequestWSErrorHandler.getErrorList() :  "+lvIRequestWSErrorHandler.getErrorList());
			lvIRequestVO.setXSDValidationError(lvIRequestWSErrorHandler.getErrorList());
			validateXML = false;

		}

		ioLogger.debug("IRequestWSUtilities : validateXMLWithXSD : Ends ");
		return validateXML;
	}
	
	
	
	public static String convertDocToString(Document poXmlDocument) throws SAXException {
		ioLogger.debug("IRequestWSUtilities : convertDocToString : Starts ");

		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = transfac.newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e1), null);
		}
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty(OutputKeys.ENCODING, IRequestWSConstants.Encoding);
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(poXmlDocument);
		try {
			trans.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
		}
		String xmlString = sw.toString();
		ioLogger.debug("IRequestWSUtilities : convertDocToString : Ends ");
		return xmlString;
	}

	  public static Document constructResponseXML(IRequestVO aIRequestVO,
			  String lvCallerMethod) {
		  
		  ioLogger.debug("IRequestWSUtilities : constructResponseXML : Starts ");

		  String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		  String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		  String W3C_XML_SCHEMA =    "http://www.w3.org/2001/XMLSchema";

		  String lvSchemaUrl = IRequestWSProperties.getProperty("IREQUEST_XSD_PATH")+IRequestWSConstants.IRequestWSSchema;
		  ioLogger.debug("IRequestWSUtilities : constructResponseXML : lvSchemaUrl "+lvSchemaUrl);



		  DocumentBuilderFactory loDocBuildFactory = DocumentBuilderFactory.
				  newInstance();

		  loDocBuildFactory.setNamespaceAware(true);
		  loDocBuildFactory.setValidating(true);
		  loDocBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		  loDocBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE,lvSchemaUrl);

		  Document doc = null;
		  try {
			  DocumentBuilder docBuilder = loDocBuildFactory.newDocumentBuilder();
			  doc = docBuilder.newDocument();

			  if(lvCallerMethod != null && !lvCallerMethod.equals("") && lvCallerMethod.equals(IRequestWSConstants.CallerCreateIncident))
			  {
				  Element transaction = doc.createElement("xs:CreateIncidentResponse");
				  transaction.setAttribute("xmlns:xs", "http://www.example.org/IRequest");
				  transaction.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				  doc.appendChild(transaction);
				  doc = constructHeader1(aIRequestVO, doc);
				  if(!(aIRequestVO.getError() != null && !aIRequestVO.getError().isEmpty()) && aIRequestVO.isInboundXMLValidated())
				  {
					  doc = constructHeader2(aIRequestVO, doc);
				  }
			  }
			  else if(lvCallerMethod != null && !lvCallerMethod.equals("") && lvCallerMethod.equals(IRequestWSConstants.CallerLogNotes))
			  {
				  Element transaction = doc.createElement("xs:LogNotesResponse");
				  transaction.setAttribute("xmlns:xs", "http://www.example.org/IRequest");
				  transaction.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				  doc.appendChild(transaction);
				  doc = constructHeader1(aIRequestVO, doc);
				  if(!(aIRequestVO.getError() != null && !aIRequestVO.getError().isEmpty()) && aIRequestVO.isInboundXMLValidated())
				  {
					  Element IncidentId = doc.createElement("xs:IncidentId");
					  transaction.appendChild(IncidentId);
					  IncidentId.appendChild(doc.createTextNode(aIRequestVO.getClarifyBOVO().getIncidentId().toString()));
				  }
			  }
			  else if(lvCallerMethod != null && !lvCallerMethod.equals("") && lvCallerMethod.equals(IRequestWSConstants.CallerSelectProblemType))
			  {
				  Element transaction = doc.createElement("xs:SelectProblemTypeResponse");
				  transaction.setAttribute("xmlns:xs", "http://www.example.org/IRequest");
				  transaction.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				  doc.appendChild(transaction);
				  doc = constructHeader1(aIRequestVO, doc);
				  if(!(aIRequestVO.getError() != null && !aIRequestVO.getError().isEmpty()) && aIRequestVO.isInboundXMLValidated())
				  {
					  Iterator lvIterator = aIRequestVO.getClarifyBOVO().getProblem_Type_List().iterator();
					  while(lvIterator.hasNext())
					  {
						  String lvProblemType = (String)lvIterator.next();
						  Element ProblemTypeList = doc.createElement("xs:ProblemTypeList");
						  transaction.appendChild(ProblemTypeList);
						  ProblemTypeList.appendChild(doc.createTextNode(lvProblemType));
					  }
				  }
			  }
			  else if(lvCallerMethod != null && !lvCallerMethod.equals("") && lvCallerMethod.equals(IRequestWSConstants.CallerSelectProduct))
			  {
				  Element transaction = doc.createElement("xs:SelectProductResponse");
				  transaction.setAttribute("xmlns:xs", "http://www.example.org/IRequest");
				  transaction.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				  doc.appendChild(transaction);
				  doc = constructHeader1(aIRequestVO, doc);
				  if(!(aIRequestVO.getError() != null && !aIRequestVO.getError().isEmpty()) && aIRequestVO.isInboundXMLValidated())
				  {
					  Iterator lvIterator = aIRequestVO.getClarifyBOVO().getProduct_List().iterator();
					  while(lvIterator.hasNext())
					  {
						  String lvProduct = (String)lvIterator.next();
						  Element ProductList = doc.createElement("xs:ProductList");
						  transaction.appendChild(ProductList);
						  ProductList.appendChild(doc.createTextNode(lvProduct));
					  }
				  }
			  }

			  String tempString = convertDocToString(doc);
			  validateXMLWithXSD(tempString, aIRequestVO);

		  }
		  catch (Exception e) {
			  e.printStackTrace();
			  ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), aIRequestVO.getIRequestWSMasterObjId());
		  }

		  ioLogger.debug("IRequestWSUtilities : constructResponseXML : Ends ");
		  return doc;
	  }
	  
	  public static Document constructHeader1(IRequestVO aIRequestVO, Document aXMLDocument)
	  {
		  ioLogger.debug("IRequestWSUtilities : constructHeader1 : Starts ");

		  ArrayList loErrorList = aIRequestVO.getError();
		  ArrayList loXMLValidationErrorList = aIRequestVO.getXSDValidationError();
		  
		  boolean transactionSuccess = true;

		  try {
			  Element transaction = aXMLDocument.getDocumentElement();
			  Element Header1 = aXMLDocument.createElement("xs:Header1");
			  transaction.appendChild(Header1);
			  Element Status = aXMLDocument.createElement("xs:Status");
			  Header1.appendChild(Status);
			  IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
			  if((loXMLValidationErrorList != null && loXMLValidationErrorList.size() > 0) || (loErrorList != null && loErrorList.size() > 0))
			  {
				  aIRequestVO.setStatus("Error");
				  Status.appendChild(aXMLDocument.createTextNode("Error"));
				  if(loXMLValidationErrorList != null && loXMLValidationErrorList.size() > 0)
				  {
					  transactionSuccess = false;
					  Iterator loIterator = null;
					  loIterator = loXMLValidationErrorList.iterator();
					  int i = 1201;
					  while(loIterator != null && loIterator.hasNext())
					  {
						  Element Error = aXMLDocument.createElement("xs:Error");
						  Header1.appendChild(Error);
						  Element ErrorCode = aXMLDocument.createElement("xs:ErrorCode");
						  Error.appendChild(ErrorCode);
						  if(!aIRequestVO.isInboundXMLValidated())
						  {
							  String lvErrorCode = new Integer(i++).toString();
							  String lvErrorMessage = loIterator.next().toString();
							  ErrorCode.appendChild(aXMLDocument.createTextNode(lvErrorCode));
							  Element ErrorMessage = aXMLDocument.createElement("xs:ErrorMessage");
							  Error.appendChild(ErrorMessage);
							  ErrorMessage.appendChild(aXMLDocument.createTextNode(lvErrorMessage));
							  lvIRequestWSDAO.insertErrorRecord(aIRequestVO, lvErrorCode, lvErrorMessage);
						  }

					  }
				  }
				  if (loErrorList != null && loErrorList.size() > 0)
				  {
					  Iterator loIterator = null;
					  loIterator = loErrorList.iterator();
					  //Status.appendChild(aXMLDocument.createTextNode("Error"));
					  transactionSuccess = false;
					  while(loIterator != null && loIterator.hasNext())
					  {
						  Element Error = aXMLDocument.createElement("xs:Error");
						  Header1.appendChild(Error);
						  Element ErrorCode = aXMLDocument.createElement("xs:ErrorCode");
						  Error.appendChild(ErrorCode);
						  ResourceBundle lvErrorCodeResourceBundle = ResourceBundle.getBundle("com.ncsi.olr.webservice.property.IRequestWSErrorCodesMapping");
						  String lvErrorCode = loIterator.next().toString();
						  String lvErrorMessage = lvErrorCodeResourceBundle.getString(lvErrorCode);
						  ioLogger.debug("IRequestWSUtilities : constructHeader1 : lvErrorCode : "+lvErrorCode);
						  ioLogger.debug("IRequestWSUtilities : constructHeader1 : lvErrorMessage : "+lvErrorMessage);
						  ErrorCode.appendChild(aXMLDocument.createTextNode(lvErrorCode));
						  Element ErrorMessage = aXMLDocument.createElement("xs:ErrorMessage");
						  Error.appendChild(ErrorMessage);
						  ErrorMessage.appendChild(aXMLDocument.createTextNode(lvErrorMessage));
						  lvIRequestWSDAO.insertErrorRecord(aIRequestVO, lvErrorCode, lvErrorMessage);
					  }
				  }
			  }
			  if(transactionSuccess)
			  {
				  aIRequestVO.setStatus("Success");
				  Status.appendChild(aXMLDocument.createTextNode("Success"));
			  }

		  } catch (DOMException e) {
			  e.printStackTrace();
			  ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), aIRequestVO.getIRequestWSMasterObjId());
		  }
		  catch (Exception e)
		  {
			  e.printStackTrace();
			  ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), aIRequestVO.getIRequestWSMasterObjId());
		  }

		  ioLogger.debug("IRequestWSUtilities : constructHeader1 : Ends ");

		  return aXMLDocument;
	  }
	  
	  public static Document constructHeader2(IRequestVO aIRequestVO, Document aXMLDocument)
	  {
		  ioLogger.debug("IRequestWSUtilities : constructHeader2 : Starts ");

		  Element transaction = aXMLDocument.getDocumentElement();
		  Element Header2 = aXMLDocument.createElement("xs:Header2");
		  transaction.appendChild(Header2);
		  Element TransactionId = aXMLDocument.createElement("xs:TransactionId");
		  Header2.appendChild(TransactionId);
		  ioLogger.debug("IRequestWSUtilities : constructHeader2 : TransactionId : "+aIRequestVO.getTransactionId());
		  TransactionId.appendChild(aXMLDocument.createTextNode(aIRequestVO.getTransactionId().toString()));
		  Element IncidentId = aXMLDocument.createElement("xs:IncidentId");
		  Header2.appendChild(IncidentId);
		  ioLogger.debug("IRequestWSUtilities : constructHeader2 : IncidentId : "+aIRequestVO.getClarifyBOVO().getIncidentId());
		  IncidentId.appendChild(aXMLDocument.createTextNode(aIRequestVO.getClarifyBOVO().getIncidentId().toString()));

		  ioLogger.debug("IRequestWSUtilities : constructHeader2 : Ends ");

		  return aXMLDocument;
	  }
	  
	  public static String getTimeStamp()
	  {
		  ioLogger.debug("IRequestWSUtilities : getTimeStamp : Starts ");
		  
		  long timeStamp = System.currentTimeMillis();
		  String timeStampString=String.valueOf(timeStamp);
		  ioLogger.debug("IRequestWSUtilities : getTimeStamp : timeStampString "+timeStampString);
		  ioLogger.debug("IRequestWSUtilities : getTimeStamp : Ends ");

		  return timeStampString;
	  }

	  public static ArrayList listFromSeparated(String lvParentString, char lvMatchCharacter)
	  {
		  //ioLogger.debug("IRequestWSUtilities : listFromSeparated : Starts ");
		  ArrayList lvSeparatedList = new ArrayList();
	      int count = 0;
	      for (int i=0; i < lvParentString.length(); i++)
	      {
	          if (lvParentString.charAt(i) == lvMatchCharacter)
	          {
	               count++;
	          }
	      }
	      //ioLogger.debug("IRequestWSUtilities : listFromSeparated : count : "+count);
	      while(count > 0)
	      {
	    	  String lvTempString = lvParentString.substring(0, lvParentString.indexOf(lvMatchCharacter));
	    	  lvParentString = lvParentString.substring(lvParentString.indexOf(lvMatchCharacter)+1,lvParentString.length());
	    	  //ioLogger.debug("IRequestWSUtilities listFromSeparated : lvTempString : "+lvTempString);
	    	  //ioLogger.debug("IRequestWSUtilities listFromSeparated : lvParentString : "+lvParentString);
	    	  lvSeparatedList.add(lvTempString);
	    	  count--;
	      }
	      if(count == 0)
	      {
	    	  lvSeparatedList.add(lvParentString.substring(0,lvParentString.length()));
	      }
	      //ioLogger.debug("IRequestWSUtilities : listFromSeparated : lvSeparatedList "+lvSeparatedList);
	      //ioLogger.debug("IRequestWSUtilities : listFromSeparated : Ends ");
	      return lvSeparatedList;
	 }

	  public static String getStackTrace(Throwable aThrowable) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
		  }

}
