package fantasy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CopyPasteRequest;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.FindReplaceRequest;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
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

	private static final String MASTER_BIDDING_SPREADSHEET_ID = "1W4O_EKpYnY1Oh_zJOL_MGNPy6NCSdE9XIL_uRl1sjO0";

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
		//findAndReplaceBiddedPlayerInCurrentTeams();
		//appendBiddingResultsToAllSucessfulBids
		//copyBiddingDataFromMasterToAnotherSheets();
		processBidsAndUpdateSheets();
		
	}

static void findAndReplaceBiddedPlayerInCurrentTeams() throws IOException
{
	
	GridRange  range=new GridRange();
	
	//currentTeams
	range.setSheetId(1003449757);
	range.setStartColumnIndex(0);
	range.setEndColumnIndex(20);
	range.setEndRowIndex(20);
	range.setStartRowIndex(0);
	
	List<Request> requests = new ArrayList<>(); // TODO: Update placeholder value.

	String sourceSheetRange = "TodaysSucessfulBids!B2:D";
	Sheets service = getSheetsService();
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
					Request request=new Request();
					request.setFindReplace(findReplaceRequest);
					requests.add(request);
					
					System.out.printf("%s, %s\n", row.get(1), row.get(2));
				}
	}
	
	
	
	
	BatchUpdateSpreadsheetResponse response = findReplaceInSheeets(service, requests);

    System.out.println(response);
    
			
				 
	 }

private static BatchUpdateSpreadsheetResponse findReplaceInSheeets(Sheets service, List<Request> requests)
		throws IOException {
	BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
	
	requestBody.setRequests(requests);
	    
	Sheets.Spreadsheets.BatchUpdate request =service.spreadsheets().batchUpdate(masterSpreadsheetId, requestBody);
	
	BatchUpdateSpreadsheetResponse response = request.execute();
	return response;
}

static void appendBiddingResultsToAllSucessfulBids() throws IOException
{
	Sheets service = getSheetsService();
	String sourceSheetRange = "TodaysSucessfulBids!A2:E1";


	ValueRange sourceSheet = service.spreadsheets().values().get(masterSpreadsheetId, sourceSheetRange).execute();
	
	String range = "AllSucessfulBids!A2:F100";
	
	Sheets.Spreadsheets.Values.Append request =service.spreadsheets().values().append(masterSpreadsheetId, range, sourceSheet);
	   request.setValueInputOption("USER_ENTERED");
	    AppendValuesResponse response = request.execute();
	    System.out.println(response);
	
	

			
				 
	 }



static void copyBiddingDataFromMasterToAnotherSheets( ) throws IOException
{
	

	
	GridRange  sourceRange=new GridRange();
	//Master
	sourceRange.setSheetId(1310794273);
	sourceRange.setStartColumnIndex(0);
	sourceRange.setEndColumnIndex(20);
	sourceRange.setEndRowIndex(1000);
	sourceRange.setStartRowIndex(0);
	
	GridRange  destinationRange=new GridRange();
	//copOfBids
	destinationRange.setSheetId(1169253405);
	destinationRange.setStartColumnIndex(0);
	destinationRange.setEndColumnIndex(20);
	destinationRange.setEndRowIndex(1000);
	destinationRange.setStartRowIndex(0);
	
	List<Request> requests = copyFromOneSheetToAnother(sourceRange, destinationRange);

	batchUpdateOfGoogleSheets(MASTER_BIDDING_SPREADSHEET_ID, requests);
	
	
			
				 
	 }

private static List<Request> copyFromOneSheetToAnother(GridRange sourceRange, GridRange destinationRange) {
	CopyPasteRequest copyPasteRequest=new CopyPasteRequest();
	copyPasteRequest.setDestination(destinationRange);
	copyPasteRequest.setSource(sourceRange);
	copyPasteRequest.setPasteType("PASTE_VALUES");
	copyPasteRequest.setPasteOrientation("NORMAL");
	
	Request request=new Request();
	
	request.setCopyPaste(copyPasteRequest);
	
	List<Request> requests=new ArrayList<Request>();
	requests.add(request);
	return requests;
}

static List<Bid> processBids() throws IOException
{
	Sheets service = getSheetsService();

	
	String sourceSheetRange = "copyofBids!A2:F";
	ValueRange sourceSheet = service.spreadsheets().values().get(MASTER_BIDDING_SPREADSHEET_ID, sourceSheetRange).execute();
	List<List<Object>> sourceValues = sourceSheet.getValues();
			
	
	List<Bid> bidsList=new ArrayList<Bid>();
	
	
	if (sourceValues == null || sourceValues.size() == 0) 
	{
		System.out.println("No sucessful bids found for today.");
			
	} else 
	{
				for (List<?> row : sourceValues) {
					
					Bid bid=new Bid();
					bid.setOwnerName(row.get(0).toString());
					bid.setTeamName(row.get(1).toString());
					bid.setBiddedPlayer(row.get(2).toString());
					bid.setDroppedPlayer(row.get(3).toString());
					bid.setAmount(new Integer(row.get(4).toString()));
					bidsList.add(bid);
					System.out.println(bid.toString()+ "\n");
				}
				
				System.out.println("Sorted");
				Collections.sort(bidsList);
				for(Bid bid:bidsList)
				{
					System.out.println(bid.toString()+ "\n");
					
				}
				
				List<Bid> removeBids=new ArrayList<Bid>();
				
				for(int i=0;i<bidsList.size();i++)
				{
					Bid bid=bidsList.get(i);
					for(int j=i+1;j<bidsList.size();j++)
					{	
						Bid bidTemp=bidsList.get(j);
						if(bidTemp.getBiddedPlayer().equals(bid.getBiddedPlayer()) || bidTemp.getDroppedPlayer().equals(bid.getDroppedPlayer()))
						{
							removeBids.add(bidTemp);
						}
						
					}
				}
				bidsList.removeAll(removeBids);
				
				System.out.println("Bid Won");
				for(Bid bid:bidsList)
				{
					System.out.println(bid.toString()+ "\n");
					
				}
				
	 }
	return bidsList;
}


static void updateSheet( String spreadsheetId, UpdateCellsRequest updateSheetRequest) throws IOException
{
	
	
	Request request=new Request();
	request.setUpdateCells(updateSheetRequest);
	List<Request> requests=new ArrayList<Request>();
	requests.add(request);

	batchUpdateOfGoogleSheets(spreadsheetId, requests);
	
}



static void appendSheet( String spreadsheetId, AppendCellsRequest updateSheetRequest) throws IOException
{
	
	
	Request request=new Request();
	request.setAppendCells(updateSheetRequest);
	List<Request> requests=new ArrayList<Request>();
	requests.add(request);

	batchUpdateOfGoogleSheets(spreadsheetId, requests);
	
}

private static void batchUpdateOfGoogleSheets(String spreadsheetId, List<Request> requests) throws IOException {
	BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();

	requestBody.setRequests(requests);
	
	Sheets service = getSheetsService();

	Sheets.Spreadsheets.BatchUpdate requestBatch =service.spreadsheets().batchUpdate(spreadsheetId, requestBody);

	BatchUpdateSpreadsheetResponse response = requestBatch.execute();
	
    System.out.println(response);
}

static void processBidsAndUpdateSheets() throws IOException
{
	List<Bid> bids=processBids();
	
	GridRange range=new GridRange();
	//TodaysSucessfulBids sheet
	range.setSheetId(619002502);
	UpdateCellsRequest clearSheetRequest = clearSheet(range);
	updateSheet(masterSpreadsheetId,  clearSheetRequest);
	
	UpdateCellsRequest updateSheetRequest=new UpdateCellsRequest();
	updateSheetRequest.setFields("userEnteredValue");
	List<RowData> rows=new ArrayList<RowData>();
	
	List<CellData> values=new ArrayList<CellData>();

	values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Date")));
	values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Owner")));
	values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Player Picked")));
	values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Player Dropped")));
	values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Amount")));
	
	RowData row=new RowData();
	row.setValues(values);
	rows.add(row);
	
	
	for(Bid bid: bids)
	{
		 row=new RowData();
		CellData cell1=new CellData();
		cell1.setUserEnteredValue(new ExtendedValue().setStringValue( new SimpleDateFormat("MM/dd/yyyy").format(new Date())));
		
		CellData cell2=new CellData();
		cell2.setUserEnteredValue(new ExtendedValue().setStringValue(bid.getOwnerName()));
		
		CellData cell3=new CellData();
		cell3.setUserEnteredValue(new ExtendedValue().setStringValue(bid.getBiddedPlayer()));
		
		CellData cell4=new CellData();
		cell4.setUserEnteredValue(new ExtendedValue().setStringValue( bid.getDroppedPlayer()));
		
		CellData cell5=new CellData();
		cell5.setUserEnteredValue(new ExtendedValue().setNumberValue(new Double(bid.getAmount())));
		
		values=new ArrayList<CellData>();
		values.add(cell1);
		values.add(cell2);	
		values.add(cell3);	
		values.add(cell4);	
		values.add(cell5);	
		
		row.setValues(values);
		rows.add(row);		
	
	}
	
	
	updateSheetRequest.setRows(rows);
	range.setStartColumnIndex(0);
	range.setEndColumnIndex(20);
	range.setEndRowIndex(10000);
	range.setStartRowIndex(0);
	
	updateSheetRequest.setRange(range);
	
	GridCoordinate start=new GridCoordinate();
	start.setColumnIndex(0);
	start.setRowIndex(10);
	start.setSheetId(619002502);
	updateSheet(masterSpreadsheetId, updateSheetRequest);
	
	
}

private static UpdateCellsRequest clearSheet(GridRange range) {
	UpdateCellsRequest clearSheetRequest=new UpdateCellsRequest();
	clearSheetRequest.setRange(range);
	clearSheetRequest.setFields("userEnteredValue");
	return clearSheetRequest;
}


}

