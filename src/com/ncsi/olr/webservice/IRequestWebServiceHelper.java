/**
 * 
 */
package com.ncsi.olr.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.clarify.cbo.Application;
import com.clarify.cbo.BoContext;
import com.clarify.cbo.Case;
import com.clarify.cbo.CboConstants;
import com.clarify.cbo.CboError;
import com.clarify.cbo.Generic;
import com.clarify.cbo.Session;
import com.clarify.igb.common.IgbUtil;
import com.clarify.xvo.AddressVo;
import com.clarify.xvo.CaseVo;
import com.clarify.xvo.ContactLookupByNameIndex;
import com.clarify.xvo.ContactVo;
import com.clarify.xvo.PhoneLogVo;
import com.clarify.xvo.SiteVo;
import com.clarify.xvobase.BulkResults;
import com.clarify.xvobase.ClfyChoiceData;
import com.clarify.xvobase.ClfyObjid;
import com.clarify.xvobase.ContainedQuerySpec;
import com.clarify.xvobase.RelatedQuerySpec;
import com.clarify.xvobase.SimpleQuerySpec;
import com.clarify.xvobase.Xvo;
import com.ncsi.olr.service.OLRCaseCreateSaveSB;
import com.ncsi.olr.webservice.dao.IRequestWSDAO;
import com.ncsi.olr.webservice.servlet.IRequestWSStartup;
import com.ncsi.olr.webservice.utilities.DBUtilities;
import com.ncsi.olr.webservice.utilities.ErrorNotification;
import com.ncsi.olr.webservice.utilities.IRequestWSConstants;
import com.ncsi.olr.webservice.utilities.IRequestWSProperties;
import com.ncsi.olr.webservice.utilities.IRequestWSUtilities;
import com.ncsi.olr.webservice.utilities.Logger;
import com.ncsi.olr.webservice.utilities.OLRProperties;

/**
 * @author Paul Panjikaran
 *
 */
public class IRequestWebServiceHelper {
	
	static Logger ioLogger = Logger.getLogger();
	
	private static Application app = null;
	
	public String createIncident(String aInboundXML)
	{
		ioLogger.debug("IRequestWebServiceHelper createIncident : starts : ");
		
		IRequestVO loIRequestVO = new IRequestVO();
		
		//Processing the inbound request
		loIRequestVO = this.requestProcessor(aInboundXML, IRequestWSConstants.CallerCreateIncident);
		
		if(loIRequestVO != null && !loIRequestVO.isParsingError() && loIRequestVO.isInboundXMLValidated())
		{
			//Contruction of the value object for later use.
			loIRequestVO = this.constructVO(loIRequestVO, IRequestWSConstants.CallerCreateIncident);

			//Checking for Business rule violations
			loIRequestVO = this.businessValidator(loIRequestVO, IRequestWSConstants.CallerCreateIncident);
		}
        
		ioLogger.debug("IRequestWebServiceHelper createIncident : errorVO : "+loIRequestVO.getError());
		
        if((loIRequestVO.getError() == null || loIRequestVO.getError().isEmpty()) && loIRequestVO.isInboundXMLValidated())
        {
        	Session clfySession = null;
        	try
        	{
                if ( app == null) {
                    app = new Application();
                    app.initialize();
                }
            	clfySession = app.createSession();
            	//new OLRProperties();
            	String lvCallerUserId = loIRequestVO.getUserId();
            	if(lvCallerUserId != null && lvCallerUserId.equalsIgnoreCase(IRequestWSConstants.DC_OPS_ROBOT))
            	{
            		clfySession.login(IRequestWSProperties.getProperty("DC_OPS_USER_LOGIN"), IRequestWSProperties.getProperty("DC_OPS_USER_PASSWORD"));
            	}
            	else
            	{
            		clfySession.login(OLRProperties.getProperty("LOGIN_USERNAME"), OLRProperties.getProperty("LOGIN_PASSWORD"));
            	}
        		BoContext loBoContext = clfySession.createBoContext();

        		ContactLookupByNameIndex nameIndex = new ContactLookupByNameIndex();
        		ContactVo contactVo = new ContactVo();
                SiteVo siteVo = new SiteVo();
                AddressVo addressVo = new AddressVo();;
                CaseVo caseVo = new CaseVo();
                PhoneLogVo phoneLogVo = new PhoneLogVo();
        		nameIndex.setFirstName(loIRequestVO.getClarifyBOVO().getContact_First_Name().trim());
        		nameIndex.setLastName(loIRequestVO.getClarifyBOVO().getContact_Last_Name().trim());
        		nameIndex.setPhone(loIRequestVO.getClarifyBOVO().getContact_Phone().trim());
        		contactVo.setLookupByNameIndex(nameIndex);
                caseVo = new CaseVo();
                ClfyChoiceData choiceCaseType = new ClfyChoiceData(IRequestWSConstants.Case_Type, "Case Type");
                ClfyChoiceData choicePriority = new ClfyChoiceData(loIRequestVO.getClarifyBOVO().getPriority(), "Response Priority Code");
                ClfyChoiceData choiceSeverity = new ClfyChoiceData(loIRequestVO.getClarifyBOVO().getSeverity().toString(), "Problem Severity Level");
                ioLogger.debug("IRequestWebServiceHelper createIncident : choiceCaseType : "+choiceCaseType);
                ioLogger.debug("IRequestWebServiceHelper createIncident : choicePriority : "+choicePriority);
                ioLogger.debug("IRequestWebServiceHelper createIncident : choiceSeverity : "+choiceSeverity);
                // These may be read by properties later.

                String status = IRequestWSConstants.Case_Status;
                ClfyChoiceData choiceStatus = new ClfyChoiceData(status, "Open");
                ioLogger.debug("IRequestWebServiceHelper createIncident : choiceStatus : "+choiceStatus);
                caseVo.setNewRow(true);
                caseVo.setTitle(loIRequestVO.getClarifyBOVO().getIncident_Title());
                caseVo.setCaseType(IRequestWSConstants.Case_Type);
                caseVo.setCaseTypeChoiceData(choiceCaseType);

                caseVo.setPriority(loIRequestVO.getClarifyBOVO().getPriority());
                caseVo.setPriorityChoiceData(choicePriority);

                caseVo.setSeverity(loIRequestVO.getClarifyBOVO().getSeverity().toString());
                caseVo.setSeverityChoiceData(choiceSeverity);

                caseVo.setStatus(status);
                caseVo.setStatusChoiceData(choiceStatus);
        		loBoContext.resolveLookups(new Xvo [] {contactVo} );
                SimpleQuerySpec contactQuerySpec = new SimpleQuerySpec("com.clarify.cbo.Contact");
                contactQuerySpec.setDataFields("*");
                contactQuerySpec.setFilter("objid=" + contactVo.getClfyObjid().getObjid());
                ContainedQuerySpec siteQuery = new ContainedQuerySpec("getSite");
                siteQuery.setDataFields("*");
                contactQuerySpec.addChildQuerySpec("Site", siteQuery);
                RelatedQuerySpec siteAddressQuery = new RelatedQuerySpec("cust_primaddr2address");
                // Set up the query to return all fields from the site table in the results.
                siteAddressQuery.setDataFields("*");


                siteQuery.addChildQuerySpec("Address", siteAddressQuery);

                // Perform the query and get the results.
                BulkResults[] myResults = loBoContext.query(new SimpleQuerySpec[] {contactQuerySpec}, 0);

                BulkResults contactResults = myResults[0];

                if ( contactResults != null ) {
                    Xvo [] contactVoArry = contactResults.getXvo();

                    // get ContactVo from BulkResultset first
                    if ( contactVoArry.length > 0 ) {
                        contactVo = (ContactVo) contactVoArry [0];

                        BulkResults siteResults = contactResults.getChildResultSet(0, "Site");

                        if ( siteResults != null ) {
                            Xvo [] siteVoArry = siteResults.getXvo();
                            // get SiteVo from ContactResultSet

                            if ( siteVoArry.length > 0 ) {
                                siteVo = (SiteVo) siteVoArry [0];
                                ioLogger.debug("IRequestWebServiceHelper createIncident : siteVo : "+siteVo);

                                BulkResults addressResults = siteResults.getChildResultSet(0, "Address");

                                if ( addressResults != null ) {
                                    Xvo [] addressVoArry = addressResults.getXvo();

                                    // get AddressVo from SiteResultSet
                                    if ( addressVoArry.length > 0 ) {
                                        addressVo = (AddressVo) addressVoArry [0];
                                        ioLogger.debug("IRequestWebServiceHelper createIncident : addressVo : "+addressVo);

                                    }
                                }
                            }
                        }
                    }
                } 
                OLRCaseCreateSaveSB saveBean = (OLRCaseCreateSaveSB) loBoContext.createBean("com.ncsi.olr.service.OLRCaseCreateSaveSB");
                saveBean.setCaseRelation(""); 
                saveBean.setSuperCaseFlag("0");
                saveBean.setCaseCdo(IgbUtil.createCdoFromXvo(loBoContext,caseVo));
                saveBean.setSiteObjid(siteVo.getClfyObjid().getObjid());
                saveBean.setContactCdo(IgbUtil.createCdoFromXvo(loBoContext,contactVo));
                saveBean.setAddressObjid(addressVo.getClfyObjid().getObjid());
                phoneLogVo = new PhoneLogVo();
                phoneLogVo.setNewRow(true);
                phoneLogVo.setNotes(loIRequestVO.getClarifyBOVO().getInitial_Log());
                saveBean.setPhoneLogCdo(IgbUtil.createCdoFromXvo(loBoContext,phoneLogVo));
                saveBean.setProduct(loIRequestVO.getClarifyBOVO().getProduct());
                saveBean.setProblem(loIRequestVO.getClarifyBOVO().getProblem_Type());
                ioLogger.warning("IRequestWebServiceHelper createIncident : Product : "+loIRequestVO.getClarifyBOVO().getProduct());
                ioLogger.warning("IRequestWebServiceHelper createIncident : Problem Type : "+loIRequestVO.getClarifyBOVO().getProblem_Type());
                saveBean.setSource(loIRequestVO.getSource());
                saveBean.setSeverity(loIRequestVO.getClarifyBOVO().getSeverity().toString());
                saveBean.setPriority(loIRequestVO.getClarifyBOVO().getPriority());
                saveBean.init(saveBean.getBeanFactory());
                saveBean.exec();
                ClfyObjid clfyObjid = new ClfyObjid("case", saveBean.getCaseObjid());
                loIRequestVO.getClarifyBOVO().setIncidentObjId(clfyObjid.getObjid());
                
                Case caseBo = (Case)loBoContext.createBO("com.clarify.cbo.Case");
                String cDate = loBoContext.getSession().toDateString(loBoContext.getSession().getCurrentDate());
                caseBo.setDataFields("*");
                caseBo.setFilter("objid = " + clfyObjid.getObjid());
                caseBo.query();
                String caseId = (String) caseBo.getFields().getItem("id_number").getValue();
                caseBo.getFields().getItem("x_logged_time").setValue(cDate);
                loIRequestVO.getClarifyBOVO().setIncidentId(new Integer(caseId));
                //IRequestWSDAO loIRequestWSDAO = new IRequestWSDAO();
                //loIRequestVO = loIRequestWSDAO.getSkillGroup(loIRequestVO);
                //loIRequestVO = loIRequestWSDAO.searchQueue(loIRequestVO);
                caseBo.getFields().getItem("x_current_queue").setValue(loIRequestVO.getClarifyBOVO().getQueueName().trim());
                caseBo.relateById(loIRequestVO.getClarifyBOVO().getProduct_Partner_Number(), "case_prt2part_info");
                
                if(loIRequestVO.getClarifyBOVO().getAutoDestId() != null)
                {
                	caseBo.relateById(loIRequestVO.getClarifyBOVO().getAutoDestId().toString(), "case2autodest");
                }
                if ( loIRequestVO.getClarifyBOVO().getQueueName() != null && OLRProperties.getProperty("IS_DISPATCH") != null && OLRProperties.getProperty("IS_DISPATCH").trim().equals("Y") ) {
                	
                	String lvSkillGroup = loIRequestVO.getClarifyBOVO().getQueueName();
                    Generic queueBo = loBoContext.createGenericBO("queue");
                    queueBo.setDataFields("*");
                    queueBo.setFilter("s_title = '" +  DBUtilities.replace (lvSkillGroup.toUpperCase(), "'", "''") + "'");
                    queueBo.query();

                    if ( queueBo.getCount() > 0 ) {
                        caseBo.dispatch(queueBo);
                        ioLogger.warning("IRequestWebServiceHelper createIncident : Incident "+caseId+" despatched to Queue : "+lvSkillGroup);
                    } 
                }
                caseBo.update();
                ioLogger.debug("IRequestWebServiceHelper createIncident : clfyObjid : "+loIRequestVO.getClarifyBOVO().getIncidentId());
                if(loIRequestVO.getClarifyBOVO().getExternal_Reference() != null && !loIRequestVO.getClarifyBOVO().getExternal_Reference().isEmpty())
                {
                	String lvUserid = loIRequestVO.getUserId();
                	String lvExtRefPropQueryString =  lvUserid + "_EXT_REF";
                	ioLogger.debug("IRequestWebServiceHelper createIncident : lvExtRefPropQueryString : "+lvExtRefPropQueryString);
                	//new IRequestWSProperties();
                	String lvExtRefQueryString = IRequestWSProperties.getProperty(lvExtRefPropQueryString);
                	ioLogger.debug("IRequestWebServiceHelper createIncident : lvExtRefQueryString : "+lvExtRefQueryString);

                	HashMap lvExtReferenceMap = loIRequestVO.getClarifyBOVO().getExternal_Reference();
                	ArrayList lvExtRefList = new ArrayList();

                	if(lvUserid.equals(IRequestWSConstants.DC_OPS_ROBOT))
                	{
                		lvExtRefList.add(IRequestWSConstants.DC_OPS_ROBOT_EXT_REF1);
                		lvExtRefList.add(IRequestWSConstants.DC_OPS_ROBOT_EXT_REF2);
                	}

                	Iterator lvIterator = lvExtRefList.iterator();

                	while(lvIterator.hasNext())
                	{
                		String lvExtReference = (String)lvIterator.next();
                		ioLogger.debug("IRequestWebServiceHelper createIncident : lvExtReference : "+lvExtReference);
                		String lvExtReferenceValue = (String)lvExtReferenceMap.get(lvExtReference);

                		ioLogger.debug("IRequestWebServiceHelper createIncident : lvExtReferenceValue : "+lvExtReferenceValue);
                		
                		if(lvExtReferenceValue != null && !lvExtReferenceValue.trim().equals(""))
                		{
                			Generic m_boFlexDef =  loBoContext.createGenericBO("flex_defn");
                			m_boFlexDef.setDataFields("*");
                			m_boFlexDef.setFilter("attribute_name like '"+lvExtRefQueryString+"%' and label like '"+lvExtReference+"'");
                			m_boFlexDef.query();

                			String attrID = m_boFlexDef.getFields().getItem("objid").getValue().toString();
                			String attrName = m_boFlexDef.getFields().getItem("attribute_name").getValue().toString();
                			String attrType = m_boFlexDef.getFields().getItem("attribute_datatype").getValue().toString();

                			ioLogger.debug("IRequestWebServiceHelper createIncident : attrID : "+attrID);
                			ioLogger.debug("IRequestWebServiceHelper createIncident : attrName : "+attrName);
                			ioLogger.debug("IRequestWebServiceHelper createIncident : attrType : "+attrType);

                			Generic m_boFlexAtt = (com.clarify.cbo.Generic) loBoContext.createGenericBO("fa_case");
                			m_boFlexAtt.addNew();
                			m_boFlexAtt.getFields().getItem("attribute_name").setValue(attrName);
                			m_boFlexAtt.getFields().getItem("attribute_value").setValue(lvExtReferenceValue.trim());
                			m_boFlexAtt.getFields().getItem("attribute_datatype").setValue(attrType);
                			m_boFlexAtt.relateById(attrID, "fa_case2flex_defn");
                			m_boFlexAtt.relateById(loIRequestVO.getClarifyBOVO().getIncidentObjId(), "fa_case2case");
                			m_boFlexAtt.update();
                		}
                	}
                }
        	}
        	catch(CboError e)
        	{
        		ioLogger.error("IRequestWebServiceHelper createIncident : CBO CboError Exception Block : ");
        		e.printStackTrace();
        		ArrayList loErrorList = new ArrayList();
        		loErrorList.add(new Integer(1400));
        		ErrorNotification.sendErrorNotification("1400", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
        	}
        	catch(Exception e)
        	{
        		ioLogger.error("IRequestWebServiceHelper createIncident : CBO Generic Exception Block : ");
        		e.printStackTrace();
        		ArrayList loErrorList = new ArrayList();
        		loErrorList.add(new Integer(1400));
        		ErrorNotification.sendErrorNotification("1400", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
        	}
        	finally
        	{
        		if(clfySession != null)
        		{
        			ioLogger.debug("IRequestWebServiceHelper createIncident : clfySession logout block : ");
        			clfySession.logout();
        			clfySession = null;
        		}
        		if(app != null)
        		{
        			ioLogger.debug("IRequestWebServiceHelper createIncident : app logout block : ");
        			app.release();
        			app = null;
        		}
        	}
        	
        }
		
		String lvOutboundXML = this.responseProcessor(loIRequestVO, IRequestWSConstants.CallerCreateIncident);
		
		loIRequestVO = null;
		
		ioLogger.debug("IRequestWebServiceHelper createIncident : ends : ");
		
		return  lvOutboundXML;
	}
	
	public String logNotes(String aInboundXML)
	{
		ioLogger.debug("IRequestWebServiceHelper logNotes : ends : ");

		IRequestVO loIRequestVO = new IRequestVO();

		//Processing the inbound request
		loIRequestVO = this.requestProcessor(aInboundXML, IRequestWSConstants.CallerLogNotes);
		
		if(loIRequestVO != null && !loIRequestVO.isParsingError() && loIRequestVO.isInboundXMLValidated())
		{
			//Contruction of the value object for later use.
			loIRequestVO = this.constructVO(loIRequestVO, IRequestWSConstants.CallerLogNotes);
			
			//Checking for Business rule violations
			loIRequestVO = this.businessValidator(loIRequestVO, IRequestWSConstants.CallerLogNotes);
		}

		ioLogger.debug("IRequestWebServiceHelper logNotes : errorVO : "+loIRequestVO.getError());

		if((loIRequestVO.getError() == null || loIRequestVO.getError().isEmpty()) && loIRequestVO.isInboundXMLValidated())
		{
			ArrayList loErrorList = new ArrayList();
			Session clfySession = null;
			try
			{
				if ( app == null) {
					app = new Application();
					app.initialize();
				}
				clfySession = app.createSession();
				//new OLRProperties();
            	String lvCallerUserId = loIRequestVO.getUserId();
            	if(lvCallerUserId != null && lvCallerUserId.equalsIgnoreCase(IRequestWSConstants.DC_OPS_ROBOT))
            	{
            		clfySession.login(IRequestWSProperties.getProperty("DC_OPS_USER_LOGIN"), IRequestWSProperties.getProperty("DC_OPS_USER_PASSWORD"));
            	}
            	else
            	{
            		clfySession.login(OLRProperties.getProperty("LOGIN_USERNAME"), OLRProperties.getProperty("LOGIN_PASSWORD"));
            	}
				BoContext loBoContext = clfySession.createBoContext();
				Case caseBo = (Case)loBoContext.createBO("com.clarify.cbo.Case");

				caseBo.setDataFields("*");
				caseBo.setFilter("id_number = " + loIRequestVO.getClarifyBOVO().getIncidentId());
				caseBo.query();

				ioLogger.debug("IRequestWebServiceHelper logNotes : count : "+caseBo.getCount());
				ioLogger.debug("IRequestWebServiceHelper logNotes : status : "+caseBo.getStateString());

				if (caseBo.getCount() == 1) 
				{
					if(caseBo.getStateString().equalsIgnoreCase("Closed"))
					{
						
						loErrorList.add(new Integer(1306));
						loIRequestVO.setError(loErrorList);
					}
					else
					{
						caseBo.getNotesLog().setQueryMode(CboConstants.cboSubmitEnabled);
						caseBo.getNotesLog().addNew();
						ioLogger.debug("IRequestWebServiceHelper logNotes : notes : "+loIRequestVO.getClarifyBOVO().getNotes());
						caseBo.getNotesLog().getFields().getItem("description").setValue(loIRequestVO.getClarifyBOVO().getNotes());
						caseBo.update();
					}
				}
				else
				{
					loErrorList.add(new Integer(1305));
					loIRequestVO.setError(loErrorList);
				}
			}
			catch(Exception e)
			{
				ioLogger.error("IRequestWebServiceHelper logNotes : CBO Generic Exception Block : ");
				e.printStackTrace();
				loErrorList.add(new Integer(1400));
				loIRequestVO.setError(loErrorList);
				ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
			}
			finally
			{
				clfySession.logout();
			}

		}

		String lvOutboundXML = this.responseProcessor(loIRequestVO, IRequestWSConstants.CallerLogNotes);

		loIRequestVO = null;
		ioLogger.debug("IRequestWebServiceHelper logNotes : ends : ");

		return  lvOutboundXML;
	}
	
	public String selectProblemType(String aInboundXML)
	{
		ioLogger.debug("IRequestWebServiceHelper selectProblemType : Starts : ");
		
		IRequestVO loIRequestVO = new IRequestVO();
		
		//Processing the inbound request
		loIRequestVO = this.requestProcessor(aInboundXML, IRequestWSConstants.CallerSelectProblemType);
		
		if(loIRequestVO != null && !loIRequestVO.isParsingError() && loIRequestVO.isInboundXMLValidated())
		{

			//Contruction of the value object for later use.
			loIRequestVO = this.constructVO(loIRequestVO, IRequestWSConstants.CallerSelectProblemType);

			//Checking for Business rule violations
			loIRequestVO = this.businessValidator(loIRequestVO, IRequestWSConstants.CallerSelectProblemType);
		}
        
		ioLogger.debug("IRequestWebServiceHelper selectProblemType : errorVO : "+loIRequestVO.getError());
		
		if(!(loIRequestVO != null && loIRequestVO.getError() != null && !loIRequestVO.getError().isEmpty()))
		{
			ArrayList loErrorList = new ArrayList();
			try {
				IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
				loIRequestVO = lvIRequestWSDAO.searchProblemType(loIRequestVO);
			} catch (Exception e) {
				ioLogger.error("IRequestWebServiceHelper selectProblemType : Generic Exception Block : ");
				e.printStackTrace();
				loErrorList.add(new Integer(1402));
				loIRequestVO.setError(loErrorList);
				ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
			}
		}
		
		String lvOutboundXML = this.responseProcessor(loIRequestVO, IRequestWSConstants.CallerSelectProblemType);
		
		ioLogger.debug("IRequestWebServiceHelper selectProblemType : Ends : ");
		
		return  lvOutboundXML;
	}
	
	public String selectProduct(String aInboundXML)
	{
		ioLogger.debug("IRequestWebServiceHelper selectProduct : Starts : ");
		
		IRequestVO loIRequestVO = new IRequestVO();
		
		//Processing the inbound request
		loIRequestVO = this.requestProcessor(aInboundXML, IRequestWSConstants.CallerSelectProduct);
		
		if(loIRequestVO != null && !loIRequestVO.isParsingError() && loIRequestVO.isInboundXMLValidated())
		{

			//Contruction of the value object for later use.
			loIRequestVO = this.constructVO(loIRequestVO, IRequestWSConstants.CallerSelectProduct);

			//Checking for Business rule violations
			loIRequestVO = this.businessValidator(loIRequestVO, IRequestWSConstants.CallerSelectProduct);
		}
        
		ioLogger.debug("IRequestWebServiceHelper selectProduct : errorVO : "+loIRequestVO.getError());
		
		if(!(loIRequestVO != null && loIRequestVO.getError() != null && !loIRequestVO.getError().isEmpty()))
		{
			ArrayList loErrorList = new ArrayList();
			try {
				IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
				loIRequestVO = lvIRequestWSDAO.searchProduct(loIRequestVO);
			} catch (Exception e) {
				ioLogger.error("IRequestWebServiceHelper selectProduct : CBO Generic Exception Block : ");
				e.printStackTrace();
				loErrorList.add(new Integer(1400));
				loIRequestVO.setError(loErrorList);
			}
		}
		
		String lvOutboundXML = this.responseProcessor(loIRequestVO, IRequestWSConstants.CallerSelectProduct);
		
		ioLogger.debug("IRequestWebServiceHelper selectProduct : Ends : ");
		
		return  lvOutboundXML;
	}

	private IRequestVO requestProcessor(String aInboundXML, String lvCallerMethod)
	{
		ioLogger.debug("IRequestWebServiceHelper requestProcessor : starts : ");
		IRequestVO lvIRequestVO = new IRequestVO();
		
		lvIRequestVO.setInboundXML(aInboundXML);		
		String lvTimestamp =IRequestWSUtilities.getTimeStamp();
		lvIRequestVO.setTimestamp(lvTimestamp);
		ArrayList errorVO = new ArrayList();
		
		Document lvIRequestXMLDocument = null;
		
		boolean validate = false;
		try {
			IRequestWSDAO loIRequestWSDAO = new IRequestWSDAO();
			loIRequestWSDAO.insertMasterRecord(lvIRequestVO, lvCallerMethod);
			ioLogger.debug("IRequestWebServiceHelper requestProcessor : objid : "+lvIRequestVO.getIRequestWSMasterObjId());

			//Counter to check number of accesses incremented.
			if(lvCallerMethod.equalsIgnoreCase(IRequestWSConstants.CallerSelectProblemType) || lvCallerMethod.equalsIgnoreCase(IRequestWSConstants.CallerSelectProduct))
			{
				IRequestWSStartup.requestCounterSelect++;
				ArrayList lvTempList = loIRequestWSDAO.getCommonMaster(lvIRequestVO, IRequestWSConstants.CommonMaster_Global_Access_Allowed_Select, null);
				String globalAccessAllowed = lvTempList.get(0).toString();
				if(globalAccessAllowed != null && globalAccessAllowed.equalsIgnoreCase("true"))//access check to prevent too many requests and overloading the system due to an error condition
				{
					lvIRequestXMLDocument = IRequestWSUtilities.convertStringToDoc(aInboundXML);
					lvIRequestVO.setInboundXMLDoc(lvIRequestXMLDocument);
				}
				else
				{
					ioLogger.error("IRequestWebServiceHelper requestProcessor : Exceeded number of allowed requests - Select");
					errorVO.add(new Integer(1403));
					lvIRequestVO.setError(errorVO);
					lvIRequestVO.setParsingError(true);
					ErrorNotification.sendErrorNotification("1402", "IRequestWebServiceHelper requestProcessor : Exceeded number of allowed requests Select", null);
					return lvIRequestVO;
				}
			}
			else if(lvCallerMethod.equalsIgnoreCase(IRequestWSConstants.CallerCreateIncident) || lvCallerMethod.equalsIgnoreCase(IRequestWSConstants.CallerLogNotes))
			{
				IRequestWSStartup.requestCounterUpdate++;
				ArrayList lvTempList = loIRequestWSDAO.getCommonMaster(lvIRequestVO, IRequestWSConstants.CommonMaster_Global_Access_Allowed_Update, null);
				String globalAccessAllowed = lvTempList.get(0).toString();
				if(globalAccessAllowed != null && globalAccessAllowed.equalsIgnoreCase("true"))//access check to prevent too many requests and overloading the system due to an error condition
				{
					lvIRequestXMLDocument = IRequestWSUtilities.convertStringToDoc(aInboundXML);
					lvIRequestVO.setInboundXMLDoc(lvIRequestXMLDocument);
				}
				else
				{
					ioLogger.error("IRequestWebServiceHelper requestProcessor : Exceeded number of allowed requests - Update");
					errorVO.add(new Integer(1403));
					lvIRequestVO.setError(errorVO);
					lvIRequestVO.setParsingError(true);
					ErrorNotification.sendErrorNotification("1402", "IRequestWebServiceHelper requestProcessor : Exceeded number of allowed requests Update", null);
					return lvIRequestVO;
				}
			}
		} catch (ParserConfigurationException e) {
			ioLogger.error("IRequestWebServiceHelper : requestProcessor : ParserConfigurationException : XML ReadException : ");
			e.printStackTrace();
			errorVO.add(new Integer(1001));
			lvIRequestVO.setError(errorVO);
			lvIRequestVO.setParsingError(true);
			return lvIRequestVO;
			
		} catch (SAXException e) {
			ioLogger.error("IRequestWebServiceHelper : requestProcessor : SAXException : XML ReadException : ");
			e.printStackTrace();
			errorVO.add(new Integer(1002));
			lvIRequestVO.setError(errorVO);
			lvIRequestVO.setParsingError(true);
			return lvIRequestVO;
		}
		catch (Exception e)
		{
			ioLogger.error("IRequestWebServiceHelper : requestProcessor : Generic Exception Block : XML ReadException : ");
			e.printStackTrace();
			errorVO.add(new Integer(1002));
			lvIRequestVO.setError(errorVO);
		}
		
		//XML validation
		if(lvIRequestXMLDocument != null)
		{
			ioLogger.debug("IRequestWebServiceHelper : requestProcessor : aInboundXML : "+aInboundXML);
			try {
				validate = IRequestWSUtilities.validateXMLWithXSD(aInboundXML, lvIRequestVO);
				lvIRequestVO.setInboundXMLValidated(validate);
				if(!validate)
				{
					return lvIRequestVO;
				}
				lvIRequestVO.setInboundXML(aInboundXML);
			} catch (SAXException e) {
				ioLogger.error("IRequestWebServiceHelper : requestProcessor : SAXException : XML ValidateException : ");
				e.printStackTrace();
				errorVO.add(new Integer(1003));
				lvIRequestVO.setError(errorVO);
				lvIRequestVO.setParsingError(true);
				return lvIRequestVO;
			} catch (IOException e) {
				ioLogger.error("IRequestWebServiceHelper : requestProcessor : IOException : XML ValidateException : ");
				e.printStackTrace();
				errorVO.add(new Integer(1004));
				lvIRequestVO.setError(errorVO);
				lvIRequestVO.setParsingError(true);
				return lvIRequestVO;
			}
			catch (Exception e)
			{
				ioLogger.error("IRequestWebServiceHelper : requestProcessor : Generic Exception Block : XML ValidateException : ");
				e.printStackTrace();
				errorVO.add(new Integer(1004));
				lvIRequestVO.setError(errorVO);
			}
		}
		
		ioLogger.debug("IRequestWebServiceHelper requestProcessor : ends : ");
		return lvIRequestVO;
	}
	
	private String responseProcessor(IRequestVO aIRequestVO, String lvCallerMethod)
	{
		ioLogger.debug("IRequestWebServiceHelper responseProcessor : starts : ");
		String lvIRequestResponseXML = "";
		
		Document lvIRequestResponseXMLDoc = IRequestWSUtilities.constructResponseXML(aIRequestVO, lvCallerMethod);
		
		try {
			lvIRequestResponseXML = IRequestWSUtilities.convertDocToString(lvIRequestResponseXMLDoc);
			aIRequestVO.setOutboundXML(lvIRequestResponseXML);
			IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
			lvIRequestWSDAO.updateMasterRecord(aIRequestVO);
		} catch (SAXException e) {
			e.printStackTrace();
			ioLogger.error("IRequestWebServiceHelper responseProcessor : SAXException : ");
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), aIRequestVO.getIRequestWSMasterObjId());
		}
		
		ioLogger.debug("IRequestWebServiceHelper responseProcessor : ends : ");
		return lvIRequestResponseXML;
	}
	
	private IRequestVO constructVO(IRequestVO loIRequestVO, String aCallingMethod) {
		
		ioLogger.debug("IRequestWebServiceHelper contructVO : starts : ");
		
		Document doc = loIRequestVO.getInboundXMLDoc();
		ClarifyBOVO loClarifyBOVO = new ClarifyBOVO();

		try
		{
			if(aCallingMethod.equalsIgnoreCase(IRequestWSConstants.CallerCreateIncident))
			{
				HashMap lvExternalReference = new HashMap();

				NodeList nodes_a 
				= doc.getDocumentElement().getChildNodes();

				for (int a = 0; a < nodes_a.getLength(); a++) {
					Node node_a = nodes_a.item(a);
					if (node_a.getNodeType() == Node.ELEMENT_NODE
							&& ((Element) node_a).getTagName()
							.equals("xs:Header1")) {
						Element Header1 = (Element) node_a;
						NodeList nodes_aa = Header1.getChildNodes();
						for (int aa = 0; aa < nodes_aa.getLength(); aa++) {
							Node node_aa = nodes_aa.item(aa);
							if (node_aa.getNodeType() == Node.ELEMENT_NODE) {
								Element names = (Element) node_aa;
								if(names.getNodeName().equalsIgnoreCase("xs:UserId"))
								{
									loIRequestVO.setUserId(node_aa.getChildNodes().item(0).getNodeValue().toString());
								}

							}
						}
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE
							&& ((Element) node_a).getTagName()
							.equals("xs:Header2")) {
						Element Header2 = (Element) node_a;
						NodeList nodes_ab = Header2.getChildNodes();
						for (int ab = 0; ab < nodes_ab.getLength(); ab++) {
							Node node_ab = nodes_ab.item(ab);
							if (node_ab.getNodeType() == Node.ELEMENT_NODE) {
								Element names = (Element) node_ab;
								if(names.getNodeName().equalsIgnoreCase("xs:ContactFirstName"))
								{
									loClarifyBOVO.setContact_First_Name((node_ab.getChildNodes().item(0).getNodeValue().toString()));
								}
								else if(names.getNodeName().equalsIgnoreCase("xs:ContactLastName"))
								{
									loClarifyBOVO.setContact_Last_Name((node_ab.getChildNodes().item(0).getNodeValue().toString()));
								}
								else if(names.getNodeName().equalsIgnoreCase("xs:Source"))
								{
									loIRequestVO.setSource((node_ab.getChildNodes().item(0).getNodeValue().toString()));
								}
							}
						}
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:TransactionId")) 
					{
						loIRequestVO.setTransactionId(new Integer(node_a.getChildNodes().item(0).getNodeValue().toString()));
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:IncidentTitle")) 
					{
						loClarifyBOVO.setIncident_Title(node_a.getChildNodes().item(0).getNodeValue().toString());
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:Severity")) 
					{
						loClarifyBOVO.setSeverity(node_a.getChildNodes().item(0).getNodeValue().toString());
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:Priority")) 
					{
						loClarifyBOVO.setPriority(node_a.getChildNodes().item(0).getNodeValue().toString());
					}	        	
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:Product")) 
					{
						loClarifyBOVO.setProduct(node_a.getChildNodes().item(0).getNodeValue().toString());
					}	   
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:ProblemType")) 
					{
						loClarifyBOVO.setProblem_Type(node_a.getChildNodes().item(0).getNodeValue().toString());
					}	
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:QueueName")) 
					{
						loClarifyBOVO.setQueueName(node_a.getChildNodes().item(0).getNodeValue().toString());
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:InitialLog")) 
					{
						loClarifyBOVO.setInitial_Log(node_a.getChildNodes().item(0).getNodeValue().toString());
					}	  
					else if (node_a.getNodeType() == Node.ELEMENT_NODE
							&& ((Element) node_a).getTagName()
							.equals("xs:ExternalRef")) {
						Element Header1 = (Element) node_a;
						NodeList nodes_aa = Header1.getChildNodes();
						String lvAttributeName = "";
						String lvAttributeValue = "";

						for (int aa = 0; aa < nodes_aa.getLength(); aa++) {
							Node node_aa = nodes_aa.item(aa);
							if (node_aa.getNodeType() == Node.ELEMENT_NODE) {
								Element names = (Element) node_aa;
								if(names.getNodeName().equalsIgnoreCase("xs:AttributeName"))
								{
									lvAttributeName = node_aa.getChildNodes().item(0).getNodeValue().toString();
								}
								else if(names.getNodeName().equalsIgnoreCase("xs:AttributeValue"))
								{
									lvAttributeValue = node_aa.getChildNodes().item(0).getNodeValue().toString();
								}

							}
						}
						lvExternalReference.put(lvAttributeName, lvAttributeValue);
						loClarifyBOVO.setExternal_Reference(lvExternalReference);
					}
				}
				loIRequestVO.setClarifyBOVO(loClarifyBOVO);
				ioLogger.debug("IRequestWebServiceHelper contructVO : UserId : "+loIRequestVO.getUserId());
				ioLogger.debug("IRequestWebServiceHelper contructVO : First Name : "+loIRequestVO.getClarifyBOVO().getContact_First_Name());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Last Name : "+loIRequestVO.getClarifyBOVO().getContact_Last_Name());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Source : "+loIRequestVO.getSource());
				ioLogger.debug("IRequestWebServiceHelper contructVO : TransactionId : "+loIRequestVO.getTransactionId());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Incident Title : "+loIRequestVO.getClarifyBOVO().getIncident_Title());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Severity : "+loIRequestVO.getClarifyBOVO().getSeverity());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Priority : "+loIRequestVO.getClarifyBOVO().getPriority());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Product : "+loIRequestVO.getClarifyBOVO().getProduct());
				ioLogger.debug("IRequestWebServiceHelper contructVO : ProblemType : "+loIRequestVO.getClarifyBOVO().getProblem_Type());
				ioLogger.debug("IRequestWebServiceHelper contructVO : QueueName : "+loIRequestVO.getClarifyBOVO().getQueueName());
				ioLogger.debug("IRequestWebServiceHelper contructVO : InitialLog : "+loIRequestVO.getClarifyBOVO().getInitial_Log());
				ioLogger.debug("IRequestWebServiceHelper contructVO : External Reference : "+loIRequestVO.getClarifyBOVO().getExternal_Reference());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Timestamp : "+loIRequestVO.getTimestamp());
			}
			else if(aCallingMethod.equalsIgnoreCase(IRequestWSConstants.CallerLogNotes))
			{
				NodeList nodes_a 
				= doc.getDocumentElement().getChildNodes();

				for (int a = 0; a < nodes_a.getLength(); a++) {
					Node node_a = nodes_a.item(a);
					if (node_a.getNodeType() == Node.ELEMENT_NODE
							&& ((Element) node_a).getTagName()
							.equals("xs:Header1")) {
						Element Header1 = (Element) node_a;
						NodeList nodes_aa = Header1.getChildNodes();
						for (int aa = 0; aa < nodes_aa.getLength(); aa++) {
							Node node_aa = nodes_aa.item(aa);
							if (node_aa.getNodeType() == Node.ELEMENT_NODE) {
								Element names = (Element) node_aa;
								if(names.getNodeName().equalsIgnoreCase("xs:UserId"))
								{
									loIRequestVO.setUserId(node_aa.getChildNodes().item(0).getNodeValue().toString());
								}

							}
						}
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:IncidentId")) 
					{
						loClarifyBOVO.setIncidentId(new Integer(node_a.getChildNodes().item(0).getNodeValue().toString()));
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:Notes")) 
					{
						loClarifyBOVO.setNotes(node_a.getChildNodes().item(0).getNodeValue().toString());
					}	  
				}
				loIRequestVO.setClarifyBOVO(loClarifyBOVO);
				ioLogger.debug("IRequestWebServiceHelper contructVO : UserId : "+loIRequestVO.getUserId());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Incident Id : "+loIRequestVO.getClarifyBOVO().getIncidentId());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Notes : "+loIRequestVO.getClarifyBOVO().getNotes());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Timestamp : "+loIRequestVO.getTimestamp());
			}
			else if(aCallingMethod.equalsIgnoreCase(IRequestWSConstants.CallerSelectProblemType))
			{
				NodeList nodes_a 
				= doc.getDocumentElement().getChildNodes();

				for (int a = 0; a < nodes_a.getLength(); a++) {
					Node node_a = nodes_a.item(a);
					if (node_a.getNodeType() == Node.ELEMENT_NODE
							&& ((Element) node_a).getTagName()
							.equals("xs:Header1")) {
						Element Header1 = (Element) node_a;
						NodeList nodes_aa = Header1.getChildNodes();
						for (int aa = 0; aa < nodes_aa.getLength(); aa++) {
							Node node_aa = nodes_aa.item(aa);
							if (node_aa.getNodeType() == Node.ELEMENT_NODE) {
								Element names = (Element) node_aa;
								if(names.getNodeName().equalsIgnoreCase("xs:UserId"))
								{
									loIRequestVO.setUserId(node_aa.getChildNodes().item(0).getNodeValue().toString());
								}

							}
						}
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:ProductName")) 
					{
						loClarifyBOVO.setProduct(node_a.getChildNodes().item(0).getNodeValue().toString());
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:ProblemType")) 
					{
						loClarifyBOVO.setProblem_Type(node_a.getChildNodes().item(0).getNodeValue().toString());
					}	  
				}
				loIRequestVO.setClarifyBOVO(loClarifyBOVO);
				ioLogger.debug("IRequestWebServiceHelper contructVO : UserId : "+loIRequestVO.getUserId());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Product : "+loIRequestVO.getClarifyBOVO().getProduct());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Problem Type : "+loIRequestVO.getClarifyBOVO().getProblem_Type());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Timestamp : "+loIRequestVO.getTimestamp());
			}
			else if(aCallingMethod.equalsIgnoreCase(IRequestWSConstants.CallerSelectProduct))
			{
				NodeList nodes_a 
				= doc.getDocumentElement().getChildNodes();

				for (int a = 0; a < nodes_a.getLength(); a++) {
					Node node_a = nodes_a.item(a);
					if (node_a.getNodeType() == Node.ELEMENT_NODE
							&& ((Element) node_a).getTagName()
							.equals("xs:Header1")) {
						Element Header1 = (Element) node_a;
						NodeList nodes_aa = Header1.getChildNodes();
						for (int aa = 0; aa < nodes_aa.getLength(); aa++) {
							Node node_aa = nodes_aa.item(aa);
							if (node_aa.getNodeType() == Node.ELEMENT_NODE) {
								Element names = (Element) node_aa;
								if(names.getNodeName().equalsIgnoreCase("xs:UserId"))
								{
									loIRequestVO.setUserId(node_aa.getChildNodes().item(0).getNodeValue().toString());
								}

							}
						}
					}
					else if (node_a.getNodeType() == Node.ELEMENT_NODE && node_a.getNodeName().equalsIgnoreCase("xs:ProductName")) 
					{
						loClarifyBOVO.setProduct(node_a.getChildNodes().item(0).getNodeValue().toString());
					}
				}
				loIRequestVO.setClarifyBOVO(loClarifyBOVO);
				ioLogger.debug("IRequestWebServiceHelper contructVO : UserId : "+loIRequestVO.getUserId());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Product : "+loIRequestVO.getClarifyBOVO().getProduct());
				ioLogger.debug("IRequestWebServiceHelper contructVO : Timestamp : "+loIRequestVO.getTimestamp());
			}
		}
		catch(Exception e)
		{
			ioLogger.error("IRequestWebServiceHelper contructVO : Generic Exception Block : ");
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());

		}
		
		ioLogger.debug("IRequestWebServiceHelper contructVO : ends : ");
		
		return loIRequestVO;
	}
	
	private IRequestVO businessValidator(IRequestVO loIRequestVO,
			String callercreateincident) {
		
		ioLogger.debug("IRequestWebServiceHelper businessValidator : Starts : ");
		
		try
		{
			//Checking whether the call is made by an authorised user.
			String lvUserId = loIRequestVO.getUserId();
			
			ArrayList lvAuthorizedUserList = new ArrayList();
			if(lvUserId != null && !lvUserId.trim().equals("")){
				IRequestWSDAO lvIRequestWSDAO = new IRequestWSDAO();
				lvAuthorizedUserList = lvIRequestWSDAO.getCommonMaster(loIRequestVO, IRequestWSConstants.CommonMaster_UserId, null);
				
				if(!lvAuthorizedUserList.isEmpty())
				{
					if(!lvAuthorizedUserList.contains(lvUserId))
					{
						ArrayList lvErrorList = loIRequestVO.getError();
						if(lvErrorList == null)
						{
							lvErrorList = new ArrayList();
						}
						lvErrorList.add(new Integer(1310));
						loIRequestVO.setError(lvErrorList);
					}
				}
			}

			ioLogger.debug("IRequestWebServiceHelper businessValidator : lvUserId : "+lvUserId);
			ioLogger.debug("IRequestWebServiceHelper businessValidator : lvAuthorizedUserList : "+lvAuthorizedUserList);
			
			if(callercreateincident.equalsIgnoreCase(IRequestWSConstants.CallerCreateIncident))
			{
				ioLogger.debug("IRequestWebServiceHelper businessValidator : CreateIncident begins : ");
				IRequestWSDAO loIRequestWSDAO = new IRequestWSDAO();
				loIRequestWSDAO.checkProductProblemType(loIRequestVO);
				loIRequestWSDAO.checkContact(loIRequestVO);
				loIRequestWSDAO.searchQueue(loIRequestVO);
				//Checking whether the severity-priority combination is an allowed one.
				ArrayList lvTempList = new ArrayList();
				lvTempList = loIRequestWSDAO.getCommonMaster(loIRequestVO, IRequestWSConstants.CommonMaster_Severity_Priority, loIRequestVO.getClarifyBOVO().getSeverity()+"-"+loIRequestVO.getClarifyBOVO().getPriority());
				if(lvTempList.isEmpty())
				{
					ArrayList lvErrorList = loIRequestVO.getError();
					if(lvErrorList == null)
					{
						lvErrorList = new ArrayList();
					}
					lvErrorList.add(new Integer(1311));
					loIRequestVO.setError(lvErrorList);
				}
			}
			else if(callercreateincident.equalsIgnoreCase(IRequestWSConstants.CallerSelectProblemType))
			{
				ioLogger.debug("IRequestWebServiceHelper businessValidator : Select Problem Type begins : ");
				if (loIRequestVO.getClarifyBOVO() == null
						|| loIRequestVO.getClarifyBOVO().getProduct() == null
						|| loIRequestVO.getClarifyBOVO().getProduct().trim()
								.equals(""))
				{
					ioLogger.debug("IRequestWebServiceHelper businessValidator : Product is empty : ");
					ArrayList lvErrorList = loIRequestVO.getError();
					if(lvErrorList == null)
					{
						lvErrorList = new ArrayList();
					}
					lvErrorList.add(new Integer(1307));
					loIRequestVO.setError(lvErrorList);
				}
			}
			else if(callercreateincident.equalsIgnoreCase(IRequestWSConstants.CallerSelectProduct))
			{
				ioLogger.debug("IRequestWebServiceHelper businessValidator : Select Product begins : ");
				if (loIRequestVO.getClarifyBOVO() == null
						|| loIRequestVO.getClarifyBOVO().getProduct() == null
						|| loIRequestVO.getClarifyBOVO().getProduct().trim()
								.equals(""))
				{
					ioLogger.debug("IRequestWebServiceHelper businessValidator : Product is empty : ");
					ArrayList lvErrorList = loIRequestVO.getError();
					if(lvErrorList == null)
					{
						lvErrorList = new ArrayList();
					}
					lvErrorList.add(new Integer(1307));
					loIRequestVO.setError(lvErrorList);
				}
			}
			
		}
		catch(Exception e)
		{
			ioLogger.error("IRequestWebServiceHelper businessValidator : Generic Exception Block : ");
			e.printStackTrace();
			ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
		}

		ioLogger.debug("IRequestWebServiceHelper businessValidator : ends : ");
		return loIRequestVO;
	}
	  
}
