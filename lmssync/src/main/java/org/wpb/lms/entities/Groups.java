package org.wpb.lms.entities;

import java.util.List;

public class Groups {
	private List<Group> groups;
	private String httpcode;
	private String moreinfo;
	private String developermessage;
	private String statuscode;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status.trim();
	}

	/**
	 * API trinkets!! API expects "profilegroups" when you want to assign a
	 * group to a user
	 * 
	 * @return
	 */
	public List<Group> getProfilegroups() {
		return groups;
	}

	public void setProfilegroups(List<Group> groups) {
		this.groups = groups;
	}

	/**
	 * API trinkets!! API will return "groups" when you ask for groups for a
	 * given user
	 * 
	 * @return list of Groups
	 */
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getHttpcode() {
		return httpcode;
	}

	public void setHttpcode(String httpcode) {
		this.httpcode = httpcode.trim();
	}

	public String getMoreinfo() {
		return moreinfo;
	}

	public void setMoreinfo(String moreinfo) {
		this.moreinfo = moreinfo.trim();
	}

	public String getDevelopermessage() {
		return developermessage;
	}

	public void setDevelopermessage(String developermessage) {
		this.developermessage = developermessage.trim();
	}

	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode.trim();
	}
}
