package org.wpb.targetsolutions.entities;

import java.net.URI;

public class Links {
	URI credentials;
	URI resourcelink;
	URI groups;

	public URI getCredentials() {
		return credentials;
	}

	public void setCredentials(URI credentials) {
		this.credentials = credentials;
	}

	public URI getResourcelink() {
		return resourcelink;
	}

	public void setResourcelink(URI resourcelink) {
		this.resourcelink = resourcelink;
	}

	public URI getGroups() {
		return groups;
	}

	public void setGroups(URI groups) {
		this.groups = groups;
	}

	public String toString() {
		return 	"Link:: " + System.lineSeparator() +
				"-------" + System.lineSeparator() +
				"CredentialsLink: " + getCredentials() + System.lineSeparator() +
				"ResourceLink: " + getResourcelink() + System.lineSeparator() +
				"GroupsLink: " + getGroups() + System.lineSeparator();
	}
}
