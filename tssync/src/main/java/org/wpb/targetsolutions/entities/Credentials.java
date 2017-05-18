package org.wpb.targetsolutions.entities;

import java.net.URI;
import java.util.Date;

public class Credentials {
//    "expirationdate": "05/13/2017",
//    "credentialid": 162095,
//    "startdate": "02/13/2017",
//    "link": "http://devsandbox.targetsolutions.com/v1/credentials/162095/assignments/162095-1339145",
//    "credentialnumber": "",
//    "status": "active",
//    "attachmentcount": 0,
//    "notes": ""
	Date expirationdate;
	Long credentialid;
	Date startdate;
	URI link;
	Long credentialnumber;
	String status;
	Integer attachmentcount;
	String notes;

	public Date getExpirationdate() {
		return expirationdate;
	}

	public void setExpirationdate(Date expirationdate) {
		this.expirationdate = expirationdate;
	}

	public Long getCredentialid() {
		return credentialid;
	}

	public void setCredentialid(Long credentialid) {
		this.credentialid = credentialid;
	}

	public Date getStartdate() {
		return startdate;
	}

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
		this.status = status;
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
		this.notes = notes;
	}

	public URI getLink() {
		return link;
	}

	public void setLink(URI link) {
		this.link = link;
	}
}
