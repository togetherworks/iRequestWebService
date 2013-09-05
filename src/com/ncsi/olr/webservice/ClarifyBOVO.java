package com.ncsi.olr.webservice;

import java.util.ArrayList;
import java.util.HashMap;


public class ClarifyBOVO {
	
	private Integer IncidentId;
	
	private String Incident_Title;
	
	private String Severity;
	
	private String Priority;
	
	private String Product;
	
	private String Problem_Type;
	
	private String Initial_Log;
	
	private HashMap External_Reference;
	
	private String Contact_First_Name;
	
	private String Contact_Last_Name;
	
	private String Contact_Phone;
	
	private String Notes;
	
	private String Product_Partner_Number;
	
	private ArrayList Product_List;
	
	private ArrayList Problem_Type_List;
	
	private String IncidentObjId;
	
	private String QueueName;
	
	private Integer AutoDestId;
	
	

	public String getQueueName() {
		return QueueName;
	}

	public void setQueueName(String queueName) {
		QueueName = queueName;
	}

	public Integer getAutoDestId() {
		return AutoDestId;
	}

	public void setAutoDestId(Integer autoDestId) {
		AutoDestId = autoDestId;
	}

	public String getIncidentObjId() {
		return IncidentObjId;
	}

	public void setIncidentObjId(String incidentObjId) {
		IncidentObjId = incidentObjId;
	}

	public String getProduct_Partner_Number() {
		return Product_Partner_Number;
	}

	public void setProduct_Partner_Number(String product_Partner_Number) {
		Product_Partner_Number = product_Partner_Number;
	}

	public String getContact_Phone() {
		return Contact_Phone;
	}

	public void setContact_Phone(String contact_Phone) {
		Contact_Phone = contact_Phone;
	}

	public Integer getIncidentId() {
		return IncidentId;
	}

	public void setIncidentId(Integer incidentId) {
		IncidentId = incidentId;
	}

	public String getIncident_Title() {
		return Incident_Title;
	}

	public void setIncident_Title(String incident_Title) {
		Incident_Title = incident_Title;
	}



	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

	public String getProduct() {
		return Product;
	}

	public void setProduct(String product) {
		Product = product;
	}

	public String getProblem_Type() {
		return Problem_Type;
	}

	public void setProblem_Type(String problem_Type) {
		Problem_Type = problem_Type;
	}

	public String getInitial_Log() {
		return Initial_Log;
	}

	public void setInitial_Log(String initial_Log) {
		Initial_Log = initial_Log;
	}


	public HashMap getExternal_Reference() {
		return External_Reference;
	}

	public void setExternal_Reference(HashMap external_Reference) {
		External_Reference = external_Reference;
	}

	public String getContact_First_Name() {
		return Contact_First_Name;
	}

	public void setContact_First_Name(String contact_First_Name) {
		Contact_First_Name = contact_First_Name;
	}

	public String getContact_Last_Name() {
		return Contact_Last_Name;
	}

	public void setContact_Last_Name(String contact_Last_Name) {
		Contact_Last_Name = contact_Last_Name;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public ArrayList getProduct_List() {
		return Product_List;
	}

	public void setProduct_List(ArrayList product_List) {
		Product_List = product_List;
	}

	public ArrayList getProblem_Type_List() {
		return Problem_Type_List;
	}

	public void setProblem_Type_List(ArrayList problem_Type_List) {
		Problem_Type_List = problem_Type_List;
	}

	public String getSeverity() {
		return Severity;
	}

	public void setSeverity(String severity) {
		Severity = severity;
	}
	
	
	
}
