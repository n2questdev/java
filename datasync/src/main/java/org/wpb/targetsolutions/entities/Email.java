package org.wpb.targetsolutions.entities;

import java.io.Serializable;

public class Email implements Serializable {

	private static final long serialVersionUID = 1L;

	String email;
	String status;
	String emailid;

	public Email() {

	}

	public Email(String email, String status) {
		this.email = email;
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmailid() {
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

}
