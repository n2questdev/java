package org.wpb.lms.entities;

import java.net.URI;
import java.util.Date;

import org.wpb.lms.integration.api.helpers.JsonDateDeserializer;
import org.wpb.lms.integration.api.helpers.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Credential {
	// "expirationdate": "05/13/2017",
	// "credentialid": 162095,
	// "startdate": "02/13/2017",
	// "link": "http://devsandbox.targetsolutions.com/v1/credentials/162095/assignments/162095-1339145",
	// "credentialnumber": "",
	// "status": "active",
	// "attachmentcount": 0,
	// "notes": ""
	Date expirationdate;
	Long credentialid;
	Date startdate;
	URI link;
	Long credentialnumber;
	String status;
	Integer attachmentcount;
	String notes;

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getExpirationdate() {
		return expirationdate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setExpirationdate(Date expirationdate) {
		this.expirationdate = expirationdate;
	}

	public Long getCredentialid() {
		return credentialid;
	}

	public void setCredentialid(Long credentialid) {
		this.credentialid = credentialid;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getStartdate() {
		return startdate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Long getCredentialnumber() {
		return credentialnumber;
	}

	public void setCredentialnumber(Long credentialnumber) {
		this.credentialnumber = credentialnumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status.trim();
	}

	public Integer getAttachmentcount() {
		return attachmentcount;
	}

	public void setAttachmentcount(Integer attachmentcount) {
		this.attachmentcount = attachmentcount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes.trim();
	}

	public URI getLink() {
		return link;
	}

	public void setLink(URI link) {
		this.link = link;
	}

	public String toString() {
		return "Credential::" + System.lineSeparator() + "------------" + System.lineSeparator() + "expirationdate: "
				+ getExpirationdate() + System.lineSeparator() + "credentialid: " + getCredentialid()
				+ System.lineSeparator() + "startdate: " + getStartdate() + System.lineSeparator()
				+ "credentialnumber: " + getCredentialnumber() + System.lineSeparator() + "status: " + getStatus()
				+ System.lineSeparator() + "attachmentcount: " + getAttachmentcount() + System.lineSeparator()
				+ "notes: " + getNotes();
	}
	// Date expirationdate;
	// Long credentialid;
	// Date startdate;
	// URI link;
	// Long credentialnumber;
	// String status;
	// Integer attachmentcount;
	// String notes;

}
