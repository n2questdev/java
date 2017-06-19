package org.wpb.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * TODO: Cache all Profiles at application startup
 * 
 * URL: GET from -
 * http://api.targetsolutions.com/v1/sites/28658/categories/profile {
 * "profilecategories": [ { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/23781",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/23781/groups"
 * } ], "siteid": 28658, "categoryid": 23781, "categoryname": "Department",
 * "groupcount": 19 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25117",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25117/groups"
 * } ], "siteid": 28658, "categoryid": 25117, "categoryname": "Division",
 * "groupcount": 99 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25503",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25503/groups"
 * } ], "siteid": 28658, "categoryid": 25503, "categoryname": "Effective Hire
 * Date", "groupcount": 1463 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25130",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25130/groups"
 * } ], "siteid": 28658, "categoryid": 25130, "categoryname": "Employee Group",
 * "groupcount": 11 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25123",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25123/groups"
 * } ], "siteid": 28658, "categoryid": 25123, "categoryname": "Employment
 * Category", "groupcount": 7 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25118",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25118/groups"
 * } ], "siteid": 28658, "categoryid": 25118, "categoryname": "Job Title",
 * "groupcount": 411 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25121",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25121/groups"
 * } ], "siteid": 28658, "categoryid": 25121, "categoryname": "Management",
 * "groupcount": 3 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/26106",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/26106/groups"
 * } ], "siteid": 28658, "categoryid": 26106, "categoryname": "Supervisor",
 * "groupcount": 256 }, { "links": [ { "resourcelink":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25128",
 * "groups":
 * "http://api.targetsolutions.com/v1/sites/28658/categories/profile/25128/groups"
 * } ], "siteid": 28658, "categoryid": 25128, "categoryname": "Supervisor
 * Responsibility", "groupcount": 2 } ] }
 * 
 * @author A
 *
 */
public class ProfileCategory {
	private Links links;
	private int siteid;
	private int categoryid;
	private String categoryname;
	private int groupcount;
	
	@JsonIgnore
	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	public int getSiteid() {
		return siteid;
	}

	public void setSiteid(int siteid) {
		this.siteid = siteid;
	}

	public int getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname.trim();
	}

	public int getGroupcount() {
		return groupcount;
	}

	public void setGroupcount(int groupcount) {
		this.groupcount = groupcount;
	}

}
