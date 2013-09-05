package com.ncsi.olr.webservice;

import java.util.ArrayList;

import org.w3c.dom.Document;

public class IRequestVO {
	
	private Integer TransactionId;
	
	private ClarifyBOVO ClarifyBOVO;
	
	private String Source;
	
	private String UserId;
	
	private String Status;
	
	private ArrayList Error;
	
	private ArrayList XSDValidationError;
	
	private String Timestamp;
	
	private String InboundXML;
	
	private String OutboundXML;
	
	private Document InboundXMLDoc;
	
	private boolean InboundXMLValidated;
	
	private String IRequestWSMasterObjId;
	
	private boolean ParsingError;	
	
	public boolean isParsingError() {
		return ParsingError;
	}

	public void setParsingError(boolean parsingError) {
		ParsingError = parsingError;
	}

	public String getIRequestWSMasterObjId() {
		return IRequestWSMasterObjId;
	}

	public void setIRequestWSMasterObjId(String iRequestWSMasterObjId) {
		IRequestWSMasterObjId = iRequestWSMasterObjId;
	}

	public boolean isInboundXMLValidated() {
		return InboundXMLValidated;
	}

	public void setInboundXMLValidated(boolean inboundXMLValidated) {
		InboundXMLValidated = inboundXMLValidated;
	}

	public Document getInboundXMLDoc() {
		return InboundXMLDoc;
	}

	public void setInboundXMLDoc(Document inboundXMLDoc) {
		InboundXMLDoc = inboundXMLDoc;
	}

	public String getInboundXML() {
		return InboundXML;
	}

	public void setInboundXML(String inboundXML) {
		InboundXML = inboundXML;
	}

	public String getOutboundXML() {
		return OutboundXML;
	}

	public void setOutboundXML(String outboundXML) {
		OutboundXML = outboundXML;
	}
	
	public ClarifyBOVO getClarifyBOVO() {
		return ClarifyBOVO;
	}

	public void setClarifyBOVO(ClarifyBOVO clarifyBOVO) {
		ClarifyBOVO = clarifyBOVO;
	}

	public Integer getTransactionId() {
		return TransactionId;
	}

	public void setTransactionId(Integer transactionId) {
		TransactionId = transactionId;
	}

	

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public ArrayList getError() {
		return Error;
	}

	public void setError(ArrayList error) {
		Error = error;
	}

	public String getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}

	public ArrayList getXSDValidationError() {
		return XSDValidationError;
	}

	public void setXSDValidationError(ArrayList xSDValidationError) {
		XSDValidationError = xSDValidationError;
	}	

}
