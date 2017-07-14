package org.wpb.lms.integration.api.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wpb.lms.entities.Credentials;
import org.wpb.lms.entities.Group;
import org.wpb.lms.entities.Groups;
import org.wpb.lms.entities.ProfileCategories;
import org.wpb.lms.entities.ProfileCategory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIBase {

	public APIBase() {
		super();
	}

	/**
	 * This method returns Users site to perform further operations using Users
	 * site Example: http://devsandbox.targetsolutions.com/v1
	 * 
	 * @param userid
	 * @return {@link WebTarget} mainSiteURL
	 * @throws IOException
	 */

	public WebTarget getMainSite() throws IOException {
		return ClientBuilder.newClient().target(PropertiesUtils.getSiteURI());
	}
 
	/**
	 * This method returns Users site to perform further operations using Users
	 * site Example: http://devsandbox.targetsolutions.com/v1/users
	 * 
	 * @param userid
	 * @return {@link WebTarget} usersSite
	 * @throws IOException
	 */
	public WebTarget getUsersSite() throws IOException {
		return getMainSite().path("users");
	}

	/**
	 * This method returns User site for given userid to perform further
	 * operations using Users site Example:
	 * http://devsandbox.targetsolutions.com/v1/users/1339145/
	 * 
	 * @param userid
	 * @return {@link WebTarget} userSite
	 * @throws IOException
	 */
	public WebTarget getUserSite(String userid) throws IOException {
		return getUsersSite().path(userid);
	}
	
	/**
	 * This method returns Profile Categories site to perform further
	 * operations like assigning groups to users 
	 * Example URI: 
	 * 		http://devsandbox.targetsolutions.com/v1/sites/28658/categories/profile
	 * 
	 * @return {@link WebTarget} profilesSite
	 * @throws IOException
	 */
	public WebTarget getProfileCategoriesSite() throws IOException {
		return getMainSite().path("sites").path(PropertiesUtils.getSiteID())
				.path("categories").path("profile");
	}

	/**
	 * This method returns Groups site for a given Profile Category (a.k.a columns like Department) to perform further
	 * operations like assigning groups to users 
	 * Example URI: 
	 * 		http://api.targetsolutions.com/v1/sites/28658/categories/profile/${categoryID}/groups
	 * 
	 * @return {@link WebTarget} profilesSite
	 * @throws IOException
	 */
	private WebTarget getGroupsSite(String categoryID) throws IOException {
		return getProfileCategoriesSite().path(categoryID).path("groups");
	}

	/**
	 * This method returns direct credentials URL without having to go through
	 * Users site Example: http://devsandbox.targetsolutions.com/v1/credentials
	 * 
	 * @return {@link WebTarget} credentialsSite
	 * @throws IOException
	 */
	public WebTarget getCredentialsSite() throws IOException {
		return getMainSite().path("credentials");
	}

	/**
	 * This method returns User Credentials URL for a given LMS userID
	 * 
	 * Example:
	 * http://devsandbox.targetsolutions.com/v1/users/1265568/credentials
	 * 
	 * @param userid
	 * @return
	 * @throws IOException
	 */
	public WebTarget getUserCredentialsSite(String userid) throws IOException {
		return getUserSite(userid).path("credentials");
	}

	/**
	 * This method returns direct credentials URL without having to go through
	 * Users site using credentialid Example:
	 * http://devsandbox.targetsolutions.com/v1/credentials/162095
	 * 
	 * @param userid
	 * @param credentialid
	 * @return {@link WebTarget} credentialSite
	 * @throws IOException
	 */
	public WebTarget getCredentialSite(String credentialid) throws IOException {
		return getCredentialsSite().path(credentialid);
	}

	/**
	 * This method returns User Groups URL for a given LMS userID
	 * URL: http://devsandbox.targetsolutions.com/v1/users/{userid}/groups
	 * 
	 * @param userid
	 * @return {@link WebTarget} groupsSite
	 * @throws IOException
	 */
	public WebTarget getUserGroupsSite(String userid) throws IOException {
		return getUserSite(userid).path("groups");
	}

	public WebTarget getCreateUserSite() throws IOException {
		return getMainSite().path("sites").path(PropertiesUtils.getSiteID()).path("users");
	}

	/**
	 * This method returns all metadata attributes (columns) to employee like
	 * Department, Division, Title etc... TargetSolutions calls these additional
	 * attributes as categories GET
	 * http://api.targetsolutions.com/v1/sites/28658/categories/profile
	 * 
	 * @return
	 */
	public Map<String, String> getProfileCategories() {
		Response response = null;
		ProfileCategories profileCategories = null;
		HashMap<String, String> categories = new HashMap<String, String>();
		try {
			WebTarget profileCategoriesSite = getProfileCategoriesSite();
			response = profileCategoriesSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken()).get();
	
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
			profileCategories = mapper.readValue(response.readEntity(String.class), ProfileCategories.class);
			if((profileCategories != null) && (profileCategories.getProfilecategories() != null)) {
				for (ProfileCategory category : profileCategories.getProfilecategories()) {
					categories.put(category.getCategoryname(), String.valueOf(category.getCategoryid()));
				}
			}
		} catch (IOException ioe) {
			log.fatal(ioe.getMessage(), ioe);
		} finally {
			if(response != null)
				response.close();
		}
		return categories;
	}
	
/*	
 	public Map<String, String> getGroupsByProfileCategory(String categoryID) {
		Response response = null;
		Groups groupsByCategory = null;
		HashMap<String, String> groups = new HashMap<String, String>();
		try {
			WebTarget groupsSite = getGroupsSite(categoryID);
			response = groupsSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken()).get();
	
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
			groupsByCategory = mapper.readValue(response.readEntity(String.class), Groups.class);
	
			for (Group group : groupsByCategory.getProfilegroups()) {
				groups.put(group.getGroupname(), String.valueOf(group.getGroupid()));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if(response != null)
				response.close();
		}
		return groups;
	}
*/
	public HashMap<String, String> getGroupIDByName(String categoryID, String newGroupName) {
		Response response = null;
		Groups groupsByCategory = null;
		HashMap<String, String> groups = new HashMap<String, String>();
		try {
			WebTarget groupsSite = getGroupsSite(categoryID);
			response = groupsSite.queryParam("groupname", newGroupName).request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.header("AccessToken", PropertiesUtils.getAccessToken()).get();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			groupsByCategory = mapper.readValue(response.readEntity(String.class), Groups.class);

			if((groupsByCategory != null) && (groupsByCategory.getProfilegroups() != null)) {
				for (Group group : groupsByCategory.getProfilegroups()) {
					groups.put(group.getGroupname(), String.valueOf(group.getGroupid()));
				}
			}
		} catch (IOException ioe) {
			log.fatal(ioe.getMessage(), ioe);
		} finally {
			if(response != null)
				response.close();
		}
		return groups;
	}

	/**
	 * This method returns Credentials of an employee identified by WPB Employee
	 * Number
	 * 
	 * @param empNo
	 * @return {@link Credentials} credentials
	 * @throws IOException
	 */
	public Credentials getEmployeeCredentials(String empNo) throws IOException {
		String userid = new GetEmployee().getEmployeeByEmpNo(empNo).getUserid();
		Credentials credentials = new Credentials();
		WebTarget userCredentialsSite = getUserCredentialsSite(userid);
		Response response = userCredentialsSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken()).get();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		try {
			credentials = mapper.readValue(response.readEntity(String.class), Credentials.class);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		}

		log.debug("getEmployeeCredentials:: Credentials returned for empNo:" empNo + " are: " + credentials.toString());
		// System.out.println(credentials.getCredentials().get(0));
		if(response != null)
			response.close();
		return credentials;

	}

	/**
	 * This method returns Employee groups a.k.a columns from employee record from LMS
	 * @param empNo
	 * @return Groups
	 * @throws IOException
	 */
	public Groups getEmployeeGroups(String empNo) throws IOException {
		String userid = new GetEmployee().getEmployeeByEmpNo(empNo).getUserid();
		Groups groups = new Groups();
		WebTarget userGroupsSite = getUserGroupsSite(userid);
		Response response = userGroupsSite.request(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
				.header("AccessToken", PropertiesUtils.getAccessToken()).get();

		ObjectMapper mapper = new ObjectMapper();

		try {
			groups = mapper.readValue(response.readEntity(String.class), Groups.class);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		}

		log.debug("GetEmployeeGroups:: Groups returned for empNo:" empNo + " are: " + groups.toString());
		// System.out.println(groups.getGroups().size() > 0 ?
		// groups.getGroups().get(0) : null);

		if(response != null)
			response.close();
		return groups;
	}

}