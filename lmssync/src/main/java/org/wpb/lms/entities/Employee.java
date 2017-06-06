package org.wpb.lms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wpb.lms.integration.utils.JsonDateDeserializer;
import org.wpb.lms.integration.utils.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	private Links links;
	private String usertype;
	private String siteid;
	private String employeeid;
	private String tsuserid;

	private String firstname;
	private List<Email> email = new ArrayList<Email>();
	private String username;
	private String status;
	private String lastname;
	private String userid;
	private String password;
	private String dept;
	private String division;
	private String job_title;
	private String management;
	private String employee_group;
	private String employee_category;
	private Date effective_hire;
	private String supervisor;
	private String supervisor_resp;
	private String httpcode;
	private String moreinfo;
	private String developermessage;
	private String statuscode;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getEmployeeid() {
		return employeeid;
	}

	public void setEmployeeid(String employeeid) {
		this.employeeid = employeeid;
	}

	public List<Email> getEmail() {
		return email;
	}

	public void setEmail(List<Email> email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Status is overloaded attribute:
	 * 1. For successful request, status indicates Active, Inactive or Offline
	 * 2. For failed request, status indicates HTTP error message
	 * @return User Status (or) HTTP error message
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getJob_title() {
		return job_title;
	}

	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}

	public String getManagement() {
		return management;
	}

	public void setManagement(String management) {
		this.management = management;
	}

	public String getEmployee_group() {
		return employee_group;
	}

	public void setEmployee_group(String employee_group) {
		this.employee_group = employee_group;
	}

	public String getEmployee_category() {
		return employee_category;
	}

	public void setEmployee_category(String employee_category) {
		this.employee_category = employee_category;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getEffective_hire() {
		return effective_hire;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setEffective_hire(Date effective_hire) {
		this.effective_hire = effective_hire;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getSupervisor_resp() {
		return supervisor_resp;
	}

	public void setSupervisor_resp(String supervisor_resp) {
		this.supervisor_resp = supervisor_resp;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	public String getTsuserid() {
		return tsuserid;
	}

	public void setTsuserid(String tsuserid) {
		this.tsuserid = tsuserid;
	}

	public String toString() {
		return "Employee:: " + System.lineSeparator() + "-----------" + System.lineSeparator() + "Links: " + getLinks()
				+ ", " + System.lineSeparator() + "Usertype: " + getUsertype() + ", " + System.lineSeparator()
				+ "SiteID: " + getSiteid() + ", " + System.lineSeparator() + "EmployeeID: " + getEmployeeid() + ", "
				+ System.lineSeparator() + "TSuserid: " + getTsuserid() + ", " + System.lineSeparator() + "FirstName: "
				+ getFirstname() + ", " + System.lineSeparator() + "Email: " + getEmail() + ", "
				+ System.lineSeparator() + "Username: " + getUsername() + ", " + System.lineSeparator() + "Status: "
				+ getStatus() + ", " + System.lineSeparator() + "Lastname: " + getLastname() + ", "
				+ System.lineSeparator() + "Userid: " + getUserid() + ", " + System.lineSeparator() + "Temp_password: "
				+ getPassword() + ", " + System.lineSeparator() + "Dept: " + getDept() + ", " + System.lineSeparator()
				+ "Division: " + getDivision() + ", " + System.lineSeparator() + "Job_Title: " + getJob_title() + ", "
				+ System.lineSeparator() + "Management: " + getManagement() + ", " + System.lineSeparator()
				+ "Employee_Group: " + getEmployee_group() + ", " + System.lineSeparator() + "Employee_Category: "
				+ getEmployee_category() + ", " + System.lineSeparator() + "Effective_Hire: " + getEffective_hire()
				+ ", " + System.lineSeparator() + "Supervisor: " + getSupervisor() + ", " + System.lineSeparator()
				+ "Supervisor_resp: " + getSupervisor_resp() + ", " + System.lineSeparator();
	}

	public String htmlPrint() {
		return "<table style=\"width:100%\">"
				+ "<tr>"
				+ "<th>User Type</th>"
				+ "<th>Employee ID</th>"
				+ "<th>First Name</th>"
				+ "<th>Last Name</th>"
				+ "<th>User Name</th>"
				+ "<th>Status</th>"
				+ "<th>User ID</th>"
				+ "<th>Emails</th>"
				+ "</tr><tr>"
				+ "<td>" + getUsertype() + "</td>"
				+ "<td>" + getEmployeeid() + "</td>"
				+ "<td>" + getFirstname() + "</td>"
				+ "<td>" + getLastname() + "</td>"
				+ "<td>" + getUsername() + "</td>"
				+ "<td>" + getStatus() + "</td>"
				+ "<td>" + getUserid() + "</td>"
				+ "<td>" + getEmail() + "</td>"
				+ "</tr>"
				+ "</table>";
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
}
