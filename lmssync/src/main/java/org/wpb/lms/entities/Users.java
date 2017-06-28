package org.wpb.lms.entities;

import java.util.List;

public class Users {
	List<Employee> users;
	private String httpcode;
	private String moreinfo;
	private String developermessage;
	private String statuscode;
	private String status;

	public List<Employee> getUsers() {
		return users;
	}

	public void setUsers(List<Employee> users) {
		this.users = users;
	}

	public String getHttpcode() {
		return httpcode;
	}

	public void setHttpcode(String httpcode) {
		this.httpcode = httpcode;
	}

	public String getMoreinfo() {
		return moreinfo;
	}

	public void setMoreinfo(String moreinfo) {
		this.moreinfo = moreinfo;
	}

	public String getDevelopermessage() {
		return developermessage;
	}

	public void setDevelopermessage(String developermessage) {
		this.developermessage = developermessage;
	}

	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}
