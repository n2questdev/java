package fantasy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.FindReplaceRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;

public class BidManager {
	/** Application name. */
	private static final String APPLICATION_NAME = "fantasy";

	static String masterSpreadsheetId = "1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA";
	
	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/sheets.googleapis.com-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		//InputStream in = BidManager.class.getResourceAsStream("/client_secret.json");
		InputStreamReader in = new InputStreamReader(new FileInputStream("C:\\fantasy\\java\\fantasy\\src\\main\\resources\\client_secret.json"));
	
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, in);

		
		
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * 
	 * @return an authorized Sheets API client service
	 * @throws IOException
	 */
	public static Sheets getSheetsService() throws IOException {
		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

	public static void main(String[] args) throws IOException {
		// Build a new authorized API client service.
		Sheets service = getSheetsService();

		// Prints the names and majors of students in a sample spreadsheet:
		// https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
		// https://docs.google.com/spreadsheets/d/1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA/edit#gid=0
			String range = "H2H Rounds/Cycle!A2:E";
		ValueRange response = service.spreadsheets().values().get(masterSpreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			System.out.println("No data found.");
		} else {
			System.out.println("Name, Major");
			for (List<?> row : values) {

				// Print columns A and E, which correspond to indices 0 and 4.
				System.out.printf("%s, %s\n", row.get(0), row.get(3));
			}
		}
		movePlayerToCurrentTeams();
	
	}

static void movePlayerToCurrentTeams() throws IOException
{
	Sheets service = getSheetsService();
	String sourceSheetRange = "TodaysSucessfulBids!B2:D";

	GridRange  range=new GridRange();
	range.setSheetId(3);
	range.setStartColumnIndex(0);
	range.setEndColumnIndex(20);
	range.setEndRowIndex(20);
	range.setStartRowIndex(0);
	
	List<Request> requests = new ArrayList<>(); // TODO: Update placeholder value.
	

	ValueRange sourceSheet = service.spreadsheets().values().get(masterSpreadsheetId, sourceSheetRange).execute();
	List<List<Object>> sourceValues = sourceSheet.getValues();
			
	
	if (sourceValues == null || sourceValues.size() == 0) 
	{
		System.out.println("No sucessful bids found for today.");
			
	} else 
	{
				for (List<?> row : sourceValues) {

					FindReplaceRequest  findReplaceRequest=new FindReplaceRequest();
					findReplaceRequest.setFind(row.get(2).toString());
					findReplaceRequest.setReplacement(row.get(1).toString());
					
					findReplaceRequest.setRange(range);
				//	findReplaceRequest.setSheetId(3);
					Request request=new Request();
					request.setFindReplace(findReplaceRequest);
					requests.add(request);
					
					System.out.printf("%s, %s\n", row.get(1), row.get(2));
				}
	}
	
	
	
	
	BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
	
	requestBody.setRequests(requests);
	    
	Sheets.Spreadsheets.BatchUpdate request =service.spreadsheets().batchUpdate(masterSpreadsheetId, requestBody);
	
	BatchUpdateSpreadsheetResponse response = request.execute();

    // TODO: Change code below to process the `response` object:
    System.out.println(response);
    
			
				 
	 }
}