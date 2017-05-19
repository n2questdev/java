package org.wpb.targetsolutions.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wpb.integration.tssync.utils.JsonDateDeserializer;
import org.wpb.integration.tssync.utils.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	Links links;
	String usertype;
	String siteid;     
	String employeeid;
	String tsuserid;

	String firstname;
	List<Email> email = new ArrayList<Email>();
	String username;
	String status;
	String lastname;
	String userid;
	String temp_password;
	String dept;
	String division;
	String job_title;
	String management;
	String employee_group;
	String employee_category;
	Date effective_hire;
	String supervisor;
	String supervisor_resp;

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

	public String getTemp_password() {
		return temp_password;
	}

	public void setTemp_password(String temp_password) {
		this.temp_password = temp_password;
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

	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getEffective_hire() {
		return effective_hire;
	}
	
	@JsonDeserialize(using=JsonDateDeserializer.class)
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
		return "Employee:: " + System.lineSeparator() +
				"-----------" + System.lineSeparator() +  
				"Links: " + getLinks() + ", " + System.lineSeparator() + 
				"Usertype: " + getUsertype() + ", " + System.lineSeparator() + 
				"SiteID: " + getSiteid() + ", " + System.lineSeparator() + 
				"EmployeeID: " + getEmployeeid() + ", " + System.lineSeparator() +
				"TSuserid: " + getTsuserid() + ", " + System.lineSeparator() +
				"FirstName: " + getFirstname() + ", " + System.lineSeparator() +
				"Email: " + getEmail() + ", " + System.lineSeparator() +
				"Username: " + getUsername() + ", " + System.lineSeparator() +
				"Status: " + getStatus() + ", " + System.lineSeparator() +
				"Lastname: " + getLastname() + ", " + System.lineSeparator() +
				"Userid: " + getUserid() + ", " + System.lineSeparator() +
				"Temp_password: " + getTemp_password() + ", " + System.lineSeparator() +
				"Dept: " + getDept() + ", " + System.lineSeparator() +
				"Division: " + getDivision() + ", " + System.lineSeparator() +
				"Job_Title: " + getJob_title() + ", " + System.lineSeparator() +
				"Management: " + getManagement() + ", " + System.lineSeparator() +
				"Employee_Group: " + getEmployee_group() + ", " + System.lineSeparator() +
				"Employee_Category: " + getEmployee_category() + ", " + System.lineSeparator() +
				"Effective_Hire: " + getEffective_hire() + ", " + System.lineSeparator() +
				"Supervisor: " + getSupervisor() + ", " + System.lineSeparator() +
				"Supervisor_resp: " + getSupervisor_resp() + ", " + System.lineSeparator();
	}
}
