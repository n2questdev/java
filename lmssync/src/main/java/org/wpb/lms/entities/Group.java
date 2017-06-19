package org.wpb.lms.entities;

public class Group {
	String usercount;
	String groupid;
	String siteid;
	String categoryid;
	String groupname;
	Links links;

	public String getUsercount() {
		return usercount;
	}

	public void setUsercount(String usercount) {
		this.usercount = usercount.trim();
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid.trim();
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid.trim();
	}

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid.trim();
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname.trim();
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	public class Links {
		String resourcelink;

		public String getResourcelink() {
			return resourcelink;
		}

		public void setResourcelink(String resourcelink) {
			this.resourcelink = resourcelink.trim();
		}

		public String toString() {
			return "Group Links:: " + System.lineSeparator() + "---------------" + System.lineSeparator()
					+ "resourcelink: " + getResourcelink();
		}
	}

	public String toString() {
		return "Group:: " + System.lineSeparator() + "--------" + System.lineSeparator() + "usercount: "
				+ getUsercount() + System.lineSeparator() + "groupid: " + getGroupid() + System.lineSeparator()
				+ "siteid: " + getSiteid() + System.lineSeparator() + "categoryid: " + getCategoryid()
				+ System.lineSeparator() + "groupname: " + getGroupname() + System.lineSeparator() + getLinks();
	}

	// http://devsandbox.targetsolutions.com/v1/users/1339145/groups
	// {
	// "groups": [
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/504076"
	// },
	// "usercount": 3,
	// "groupid": 504076,
	// "siteid": 28658,
	// "categoryid": 25503,
	// "groupname": " 2016/11/14"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/473755"
	// },
	// "usercount": 1494,
	// "groupid": 473755,
	// "siteid": 28658,
	// "categoryid": 25123,
	// "groupname": "FR"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/474167"
	// },
	// "usercount": 46,
	// "groupid": 474167,
	// "siteid": 28658,
	// "categoryid": 23781,
	// "groupname": "Information Technology"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/474193"
	// },
	// "usercount": 12,
	// "groupid": 474193,
	// "siteid": 28658,
	// "categoryid": 25117,
	// "groupname": "IT Systems Development"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/505034"
	// },
	// "usercount": 12,
	// "groupid": 505034,
	// "siteid": 28658,
	// "categoryid": 26106,
	// "groupname": "McCarty Jason Scot"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/473759"
	// },
	// "usercount": 1444,
	// "groupid": 473759,
	// "siteid": 28658,
	// "categoryid": 25128,
	// "groupname": false
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/473750"
	// },
	// "usercount": 1675,
	// "groupid": 473750,
	// "siteid": 28658,
	// "categoryid": 25121,
	// "groupname": "None"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/473888"
	// },
	// "usercount": 172,
	// "groupid": 473888,
	// "siteid": 28658,
	// "categoryid": 25130,
	// "groupname": "PMSA"
	// },
	// {
	// "links": {
	// "resourcelink":
	// "http://devsandbox.targetsolutions.com/v1/users/1339145/groups/474235"
	// },
	// "usercount": 4,
	// "groupid": 474235,
	// "siteid": 28658,
	// "categoryid": 25118,
	// "groupname": "Systems Analyst"
	// }
	// ]
	// }

}
