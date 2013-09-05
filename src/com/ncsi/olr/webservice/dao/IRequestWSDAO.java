package com.ncsi.olr.webservice.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.sql.CLOB;

import com.ncsi.olr.webservice.IRequestVO;
import com.ncsi.olr.webservice.utilities.DBUtilities;
import com.ncsi.olr.webservice.utilities.ErrorNotification;
import com.ncsi.olr.webservice.utilities.IRequestWSConstants;
import com.ncsi.olr.webservice.utilities.IRequestWSProperties;
import com.ncsi.olr.webservice.utilities.IRequestWSUtilities;
import com.ncsi.olr.webservice.utilities.Logger;

public class IRequestWSDAO {
	
	static Logger ioLogger = Logger.getLogger();
	
     public IRequestVO checkProductProblemType(IRequestVO loIRequestVO) {

    	 ioLogger.debug("IRequestWSDAO checkProductProblemType : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvProduct = loIRequestVO.getClarifyBOVO().getProduct();
    	 String lvProblemType = loIRequestVO.getClarifyBOVO().getProblem_Type();

    	 ioLogger.warning("IRequestWSDAO checkProductProblemType : Product : "+lvProduct);
    	 ioLogger.warning("IRequestWSDAO checkProductProblemType : Problem Type : "+lvProblemType);

    	 ArrayList loErrorList = loIRequestVO.getError();
    	 if(loErrorList == null)
    	 {
    		 loErrorList = new ArrayList();
    	 }
    	 if(lvProduct == null || lvProblemType == null || lvProduct.equals("") || lvProblemType.equals(""))
    	 {
    		 loErrorList.add(new Integer(1301));
    		 loIRequestVO.setError(loErrorList);
    		 return loIRequestVO;
    	 }

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 StringBuffer loSelectClause = new StringBuffer(" ");
    	 StringBuffer loFromClause = new StringBuffer(" ");
    	 StringBuffer loWhereClause = new StringBuffer(" ");
    	 StringBuffer loSortByClause = new StringBuffer(" ");

    	 loSelectClause.append("SELECT * ");

    	 loFromClause.append("FROM ").append("TABLE_X_PARTNUM_VIEW");

    	 loWhereClause.append(" WHERE ");
    	 loWhereClause.append("PART_NO = ?");

    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("PROBLEM_TYPE = ? ");
    	 
    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("ACTIVE = 'Active' ");
    	 
    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("MOD_ACTIVE = 'Active' ");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 loCompleteQuery = loSelectClause.append(loFromClause).append(loWhereClause).append(loSortByClause);
    		 ioLogger.debug("IRequestWSDAO checkProductProblemType : loCompleteQuery : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, lvProduct);
    		 lvPreparedStatement.setString(2, lvProblemType);

    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 if(lvResultSet.next())
    		 {
    			 ioLogger.debug("IRequestWSDAO checkProductProblemType : Product - Problem Type matched : ");
    			 loIRequestVO.getClarifyBOVO().setProduct_Partner_Number(lvResultSet.getString("PART_NUM_OBJID"));
    		 }
    		 else
    		 {
    			 ioLogger.error("IRequestWSDAO checkProductProblemType : Product - Problem Type NOT matched : ");
    			 loErrorList.add(new Integer(1302));
    			 loIRequestVO.setError(loErrorList);
    		 }

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO checkProductProblemType : Ends : ");

    	 return loIRequestVO;

     }
     
     public IRequestVO checkContact(IRequestVO loIRequestVO) {

    	 ioLogger.debug("IRequestWSDAO checkContact : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvFirstName = loIRequestVO.getClarifyBOVO().getContact_First_Name();
    	 String lvLastName = loIRequestVO.getClarifyBOVO().getContact_Last_Name();

    	 ioLogger.debug("IRequestWSDAO checkContact : lvFirstName : "+lvFirstName);
    	 ioLogger.debug("IRequestWSDAO checkContact : lvLastName : "+lvLastName);

    	 ArrayList loErrorList = loIRequestVO.getError();
    	 if(loErrorList == null)
    	 {
    		 loErrorList = new ArrayList();
    	 }
    	 if(lvFirstName == null || lvLastName == null || lvFirstName.equals("") || lvLastName.equals(""))
    	 {
    		 loErrorList.add(new Integer(1303));
    		 loIRequestVO.setError(loErrorList);
    		 return loIRequestVO;
    	 }

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 StringBuffer loSelectClause = new StringBuffer(" ");
    	 StringBuffer loFromClause = new StringBuffer(" ");
    	 StringBuffer loWhereClause = new StringBuffer(" ");
    	 StringBuffer loSortByClause = new StringBuffer(" ");

    	 loSelectClause.append("SELECT * ");

    	 loFromClause.append("FROM ").append("TABLE_CONTACT");

    	 loWhereClause.append(" WHERE ");
    	 loWhereClause.append("UPPER(FIRST_NAME) = ?");

    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("UPPER(LAST_NAME) = ? ");
    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("STATUS = 0 ");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 loCompleteQuery = loSelectClause.append(loFromClause).append(loWhereClause).append(loSortByClause);
    		 ioLogger.debug("IRequestWSDAO checkContact : loCompleteQuery : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, lvFirstName.toUpperCase());
    		 lvPreparedStatement.setString(2, lvLastName.toUpperCase());

    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 if(lvResultSet.next())
    		 {
    			 ioLogger.debug("IRequestWSDAO checkContact : First Name Last Name matched : ");
    			 loIRequestVO.getClarifyBOVO().setContact_Phone(lvResultSet.getString("PHONE"));
    		 }
    		 else
    		 {
    			 ioLogger.warning("IRequestWSDAO checkContact : First Name Last Name NOT matched : ");
    			 loErrorList.add(new Integer(1304));
    			 loIRequestVO.setError(loErrorList);
    		 }

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO checkContact : Ends : ");

    	 return loIRequestVO;

     }
     
     public IRequestVO getSkillGroup (IRequestVO aIRequestVO) {
    	 
    	 ioLogger.debug("IRequestWSDAO getSkillGroup : Starts : ");

         String sqlStatement = "{call sa.ncs_autodest.sp_get_resolver_grp (?, ?, ?, ?, ?)}";
         CallableStatement cstmt = null;
         String queueName = "";
         Integer autoDestId = null;
         Connection con = null;
         try {
             con = DBUtilities.getConnection();
             cstmt = con.prepareCall(sqlStatement);
             cstmt.setString(1,aIRequestVO.getClarifyBOVO().getProduct().toUpperCase().trim());
             cstmt.setString(2,aIRequestVO.getClarifyBOVO().getProblem_Type().toUpperCase().trim());
             cstmt.setString(3,aIRequestVO.getClarifyBOVO().getIncidentId().toString());
             cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
             cstmt.registerOutParameter(5, java.sql.Types.INTEGER);
             cstmt.executeQuery();

             queueName = cstmt.getString(4);
             autoDestId = new Integer(cstmt.getInt(5));
             aIRequestVO.getClarifyBOVO().setAutoDestId(autoDestId);

         } catch (Exception ex) {
        	 ioLogger.error("*E* OLR Error executing sa.ncs_autodest.sp_get_resolver_grp, or no skill group found.");
        	 ioLogger.error("*E* OLR Error :: Product = " + aIRequestVO.getClarifyBOVO().getProduct() + " :: Problem = " + aIRequestVO.getClarifyBOVO().getProblem_Type() + " :: Case ID = " + aIRequestVO.getClarifyBOVO().getIncidentId() );
        	 ioLogger.error("*E* OLR Error No Skill Group Found.");
             queueName = IRequestWSConstants.Default_Skill_Group;
         } finally {
        	 DBUtilities.closeCallableStatement(cstmt);
        	 DBUtilities.closeConnection(con);
        	 aIRequestVO.getClarifyBOVO().setQueueName(queueName);
         }
         
         ioLogger.debug("IRequestWSDAO getSkillGroup : queueName : "+queueName);
         ioLogger.debug("IRequestWSDAO getSkillGroup : autoDestId : "+autoDestId);
         
         ioLogger.debug("IRequestWSDAO getSkillGroup : Ends : ");
         return aIRequestVO;
     }
     
     public IRequestVO searchQueue(IRequestVO loIRequestVO) {

    	 ioLogger.debug("IRequestWSDAO searchQueue : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvQueueName = loIRequestVO.getClarifyBOVO().getQueueName();

    	 ioLogger.warning("IRequestWSDAO searchQueue : lvQueueName : "+lvQueueName);

    	 ArrayList loErrorList = loIRequestVO.getError();
    	 if(loErrorList == null)
    	 {
    		 loErrorList = new ArrayList();
    	 }

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 StringBuffer loSelectClause = new StringBuffer(" ");
    	 StringBuffer loFromClause = new StringBuffer(" ");
    	 StringBuffer loWhereClause = new StringBuffer(" ");
    	 StringBuffer loSortByClause = new StringBuffer(" ");

    	 loSelectClause.append("SELECT * ");

    	 loFromClause.append("FROM ").append("TABLE_QUEUE");

    	 loWhereClause.append(" WHERE ");
    	 loWhereClause.append("UPPER(TITLE) LIKE ?");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 loCompleteQuery = loSelectClause.append(loFromClause).append(loWhereClause).append(loSortByClause);
    		 ioLogger.debug("IRequestWSDAO searchProduct : loCompleteQuery : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, lvQueueName.toUpperCase());

    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 if(lvResultSet.next())
    		 {
    			 loIRequestVO.getClarifyBOVO().setQueueName(lvResultSet.getString("TITLE"));
    			 loIRequestVO.getClarifyBOVO().setAutoDestId(new Integer(lvResultSet.getInt("OBJID")));
    			 ioLogger.debug("IRequestWSDAO searchQueue : lvQueueName : "+loIRequestVO.getClarifyBOVO().getQueueName());
    		 }
    		 else
    		 {
    			 ioLogger.error("IRequestWSDAO searchQueue : Queue NOT found : ");
    			 loErrorList.add(new Integer(1313));
    			 loIRequestVO.setError(loErrorList);
    		 }

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO searchQueue : Ends : ");

    	 return loIRequestVO;

     }
     
     public IRequestVO insertMasterRecord(IRequestVO loIRequestVO, String aMethodName) {

    	 ioLogger.debug("IRequestWSDAO insertMasterRecord : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvTimeStamp = loIRequestVO.getTimestamp();
    	 String lvInboundXML = loIRequestVO.getInboundXML();
		 if(lvInboundXML.length() > 4000)
		 {
			 lvInboundXML = lvInboundXML.substring(0, 3999);
		 }

    	 ioLogger.error("IRequestWSDAO insertMasterRecord : lvTimeStamp : "+lvTimeStamp);
    	 ioLogger.error("IRequestWSDAO insertMasterRecord : lvInboundXML : "+lvInboundXML);

    	 StringBuffer loInsertQuery = new StringBuffer("");
    	 StringBuffer loSelectQuery = new StringBuffer("");
    	 StringBuffer loUpdateQuery = new StringBuffer("");
    	 StringBuffer loSequenceNumberQuery = new StringBuffer("");
    	 
    	 loSequenceNumberQuery.append("SELECT IREQUESTWSMASTER_SEQ.NEXTVAL FROM DUAL");
    	 loInsertQuery.append("INSERT INTO IREQUESTWSMASTER (OBJID, REQTIMESTAMP, TRANSACTIONID, METHODNAME, INBOUNDXML, OUTBOUNDXML, USERID, CONTACTFIRSTNAME, CONTACTLASTNAME, SOURCE, STATUS, INCIDENTID, STARTTIME, ENDTIME) VALUES (?,?,NULL,?,EMPTY_CLOB(),EMPTY_CLOB(),NULL,NULL,NULL,NULL,NULL,NULL,SYSDATE,SYSDATE)");
    	 loSelectQuery.append("SELECT INBOUNDXML FROM IREQUESTWSMASTER WHERE OBJID = ?");
    	 loUpdateQuery.append("UPDATE IREQUESTWSMASTER SET INBOUNDXML = ? WHERE OBJID = ?");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 lvConnection.setAutoCommit(true);
    		 
    		 ioLogger.debug("IRequestWSDAO insertMasterRecord : loSequenceNumberQuery : "+loSequenceNumberQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loSequenceNumberQuery.toString());
    		 
    		 lvResultSet = lvPreparedStatement.executeQuery();
    		 lvResultSet.next();
    		 int ObjId = lvResultSet.getInt(1);
    		 loIRequestVO.setIRequestWSMasterObjId(new Integer(ObjId).toString());
    		 
    		 lvConnection.setAutoCommit(false);
    		 lvPreparedStatement = null;
    		 lvResultSet = null;
    		 
    		 ioLogger.debug("IRequestWSDAO insertMasterRecord : loInsertQuery : "+loInsertQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loInsertQuery.toString());

    		 lvPreparedStatement.setString(1, new Integer(ObjId).toString());
    		 lvPreparedStatement.setLong(2, new Long(lvTimeStamp).longValue());
    		 lvPreparedStatement.setString(3, aMethodName);

    		 lvResultSet = lvPreparedStatement.executeQuery();
    		 
    		 lvPreparedStatement = null;
    		 lvResultSet = null;
    		 
    		 ioLogger.debug("IRequestWSDAO insertMasterRecord : loSelectQuery : "+loSelectQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loSelectQuery.toString());
    		 
    		 lvPreparedStatement.setInt(1, ObjId);
    		 
    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 if(lvResultSet.next())
    		 {
    			 CLOB loClob= (CLOB)lvResultSet.getClob("INBOUNDXML");
    			 loClob.putString(1, lvInboundXML);
    			 lvPreparedStatement = null;
        		 lvResultSet = null;
        		 
        		 ioLogger.debug("IRequestWSDAO insertMasterRecord : loUpdateQuery : "+loUpdateQuery);
        		 lvPreparedStatement = lvConnection.prepareStatement(loUpdateQuery.toString());
        		 
        		 lvPreparedStatement.setClob(1, loClob);
        		 lvPreparedStatement.setInt(2, ObjId);
        		 lvPreparedStatement.executeUpdate();
        		 lvConnection.commit();
    		 }
    		 

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO insertMasterRecord : Ends : ");

    	 return loIRequestVO;

     }
     
     public IRequestVO updateMasterRecord(IRequestVO loIRequestVO) {

    	 ioLogger.debug("IRequestWSDAO insertMasterRecord : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;
    	 
    	 StringBuffer loUpdateQuery = new StringBuffer("");
    	 
    	 loUpdateQuery.append("UPDATE IREQUESTWSMASTER SET ");

    	 String lvObjId = ""; 
    	 Integer lvTransactionId = null;
    	 String lvOutboundXML = "";
    	 String lvUserid = "";
    	 String lvContactFirstName = "";
    	 String lvContactLastName = "";
    	 String lvSource = "";
    	 String lvStatus = "";
    	 String lvIncidentId = "";
    	 String lvTimeStamp = "";
    	 
    	 if(loIRequestVO != null)
    	 {
    		 if(loIRequestVO.getIRequestWSMasterObjId() != null)
    		 {
    			 lvObjId = loIRequestVO.getIRequestWSMasterObjId();
    		 }
    		 if(loIRequestVO.getTimestamp() != null)
    		 {
    			 lvTimeStamp = loIRequestVO.getTimestamp();
    			 loUpdateQuery.append("REQTIMESTAMP = ? ");
    		 }
    		 if(loIRequestVO.getTransactionId() != null)
    		 {
    			 lvTransactionId = loIRequestVO.getTransactionId();
    			 loUpdateQuery.append(", TRANSACTIONID = ? ");
    		 }
    		 if(loIRequestVO.getOutboundXML() != null)
    		 {
    			 lvOutboundXML = loIRequestVO.getOutboundXML();
    			 if(lvOutboundXML.length() > 4000)
    			 {
    				 lvOutboundXML = lvOutboundXML.substring(0, 3999);
    			 }
    			 loUpdateQuery.append(", OUTBOUNDXML = ? ");
    		 }
    		 if(loIRequestVO.getUserId() != null)
    		 {
    			 lvUserid = loIRequestVO.getUserId();
    			 loUpdateQuery.append(", USERID = ? ");
    		 }
    		 if(loIRequestVO.getSource()!= null)
    		 {
    			 lvSource = loIRequestVO.getSource();
    			 loUpdateQuery.append(", SOURCE = ? ");
    		 }
    		 if(loIRequestVO.getStatus() != null)
    		 {
    			 lvStatus = loIRequestVO.getStatus();
    			 loUpdateQuery.append(", STATUS = ? ");
    		 }
    		 if(loIRequestVO.getClarifyBOVO() != null)
    		 {
    			 if(loIRequestVO.getClarifyBOVO().getContact_First_Name() != null)
    			 {
    				 lvContactFirstName = loIRequestVO.getClarifyBOVO().getContact_First_Name();
    				 loUpdateQuery.append(", CONTACTFIRSTNAME = ? ");
    			 }
    			 if(loIRequestVO.getClarifyBOVO().getContact_Last_Name() != null)
    			 {
    				 lvContactLastName = loIRequestVO.getClarifyBOVO().getContact_Last_Name();
    				 loUpdateQuery.append(", CONTACTLASTNAME = ? ");
    			 }
    			 if(loIRequestVO.getClarifyBOVO().getIncidentId() != null)
    			 {
    				 lvIncidentId = loIRequestVO.getClarifyBOVO().getIncidentId().toString();
    				 loUpdateQuery.append(", INCIDENTID = ? ");
    			 }
    		 }
    	 }
    	 loUpdateQuery.append(", ENDTIME = SYSDATE ");
    	 loUpdateQuery.append(" WHERE OBJID = ? ");

    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvObjId : "+lvObjId);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvTimeStamp : "+lvTimeStamp);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvTransactionId : "+lvTransactionId);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvOutboundXML : "+lvOutboundXML);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvUserid : "+lvUserid);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvSource : "+lvSource);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvStatus : "+lvStatus);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvContactFirstName : "+lvContactFirstName);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvContactLastName : "+lvContactLastName);
    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : lvIncidentId : "+lvIncidentId);



    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 lvConnection.setAutoCommit(false);
    		 
    		 ioLogger.debug("IRequestWSDAO updateMasterRecord : loUpdateQuery : "+loUpdateQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loUpdateQuery.toString());

    		 int i = 1;
    		 if(loIRequestVO.getTimestamp() != null)
    		 {
    			 lvPreparedStatement.setLong(i++, new Long(lvTimeStamp).longValue());
    		 }
    		 if(loIRequestVO.getTransactionId() != null)
    		 {
    			 lvPreparedStatement.setString(i++, lvTransactionId.toString());
    		 }
    		 if(loIRequestVO.getOutboundXML() != null)
    		 {
    			 lvPreparedStatement.setString(i++, lvOutboundXML.toString());
    		 }
    		 if(loIRequestVO.getUserId() != null)
    		 {
    			 lvPreparedStatement.setString(i++, lvUserid);
    		 }
    		 if(loIRequestVO.getSource()!= null)
    		 {
    			 lvPreparedStatement.setString(i++, lvSource);
    		 }
    		 if(loIRequestVO.getStatus() != null)
    		 {
    			 lvPreparedStatement.setString(i++, lvStatus);
    		 }
    		 if(loIRequestVO.getClarifyBOVO() != null)
    		 {
    			 if(loIRequestVO.getClarifyBOVO().getContact_First_Name() != null)
    			 {
    				 lvPreparedStatement.setString(i++, lvContactFirstName);
    			 }
    			 if(loIRequestVO.getClarifyBOVO().getContact_Last_Name() != null)
    			 {
    				 lvPreparedStatement.setString(i++, lvContactLastName);
    			 }
    			 if(loIRequestVO.getClarifyBOVO().getIncidentId() != null)
    			 {
    				 lvPreparedStatement.setString(i++, lvIncidentId);
    			 }
    		 }
    		 lvPreparedStatement.setInt(i++, new Integer(lvObjId).intValue());

    		 lvResultSet = lvPreparedStatement.executeQuery();

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO updateMasterRecord : Ends : ");

    	 return loIRequestVO;

     }
     
     public IRequestVO insertErrorRecord(IRequestVO loIRequestVO, String aErrorCode, String aErrorMessage) {

    	 ioLogger.debug("IRequestWSDAO insertErrorRecord : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvMasterObjid = loIRequestVO.getIRequestWSMasterObjId();

    	 ioLogger.warning("IRequestWSDAO insertErrorRecord : lvMasterObjid : "+lvMasterObjid);
    	 ioLogger.warning("IRequestWSDAO insertErrorRecord : aErrorCode : "+aErrorCode);
    	 ioLogger.warning("IRequestWSDAO insertErrorRecord : aErrorMessage : "+aErrorMessage);

    	 StringBuffer loInsertQuery = new StringBuffer("");
    	 StringBuffer loSequenceNumberQuery = new StringBuffer("");
    	 
    	 loSequenceNumberQuery.append("SELECT IREQUESTWSERROR_SEQ.NEXTVAL FROM DUAL");
    	 loInsertQuery.append("INSERT INTO IREQUESTWSERROR (OBJID, ERRORTOMASTER, ERRORCODE, ERRORDESC, STARTTIME, ENDTIME) VALUES (?,?,?,?,SYSDATE,SYSDATE)");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 lvConnection.setAutoCommit(true);
    		 
    		 ioLogger.debug("IRequestWSDAO insertErrorRecord : loSequenceNumberQuery : "+loSequenceNumberQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loSequenceNumberQuery.toString());
    		 
    		 lvResultSet = lvPreparedStatement.executeQuery();
    		 lvResultSet.next();
    		 int ObjId = lvResultSet.getInt(1);
    		 
    		 lvConnection.setAutoCommit(false);
    		 lvPreparedStatement = null;
    		 lvResultSet = null;
    		 
    		 ioLogger.debug("IRequestWSDAO insertErrorRecord : loInsertQuery : "+loInsertQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loInsertQuery.toString());

    		 lvPreparedStatement.setString(1, new Integer(ObjId).toString());
    		 lvPreparedStatement.setString(2, new Integer(lvMasterObjid).toString());
    		 lvPreparedStatement.setString(3, aErrorCode);
    		 lvPreparedStatement.setString(4, aErrorMessage);

    		 lvResultSet = lvPreparedStatement.executeQuery();

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO insertErrorRecord : Ends : ");

    	 return loIRequestVO;

     }

     public IRequestVO searchProblemType(IRequestVO loIRequestVO) {

    	 ioLogger.debug("IRequestWSDAO searchProblemType : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvProduct = loIRequestVO.getClarifyBOVO().getProduct();
    	 String lvProblemType = loIRequestVO.getClarifyBOVO().getProblem_Type();
    	 String lvProblemTypeSearchResult = "";

    	 ioLogger.warning("IRequestWSDAO searchProblemType : Product : "+lvProduct);
    	 ioLogger.warning("IRequestWSDAO searchProblemType : Problem Type : "+lvProblemType);

    	 ArrayList loErrorList = loIRequestVO.getError();
    	 if(loErrorList == null)
    	 {
    		 loErrorList = new ArrayList();
    	 }
    	 ArrayList lvProblemTypeList = new ArrayList();

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 StringBuffer loSelectClause = new StringBuffer(" ");
    	 StringBuffer loFromClause = new StringBuffer(" ");
    	 StringBuffer loWhereClause = new StringBuffer(" ");
    	 StringBuffer loSortByClause = new StringBuffer(" ");

    	 loSelectClause.append("SELECT DISTINCT PROBLEM_TYPE ");

    	 loFromClause.append("FROM ").append("TABLE_X_PARTNUM_VIEW");

    	 loWhereClause.append(" WHERE ");
    	 loWhereClause.append("UPPER(PART_NO) = ?");

    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("UPPER(PROBLEM_TYPE) LIKE ? ");
   	 
    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("ACTIVE = 'Active' ");
    	 
    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("MOD_ACTIVE = 'Active' ");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 loCompleteQuery = loSelectClause.append(loFromClause).append(loWhereClause).append(loSortByClause);
    		 ioLogger.debug("IRequestWSDAO searchProblemType : loCompleteQuery : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, lvProduct.toUpperCase());
    		 ioLogger.debug("IRequestWSDAO searchProblemType : lvProduct : "+lvProduct);
    		 
    		 if(lvProblemType == null || lvProblemType.trim().equals(""))
    		 {
    			 lvPreparedStatement.setString(2, "%");
    		 }
    		 else
    		 {
    			 lvPreparedStatement.setString(2, lvProblemType.toUpperCase());
    			 ioLogger.debug("IRequestWSDAO searchProblemType : lvProblemType : "+lvProblemType);
    		 }

    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 int i = 0;
    		 while(lvResultSet.next())
    		 {
    			 lvProblemTypeSearchResult = lvResultSet.getString("PROBLEM_TYPE");
    			 ioLogger.debug("IRequestWSDAO searchProblemType : Problem Type Search Result : "+lvProblemTypeSearchResult);
    			 lvProblemTypeList.add(lvProblemTypeSearchResult);
    			 i++;
    		 }
    		 if(lvProblemTypeList.isEmpty())
    		 {
    			 loErrorList.add(new Integer(1308));
    			 loIRequestVO.setError(loErrorList);
    		 }
    		 else if(i > IRequestWSProperties.getPropertyInt("MAX_SEARCH_RESULTS"))
    		 {
    			 loErrorList.add(new Integer(1312));
    			 loIRequestVO.setError(loErrorList);
    		 }
    		 else
    		 {
    			 loIRequestVO.getClarifyBOVO().setProblem_Type_List(lvProblemTypeList);
    		 }

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO searchProblemType : Ends : ");

    	 return loIRequestVO;

     }
     
     public IRequestVO searchProduct(IRequestVO loIRequestVO) {

    	 ioLogger.debug("IRequestWSDAO searchProduct : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 String lvProduct = loIRequestVO.getClarifyBOVO().getProduct();
    	 String lvProductSearchResult = "";

    	 ioLogger.warning("IRequestWSDAO searchProduct : Product : "+lvProduct);

    	 ArrayList loErrorList = loIRequestVO.getError();
    	 if(loErrorList == null)
    	 {
    		 loErrorList = new ArrayList();
    	 }
    	 ArrayList lvProductList = new ArrayList();

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 StringBuffer loSelectClause = new StringBuffer(" ");
    	 StringBuffer loFromClause = new StringBuffer(" ");
    	 StringBuffer loWhereClause = new StringBuffer(" ");
    	 StringBuffer loSortByClause = new StringBuffer(" ");

    	 loSelectClause.append("SELECT DISTINCT PART_NO ");

    	 loFromClause.append("FROM ").append("TABLE_X_PARTNUM_VIEW");

    	 loWhereClause.append(" WHERE ");
    	 loWhereClause.append("UPPER(PART_NO) LIKE ?");

    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("ACTIVE = 'Active' ");
    	 
    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 loCompleteQuery = loSelectClause.append(loFromClause).append(loWhereClause).append(loSortByClause);
    		 ioLogger.debug("IRequestWSDAO searchProduct : loCompleteQuery : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, lvProduct.toUpperCase());
    		 ioLogger.debug("IRequestWSDAO searchProduct : lvProduct : "+lvProduct);

    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 int i = 0;
    		 while(lvResultSet.next())
    		 {
    			 lvProductSearchResult = lvResultSet.getString("PART_NO");
    			 ioLogger.debug("IRequestWSDAO searchProduct : Product Result : "+lvProductSearchResult);
    			 lvProductList.add(lvProductSearchResult);
    			 i++;
    		 }
    		 if(lvProductList.isEmpty())
    		 {
    			 loErrorList.add(new Integer(1309));
    			 loIRequestVO.setError(loErrorList);
    		 }
    		 else if(i > IRequestWSProperties.getPropertyInt("MAX_SEARCH_RESULTS"))
    		 {
    			 loErrorList.add(new Integer(1312));
    			 loIRequestVO.setError(loErrorList);
    		 }
    		 else
    		 {
    			 loIRequestVO.getClarifyBOVO().setProduct_List(lvProductList);
    		 }

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO searchProduct : Ends : ");

    	 return loIRequestVO;

     }
     
     public ArrayList getCommonMaster(IRequestVO loIRequestVO, String lvParamName, String lvParamValue) {

    	 ioLogger.debug("IRequestWSDAO getCommonMaster : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 ioLogger.debug("IRequestWSDAO getCommonMaster : lvParamName : "+lvParamName);

    	 ArrayList loErrorList = loIRequestVO.getError();
    	 if(loErrorList == null)
    	 {
    		 loErrorList = new ArrayList();
    	 }
    	 ArrayList lvParamValues = new ArrayList();

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 StringBuffer loSelectClause = new StringBuffer(" ");
    	 StringBuffer loFromClause = new StringBuffer(" ");
    	 StringBuffer loWhereClause = new StringBuffer(" ");
    	 StringBuffer loSortByClause = new StringBuffer(" ");

    	 loSelectClause.append("SELECT VALUE ");

    	 loFromClause.append("FROM ").append("IREQUESTWSCOMMONMASTER");

    	 loWhereClause.append(" WHERE ");
    	 
    	 if(lvParamName != null)
    	 {
    		 loWhereClause.append("UPPER(NAME) LIKE ? ");
    	 }
    	 
    	 if(lvParamValue != null)
    	 {
    		 loWhereClause.append(" AND UPPER(VALUE) LIKE ?");
    	 }

    	 loWhereClause.append(" AND ");
    	 loWhereClause.append("ACTIVE = 'Y' ");
    	 
    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 loCompleteQuery = loSelectClause.append(loFromClause).append(loWhereClause).append(loSortByClause);
    		 ioLogger.debug("IRequestWSDAO getCommonMaster : loCompleteQuery : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 int i=1;
        	 if(lvParamName != null)
        	 {
        		 lvPreparedStatement.setString(i++, lvParamName.toUpperCase()+"%");
        	 }
        	 if(lvParamValue != null)
        	 {
        		 lvPreparedStatement.setString(i++, lvParamValue.toUpperCase()+"%");
        	 }
    		 ioLogger.debug("IRequestWSDAO getCommonMaster : lvParamName : "+lvParamName);
    		 ioLogger.debug("IRequestWSDAO getCommonMaster : lvParamValue : "+lvParamValue);

    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 while(lvResultSet.next())
    		 {
    			 String lvSearchResult = lvResultSet.getString("VALUE");
    			 ioLogger.debug("IRequestWSDAO getCommonMaster : lvSearchResult : "+lvSearchResult);
    			 lvParamValues.add(lvSearchResult);
    		 }
    		 if(lvParamValues.isEmpty() && lvParamValue == null)
    		 {
    			 loErrorList.add(new Integer(1401));
    			 loIRequestVO.setError(loErrorList);
    		 }
    		 ioLogger.debug("IRequestWSDAO getCommonMaster : lvParamValues : "+lvParamValues);

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), loIRequestVO.getIRequestWSMasterObjId());
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), loIRequestVO.getIRequestWSMasterObjId());
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO searchProduct : Ends : ");

    	 return lvParamValues;

     }
     
     public void updateCommonMaster(String lvParamName, String lvParamValue) {

    	 ioLogger.debug("IRequestWSDAO updateCommonMaster : Starts : ");

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;
    	 
    	 StringBuffer loUpdateQuery = new StringBuffer("");
    	 
    	 ioLogger.debug("IRequestWSDAO updateCommonMaster : lvParamName : "+lvParamName);
    	 ioLogger.debug("IRequestWSDAO updateCommonMaster : lvParamValue : "+lvParamValue);
    	 
    	 loUpdateQuery.append("UPDATE IREQUESTWSCOMMONMASTER SET ");
    	 
    	 loUpdateQuery.append("VALUE = ?, UPDATEDDATE = SYSDATE WHERE NAME = ?");

       	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 lvConnection.setAutoCommit(false);
    		 
    		 ioLogger.debug("IRequestWSDAO updateCommonMaster : loUpdateQuery : "+loUpdateQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loUpdateQuery.toString());

   			 lvPreparedStatement.setString(1, lvParamValue);
   			 lvPreparedStatement.setString(2, lvParamName);

    		 lvResultSet = lvPreparedStatement.executeQuery();

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), null);
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO updateCommonMaster : Ends : ");
     }
     
     public void generateUsageReport(int start,int end, HashMap aMasterMap ) {

    	 ioLogger.debug("IRequestWSDAO generateUsageReport : Starts : ");
    	 
    	 ioLogger.debug("IRequestWSDAO generateUsageReport : date range between "+start+" and "+end);

    	 Connection lvConnection = null;
    	 PreparedStatement lvPreparedStatement = null;
    	 ResultSet lvResultSet = null;

    	 StringBuffer loCompleteQuery = new StringBuffer("");
    	 String lvMethodName = "";
    	 String lvStatus = "";
    	 String lvCounts = "";
    	 String lvStatusCount = "";
    	 String lvErrorCode = "";
    	 int createincidentcount = 0;
    	 int lognotescount = 0;
    	 int selectproductcount = 0;
    	 int selectproblemtypecount = 0;
    	 //int errorcount = 0;
    	 //int successcount = 0;
    	 HashMap lvMap = new HashMap();

    	 lvMap.put(IRequestWSConstants.CallerCreateIncident+"Success", new Integer(createincidentcount));
    	 lvMap.put(IRequestWSConstants.CallerCreateIncident+"Error", new Integer(createincidentcount));
    	 lvMap.put(IRequestWSConstants.CallerLogNotes+"Success", new Integer(lognotescount));
    	 lvMap.put(IRequestWSConstants.CallerLogNotes+"Error", new Integer(lognotescount));
    	 lvMap.put(IRequestWSConstants.CallerSelectProduct+"Success", new Integer(selectproductcount));
    	 lvMap.put(IRequestWSConstants.CallerSelectProduct+"Error", new Integer(selectproductcount));
    	 lvMap.put(IRequestWSConstants.CallerSelectProblemType+"Success", new Integer(selectproblemtypecount));
    	 lvMap.put(IRequestWSConstants.CallerSelectProblemType+"Error", new Integer(selectproblemtypecount));
    	 lvMap.put(IRequestWSConstants.CallerCreateIncident+"ErrorCode", new Integer(0));
    	 lvMap.put(IRequestWSConstants.CallerLogNotes+"ErrorCode", new Integer(0));
    	 lvMap.put(IRequestWSConstants.CallerSelectProduct+"ErrorCode", new Integer(0));
    	 lvMap.put(IRequestWSConstants.CallerSelectProblemType+"ErrorCode", new Integer(0));

    	 loCompleteQuery = loCompleteQuery
    			 .append(" SELECT METHODNAME, COUNT(METHODNAME), STATUS, COUNT(STATUS) FROM IREQUESTWSMASTER  WHERE STARTTIME > Trunc(SYSDATE - "+start+") AND STARTTIME < (SYSDATE - "+end+") GROUP BY METHODNAME, STATUS ORDER BY METHODNAME ASC ");

    	 try
    	 {
    		 lvConnection = DBUtilities.getConnection();
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : loCompleteQuery : method counts : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );


    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 while(lvResultSet.next())
    		 {
    			 lvMethodName = lvResultSet.getString("METHODNAME");
    			 lvCounts = lvResultSet.getString("COUNT(METHODNAME)");
    			 lvStatus = lvResultSet.getString("STATUS");
    			 lvStatusCount = lvResultSet.getString("COUNT(STATUS)");
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvMethodName : "+lvMethodName);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvCounts : "+lvCounts);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvStatus : "+lvStatus);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvStatusCount : "+lvStatusCount);
        		 if(lvStatus == null || lvStatus.equals(""))
        		 {
        			 lvStatus = "Error";
        		 }
        		 if(lvMethodName != null && lvCounts != null)
        		 {
        			 if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerCreateIncident) && lvStatus.equals("Success"))
        			 {
        				 createincidentcount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerCreateIncident+"Success", new Integer(createincidentcount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerCreateIncident) && lvStatus.equals("Error"))
        			 {
        				 createincidentcount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerCreateIncident+"Error", new Integer(createincidentcount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerLogNotes) && lvStatus.equals("Success"))
        			 {
        				 lognotescount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerLogNotes+"Success", new Integer(lognotescount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerLogNotes) && lvStatus.equals("Error"))
        			 {
        				 lognotescount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerLogNotes+"Error", new Integer(lognotescount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerSelectProduct) && lvStatus.equals("Success"))
        			 {
        				 selectproductcount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerSelectProduct+"Success", new Integer(selectproductcount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerSelectProduct) && lvStatus.equals("Error"))
        			 {
        				 selectproductcount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerSelectProduct+"Error", new Integer(selectproductcount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerSelectProblemType) && lvStatus.equals("Success"))
        			 {
        				 selectproblemtypecount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerSelectProblemType+"Success", new Integer(selectproblemtypecount));
        			 }
        			 else if(lvMethodName.equalsIgnoreCase(IRequestWSConstants.CallerSelectProblemType) && lvStatus.equals("Error"))
        			 {
        				 selectproblemtypecount = new Integer(lvCounts).intValue();
        				 lvMap.put(IRequestWSConstants.CallerSelectProblemType+"Error", new Integer(selectproblemtypecount));
        			 }
        		 }
    		 }
    		 
    		 
    		 loCompleteQuery = new StringBuffer();
    		 lvResultSet = null;
    		 lvPreparedStatement = null;
    		 lvCounts = "";
    		 lvErrorCode = "";
    		 
    		 
    		 loCompleteQuery = loCompleteQuery
        			 .append(" SELECT A.METHODNAME, B.ERRORCODE, COUNT(ERRORCODE) FROM IREQUESTWSMASTER A, IREQUESTWSERROR B WHERE a.STARTTIME > Trunc(SYSDATE - "+start+") AND A.STARTTIME < (SYSDATE - "+end+") AND A.OBJID = B.ERRORTOMASTER AND A.METHODNAME = ? GROUP BY A.METHODNAME, B.ERRORCODE ORDER BY COUNT(ERRORCODE) DESC ");
    		 
    		 lvConnection = DBUtilities.getConnection();
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : loCompleteQuery : status : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, IRequestWSConstants.CallerCreateIncident);
    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 
    		 if(lvResultSet.next())
    		 {
    			 lvErrorCode = lvResultSet.getString("ERRORCODE");
    			 lvCounts = lvResultSet.getString("COUNT(ERRORCODE)");
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : errorcode for createIncident : "+lvErrorCode);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvCounts : "+lvCounts);
        		 lvMap.put(IRequestWSConstants.CallerCreateIncident+"ErrorCode", new Integer(lvErrorCode));
    		 }
    		 
    		 loCompleteQuery = new StringBuffer();
    		 lvResultSet = null;
    		 lvPreparedStatement = null;
    		 lvCounts = "";
    		 lvErrorCode = "";
    		 
    		 
    		 loCompleteQuery = loCompleteQuery
        			 .append(" SELECT A.METHODNAME, B.ERRORCODE, COUNT(ERRORCODE) FROM IREQUESTWSMASTER A, IREQUESTWSERROR B WHERE a.STARTTIME > Trunc(SYSDATE - "+start+") AND A.STARTTIME < (SYSDATE - "+end+") AND A.OBJID = B.ERRORTOMASTER AND A.METHODNAME = ? GROUP BY A.METHODNAME, B.ERRORCODE ORDER BY COUNT(ERRORCODE) DESC ");
    		 
    		 lvConnection = DBUtilities.getConnection();
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : loCompleteQuery : status : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, IRequestWSConstants.CallerLogNotes);
    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 
    		 if(lvResultSet.next())
    		 {
    			 lvErrorCode = lvResultSet.getString("ERRORCODE");
    			 lvCounts = lvResultSet.getString("COUNT(ERRORCODE)");
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : errorcode for logNotes : "+lvErrorCode);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvCounts : "+lvCounts);
        		 lvMap.put(IRequestWSConstants.CallerLogNotes+"ErrorCode", new Integer(lvErrorCode));
    		 }
    		 
    		 loCompleteQuery = new StringBuffer();
    		 lvResultSet = null;
    		 lvPreparedStatement = null;
    		 lvCounts = "";
    		 lvErrorCode = "";
    		 
    		 
    		 loCompleteQuery = loCompleteQuery
        			 .append(" SELECT A.METHODNAME, B.ERRORCODE, COUNT(ERRORCODE) FROM IREQUESTWSMASTER A, IREQUESTWSERROR B WHERE a.STARTTIME > Trunc(SYSDATE - "+start+") AND A.STARTTIME < (SYSDATE - "+end+") AND A.OBJID = B.ERRORTOMASTER AND A.METHODNAME = ? GROUP BY A.METHODNAME, B.ERRORCODE ORDER BY COUNT(ERRORCODE) DESC ");
    		 
    		 lvConnection = DBUtilities.getConnection();
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : loCompleteQuery : status : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, IRequestWSConstants.CallerSelectProduct);
    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 
    		 if(lvResultSet.next())
    		 {
    			 lvErrorCode = lvResultSet.getString("ERRORCODE");
    			 lvCounts = lvResultSet.getString("COUNT(ERRORCODE)");
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : errorcode for selectProduct : "+lvErrorCode);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvCounts : "+lvCounts);
        		 lvMap.put(IRequestWSConstants.CallerSelectProduct+"ErrorCode", new Integer(lvErrorCode));
    		 }
    		 
    		 loCompleteQuery = new StringBuffer();
    		 lvResultSet = null;
    		 lvPreparedStatement = null;
    		 lvCounts = "";
    		 lvErrorCode = "";
    		 
    		 
    		 loCompleteQuery = loCompleteQuery
        			 .append(" SELECT A.METHODNAME, B.ERRORCODE, COUNT(ERRORCODE) FROM IREQUESTWSMASTER A, IREQUESTWSERROR B WHERE a.STARTTIME > Trunc(SYSDATE - "+start+") AND A.STARTTIME < (SYSDATE - "+end+") AND A.OBJID = B.ERRORTOMASTER AND A.METHODNAME = ? GROUP BY A.METHODNAME, B.ERRORCODE ORDER BY COUNT(ERRORCODE) DESC ");
    		 
    		 lvConnection = DBUtilities.getConnection();
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : loCompleteQuery : status : "+loCompleteQuery);
    		 lvPreparedStatement = lvConnection.prepareStatement(loCompleteQuery.toString(),
    				 ResultSet.TYPE_SCROLL_INSENSITIVE,
    				 ResultSet.CONCUR_READ_ONLY
    				 );

    		 lvPreparedStatement.setString(1, IRequestWSConstants.CallerSelectProblemType);
    		 lvResultSet = lvPreparedStatement.executeQuery();

    		 
    		 if(lvResultSet.next())
    		 {
    			 lvErrorCode = lvResultSet.getString("ERRORCODE");
    			 lvCounts = lvResultSet.getString("COUNT(ERRORCODE)");
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : errorcode for select problem type : "+lvErrorCode);
        		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvCounts : "+lvCounts);
        		 lvMap.put(IRequestWSConstants.CallerSelectProblemType+"ErrorCode", new Integer(lvErrorCode));
    		 }
    		 
    		 
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : lvMap : "+lvMap);
    		 
    		 aMasterMap.put((new Integer(start).toString()+"-"+new Integer(end).toString()), lvMap);
    		 
    		 ioLogger.debug("IRequestWSDAO generateUsageReport : aMasterMap : "+aMasterMap);

    	 } catch (SQLException se) {
    		 se.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(se), null);
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(e), null);
    	 } finally {
    		 DBUtilities.closeResultSet (lvResultSet);
    		 DBUtilities.closePreparedStatement (lvPreparedStatement);
    		 DBUtilities.closeConnection(lvConnection);
    	 }

    	 ioLogger.debug("IRequestWSDAO generateUsageReport : Ends : ");

     }
}
