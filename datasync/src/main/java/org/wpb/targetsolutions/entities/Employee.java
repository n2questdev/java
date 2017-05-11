package org.wpb.targetsolutions.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

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
	String sync_status;
	String sync_reason;
	Date sync_timestamp;
	Long datasync_job_id;

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

	public Date getEffective_hire() {
		return effective_hire;
	}

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

	public String getSync_status() {
		return sync_status;
	}

	public void setSync_status(String sync_status) {
		this.sync_status = sync_status;
	}

	public String getSync_reason() {
		return sync_reason;
	}

	public void setSync_reason(String sync_reason) {
		this.sync_reason = sync_reason;
	}

	public Date getSync_timestamp() {
		return sync_timestamp;
	}

	public void setSync_timestamp(Date sync_timestamp) {
		this.sync_timestamp = sync_timestamp;
	}

	public Long getDatasync_job_id() {
		return datasync_job_id;
	}

	public void setDatasync_job_id(Long datasync_job_id) {
		this.datasync_job_id = datasync_job_id;
	}

	public String toString()
	{
		return getEmployeeid() + ", " + getFirstname() + ", " + getLastname();
	}
	// JSON Object "User"
	// {
	// "links": {
	// "credentials":
	// "http://devsandbox.targetsolutions.com/v1/users/1266493/credentials",
	// "resourcelink": "http://devsandbox.targetsolutions.com/v1/users/1266493",
	// "groups": "http://devsandbox.targetsolutions.com/v1/users/1266493/groups"
	// },
	// }
	//
}
