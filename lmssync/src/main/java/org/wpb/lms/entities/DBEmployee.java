package org.wpb.lms.entities;

import java.io.Serializable;
import java.util.Date;

public class DBEmployee implements Serializable {

	private static final long serialVersionUID = 1L;

	String FIRST_NAME_MI;
	String LAST_NAME;
	String EMPLOYEE_ID;
	String USERNAME;
	String EMAIL;
	String TEMP_PASSWORD;
	String DEPT;
	String DIVISION;
	String JOB_TITLE;
	String MANAGEMENT;
	String EMPLOYEE_GROUP;
	String EMPLOYEE_CATEGORY;
	String TS_USER_ID;
	Date EFFECTIVE_HIRE;
	String SUPERVISOR;
	String SUPERVISOR_RESP;
	String SYNC_STATUS;
	String SYNC_REASON;
	String SYNC_TIMESTAMP;
	String DATASYNC_JOB_ID;
	String STATUS;

	public String getFIRST_NAME_MI() {
		return FIRST_NAME_MI;
	}

	public void setFIRST_NAME_MI(String fIRST_NAME_MI) {
		FIRST_NAME_MI = fIRST_NAME_MI != null ? fIRST_NAME_MI.trim() : "";
	}

	public String getLAST_NAME() {
		return LAST_NAME;
	}

	public void setLAST_NAME(String lAST_NAME) {
		LAST_NAME = lAST_NAME != null ? lAST_NAME.trim() : "";
	}

	public String getEMPLOYEE_ID() {
		return EMPLOYEE_ID;
	}

	public void setEMPLOYEE_ID(String eMPLOYEE_ID) {
		EMPLOYEE_ID = eMPLOYEE_ID != null ? eMPLOYEE_ID.trim() : "";
	}

	public String getUSERNAME() {
		return USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME != null ? uSERNAME.trim() : "";
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL != null ? eMAIL.trim() : "";
	}

	public String getTEMP_PASSWORD() {
		return TEMP_PASSWORD;
	}

	public void setTEMP_PASSWORD(String tEMP_PASSWORD) {
		TEMP_PASSWORD = tEMP_PASSWORD != null ? tEMP_PASSWORD.trim() : "";
	}

	public String getDEPT() {
		return DEPT;
	}

	public void setDEPT(String dEPT) {
		DEPT = dEPT != null ? dEPT.trim() : "";
	}

	public String getDIVISION() {
		return DIVISION;
	}

	public void setDIVISION(String dIVISION) {
		DIVISION = dIVISION != null ? dIVISION.trim() : "";
	}

	public String getJOB_TITLE() {
		return JOB_TITLE;
	}

	public void setJOB_TITLE(String jOB_TITLE) {
		JOB_TITLE = jOB_TITLE != null ? jOB_TITLE.trim() : "";
	}

	public String getMANAGEMENT() {
		return MANAGEMENT;
	}

	public void setMANAGEMENT(String mANAGEMENT) {
		MANAGEMENT = mANAGEMENT != null ? mANAGEMENT.trim() : "";
	}

	public String getEMPLOYEE_GROUP() {
		return EMPLOYEE_GROUP;
	}

	public void setEMPLOYEE_GROUP(String eMPLOYEE_GROUP) {
		EMPLOYEE_GROUP = eMPLOYEE_GROUP != null ? eMPLOYEE_GROUP.trim() : "";
	}

	public String getEMPLOYEE_CATEGORY() {
		return EMPLOYEE_CATEGORY;
	}

	public void setEMPLOYEE_CATEGORY(String eMPLOYEE_CATEGORY) {
		EMPLOYEE_CATEGORY = eMPLOYEE_CATEGORY != null ? eMPLOYEE_CATEGORY.trim() : "";
	}

	public String getTS_USER_ID() {
		return TS_USER_ID;
	}

	public void setTS_USER_ID(String tS_USER_ID) {
		TS_USER_ID = tS_USER_ID != null ? tS_USER_ID.trim() : "";
	}

	public Date getEFFECTIVE_HIRE() {
		return EFFECTIVE_HIRE;
	}

	public void setEFFECTIVE_HIRE(Date eFFECTIVE_HIRE) {
		EFFECTIVE_HIRE = eFFECTIVE_HIRE != null ? eFFECTIVE_HIRE : new Date();
	}
	
	public String getSUPERVISOR() {
		return SUPERVISOR;
	}

	public void setSUPERVISOR(String sUPERVISOR) {
		SUPERVISOR = sUPERVISOR != null ? sUPERVISOR.trim() : "";
	}

	public String getSUPERVISOR_RESP() {
		return SUPERVISOR_RESP;
	}

	public void setSUPERVISOR_RESP(String sUPERVISOR_RESP) {
		SUPERVISOR_RESP = sUPERVISOR_RESP != null ? sUPERVISOR_RESP.trim() : "";
	}

	public String getSYNC_STATUS() {
		return SYNC_STATUS;
	}

	public void setSYNC_STATUS(String sYNC_STATUS) {
		SYNC_STATUS = sYNC_STATUS != null ? sYNC_STATUS.trim() : "";
	}

	public String getSYNC_REASON() {
		return SYNC_REASON;
	}

	public void setSYNC_REASON(String sYNC_REASON) {
		SYNC_REASON = sYNC_REASON != null ? sYNC_REASON.trim() : "";
	}

	public String getSYNC_TIMESTAMP() {
		return SYNC_TIMESTAMP;
	}

	public void setSYNC_TIMESTAMP(String sYNC_TIMESTAMP) {
		SYNC_TIMESTAMP = sYNC_TIMESTAMP != null ? sYNC_TIMESTAMP.trim() : "";
	}

	public String getDATASYNC_JOB_ID() {
		return DATASYNC_JOB_ID;
	}

	public void setDATASYNC_JOB_ID(String dATASYNC_JOB_ID) {
		DATASYNC_JOB_ID = dATASYNC_JOB_ID != null ? dATASYNC_JOB_ID.trim() : "";
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
}
