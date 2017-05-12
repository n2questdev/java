package org.wpb.targetsolutions.entities;

import java.io.Serializable;
import java.net.URI;

public class Email implements Serializable {

	private static final long serialVersionUID = 1L;

	URI link;
	String email;
	String status;
	String emailid;

	public Email() {

	}

	public Email(String email, String status) {
		this.email = email;
		this.status = status;
	}

	public URI getLink() {
		return link;
	}

	public void setLink(URI link) {
		this.link = link;
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
	
	public String toString() {
		return "Email::" + System.lineSeparator() + 
				"-------" + System.lineSeparator() + 
				"Email: " + getEmail() + ", " + System.lineSeparator() + 
				"Status: " + getStatus() + ", " + System.lineSeparator() + 
				"Emailid: " + getEmailid() + ", " + System.lineSeparator();
	}

}
