package fantasy;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CopyPasteRequest;
import com.google.api.services.sheets.v4.model.CopySheetToAnotherSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.FindReplaceRequest;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.InsertRangeRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
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
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, GmailScopes.GMAIL_SEND);

	private static StringBuffer email = new StringBuffer("BIDS\n\n\n***********************\n\nALL BIDS\n\n*************************\n\nOwner Name	Team Name	Picked Player	Dropped player	Amount\n");
	
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
	
	public static void mainold(String[] args) throws IOException, MessagingException {
        // Build a new authorized API client service.
     }

	public static void main(String[] args) throws IOException, MessagingException {
		
		GridRange  availablePlayerRange=new GridRange();
		
		//availablePlayers
		availablePlayerRange.setSheetId(2027324762);
		availablePlayerRange.setStartColumnIndex(0);
		availablePlayerRange.setEndColumnIndex(20);
		availablePlayerRange.setEndRowIndex(40);
		availablePlayerRange.setStartRowIndex(0);
		
		GridRange  range=new GridRange();
		
		//currentTeams
		range.setSheetId(1003449757);
		range.setStartColumnIndex(0);
		range.setEndColumnIndex(20);
		range.setEndRowIndex(20);
		range.setStartRowIndex(0);
		

		lockThePlayers(range);
		
		String sheetName=createNewSheet();
		
		backUpSheet("1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA", 1003449757, sheetName);
		backUpSheet("1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA", 2027324762, sheetName);
		backUpSheet("1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA", 1168678591, sheetName);
		backUpSheet("1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA", 195609735, sheetName);
		
	//	copyBiddingDataFromMasterToAnotherSheets();
		if(processBidsAndUpdateSheets())
		{
			findAndReplaceBiddedPlayerInCurrentTeams(availablePlayerRange);
			if(args[0]=="1")
			{
				clearAllSpreadSheets();
			}
		}
	     Gmail service = getGmailService();

	        
	        
	        // Print the labels in the user's account.
	        String user = "ddfantasyleague@gmail.com";
	      
	        sendMessage(service,user, createEmail("geothomas316@yahoo.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("dyerradla@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("sachin_gurung@yahoo.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("balashekarreddy@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("nabhilash1991@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("mamidala86@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("udayshankarc@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("mvalluru@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("bala.peddy@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("ugopi15@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("deepak.bommaraju@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("luxmidutt@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("sachyderm@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
	        sendMessage(service,user, createEmail("riteshruchi@gmail.com", "ddfantasyleague@gmail.com", "Bids", email.toString()));
		  	   	  
	}

private static void clearAllSpreadSheets() throws IOException {

	GridRange range=new GridRange();
	//master sheet
	range.setSheetId(1310794273);
	range.setStartRowIndex(1);
	range.setEndColumnIndex(4);
	UpdateCellsRequest clearSheetRequest = clearSheet(range);
	//updateSheet(MASTER_BIDDING_SPREADSHEET_ID,  clearSheetRequest);

	//copyOfBids sheet
	range.setSheetId(1169253405);
	//clearSheetRequest = clearSheet(range);
	//updateSheet(MASTER_BIDDING_SPREADSHEET_ID,  clearSheetRequest);
	
	//GeorgeAbhi
	range.setSheetId(1367377156);
	range.setStartRowIndex(1);
	clearSheetRequest = clearSheet(range);
	updateSheet("1OZBqDisssAuIZH5kI6CGfqDUo11eDh2hAzint6cwIGY",  clearSheetRequest);
	
	//Durga
	clearSheetRequest = clearSheet(range);
	updateSheet("1nKrdy5HfMAGxBOG-h04eE0Afm4J0g4Y6sOhV2Jsq0NA",  clearSheetRequest);
	
	//Abhi
	clearSheetRequest = clearSheet(range);
	updateSheet("1WgdjnfpHYEP0_iGcR6LrehI7JancodLJLrkKln7eWfs",  clearSheetRequest);
	
	
	//luxmi
	clearSheetRequest = clearSheet(range);
	updateSheet("13Y6YSSnI5hOq1w2SKMESpUYWAh6kFgKkfxE3D50NCw0",  clearSheetRequest);
	
	//Srikanth
	clearSheetRequest = clearSheet(range);
	updateSheet("1Nygx2rcDGquWqen5QkSITTVNk0mtg72KBASMhQi4sGA",  clearSheetRequest);
	
	//mani
	clearSheetRequest = clearSheet(range);
	updateSheet("1U__FeOwZeZMuyzT8lFOu1RCLxUbdp_bqRtXqLSWNcOk",  clearSheetRequest);
	
	//Deepak
	clearSheetRequest = clearSheet(range);
	updateSheet("1o7B-6F39RXZ95bUXEPUvcMDIPnRjWVgNINTO2tL5n8k",  clearSheetRequest);
	
	
	
	
	
	
	
	}

static void findAndReplaceBiddedPlayerInCurrentTeams(GridRange availablePlayerRange) throws IOException
{
	
	GridRange  range=new GridRange();
	
	//LockedcurrentTeams
	range.setSheetId(1003449757);
	range.setStartColumnIndex(0);
	range.setEndColumnIndex(20);
	range.setEndRowIndex(20);
	range.setStartRowIndex(0);
	
	//UnLockedcurrentTeams
	GridRange  rangeUnlocked=new GridRange();
	rangeUnlocked.setSheetId(668027620);
	rangeUnlocked.setStartColumnIndex(0);
	rangeUnlocked.setEndColumnIndex(20);
	rangeUnlocked.setEndRowIndex(20);
	rangeUnlocked.setStartRowIndex(0);
		
		
	/*
	GridRange  availablePlayerRange=new GridRange();
	
	//availablePlayers
	availablePlayerRange.setSheetId(2027324762);
	availablePlayerRange.setStartColumnIndex(0);
	availablePlayerRange.setEndColumnIndex(20);
	availablePlayerRange.setEndRowIndex(20);
	availablePlayerRange.setStartRowIndex(0);
	
	*/
	List<Request> requests = new ArrayList<>(); // TODO: Update placeholder value.

	String sourceSheetRange = "TodaysSucessfulBids!B2:D";
	Sheets service = getSheetsService();
	ValueRange sourceSheet = service.spreadsheets().values().get(masterSpreadsheetId, sourceSheetRange).execute();
	List<List<Object>> sourceValues = sourceSheet.getValues();
			
	
	if (sourceValues == null || sourceValues.size() == 0) 
	{
		throw new IOException("No sucessful bids found for today.");
			
	} else 
	{
				for (List<?> row : sourceValues) {

					String from=row.get(2).toString();
					String to=row.get(1).toString();
					requests.add(getFindReplaceRequests(range, from, to));				

					requests.add(getFindReplaceRequests(rangeUnlocked, from, to));	
					
					requests.add(getFindReplaceRequests(availablePlayerRange, to,""));
					/*
					
					FindReplaceRequest  availablePlayer=new FindReplaceRequest();
					availablePlayer.setFind(row.get(1).toString());
					availablePlayer.setReplacement("");
					availablePlayer.setRange(availablePlayerRange);
					Request request1=new Request();
					request1.setFindReplace(availablePlayer);
					requests.add(request1);
					
					System.out.printf("%s, %s\n", row.get(1), row.get(2));
					*/
				}
	}
	
//	batchUpdateOfGoogleSheets(MASTER_BIDDING_SPREADSHEET_ID, requests);
	
	
	BatchUpdateSpreadsheetResponse response = findReplaceInSheeets( requests);

    System.out.println(response);
    
			
				 
	 }

private static Request getFindReplaceRequests(GridRange range, String from, String to) {
	FindReplaceRequest  findReplaceRequest=new FindReplaceRequest();
	findReplaceRequest.setFind(from);
	findReplaceRequest.setReplacement(to);
	findReplaceRequest.setRange(range);
	Request request=new Request();
	request.setFindReplace(findReplaceRequest);
	return request;
}

private static BatchUpdateSpreadsheetResponse findReplaceInSheeets( List<Request> requests)
		throws IOException {
	Sheets service = getSheetsService();
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

static List<Bid> getWonBids() throws IOException
{
	/*
	String sourceSheetRange = "copyofBids!A2:F";
	List<List<Object>> sourceValues = getDataFromSheet(sourceSheetRange, MASTER_BIDDING_SPREADSHEET_ID);
	
	*/
	String sourceSheetRange = "Bids!A2:F";
	List<List<Object>> sourceValues=new ArrayList<List<Object>>();
	
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1OZBqDisssAuIZH5kI6CGfqDUo11eDh2hAzint6cwIGY"));
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1nKrdy5HfMAGxBOG-h04eE0Afm4J0g4Y6sOhV2Jsq0NA"));
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1WgdjnfpHYEP0_iGcR6LrehI7JancodLJLrkKln7eWfs"));
	//luxmi
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "13Y6YSSnI5hOq1w2SKMESpUYWAh6kFgKkfxE3D50NCw0"));
	//sachin
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1nS2vZu7pbc_viv6q_vsv4I_reuFc9ngRTcDzcUbM3is"));
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1U__FeOwZeZMuyzT8lFOu1RCLxUbdp_bqRtXqLSWNcOk"));
	
	//Deepak
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1o7B-6F39RXZ95bUXEPUvcMDIPnRjWVgNINTO2tL5n8k"));
	//srikanth
	sourceValues.addAll( getDataFromSheet(sourceSheetRange, "1Nygx2rcDGquWqen5QkSITTVNk0mtg72KBASMhQi4sGA"));

	
	if ( sourceValues.size() == 0) 
	{
		throw new IOException("No bids found for today.");
			
	}
	List<Bid> bidsList = getListFromSheetValues(sourceValues);
	
	List<RowData> rowDatas=getRowDatas(bidsList);
	
		
	//append to all Bids
	appendSheet(rowDatas, 195609735,masterSpreadsheetId);
	
	
	removeLostBids(bidsList);
	
	
	return bidsList;
}

private static List<Bid> getListFromSheetValues(List<List<Object>> sourceValues) throws IOException{
	List<Bid> bidsList=new ArrayList<Bid>();
	
	String sourceSheetRange = "UnLockedCurrentTeams!K3:L10";
	List<List<Object>> rankValues = getDataFromSheet(sourceSheetRange, masterSpreadsheetId);
	Map<String, Integer> ranks=new HashMap<String, Integer>();
	
	for (List<?> row : rankValues)
	{
		ranks.put(row.get(1).toString(), new Integer(row.get(0).toString()));
	}	
	

				for (List<?> row : sourceValues)
				{
					if(row.size()==5)
					{
						Bid bid=new Bid();
						if(isValidAmount(row) )
						{
							bid.setAmount(new Integer(row.get(3).toString()));
						}
						else
						{
							continue;
						}
					
						
						if(row.get(4).toString()!=null)
						{
							bid.setOwnerName(row.get(4).toString());
						}
						if(row.get(0)!=null)
						{
							bid.setTeamName(row.get(0).toString());
						}
						
						
						if(row.get(1)!=null)
						{
							bid.setBiddedPlayer(row.get(1).toString());
						}
						else
						{
							continue;
						}
						
						if(row.get(2)!=null)
						{
							bid.setDroppedPlayer(row.get(2).toString());
						}
						else
						{
							continue;
						}
						
						
					
						bid.setRank(ranks.get(bid.getOwnerName()));
						bidsList.add(bid);
						
						email.append(bid+"\n");
					}
				}
	return bidsList;
}

private static boolean isValidAmount(List<?> row) 
{
	boolean validAmount=false;
	
	if( row.get(3)!=null && row.get(3).toString()!="")
	{
		try 
		{
			int amount=new Integer(row.get(3).toString());
			if( amount >=0)
				{
					validAmount=true;
				}
		}
		catch(NumberFormatException e)
		{
			
		}
		
	}
	return validAmount;
}

private static List<List<Object>> getDataFromSheet(String sourceSheetRange, String masterSpreadSheetId) throws IOException {
	Sheets service = getSheetsService();
	ValueRange sourceSheet = service.spreadsheets().values().get(masterSpreadSheetId, sourceSheetRange).execute();
	List<List<Object>> sourceValues= sourceSheet.getValues();
	if(sourceValues !=null)
	{
		return sourceValues;
	}
	else
	{
		return new ArrayList<List<Object>>();
	}
	 
}

private static void removeLostBids(List<Bid> bidsList) {
	Collections.sort(bidsList);
	/*for(Bid bid:bidsList)
	{
		System.out.println(bid.toString()+ "\n");
		
	}
	*/
	List<Bid> removeBids=new ArrayList<Bid>();
	
	Iterator<Bid> iterator=bidsList.iterator();
	int i=0;
	while(iterator.hasNext())
	//for(int i=0;i<bidsList.size();i++)
	{
		Bid bid=iterator.next();
		if(!removeBids.contains(bid))
		{
			for(int j=i+1;j<bidsList.size();j++)
			{	
				Bid bidTemp=bidsList.get(j);
				if(bidTemp.getBiddedPlayer().equals(bid.getBiddedPlayer()) || bidTemp.getDroppedPlayer().equals(bid.getDroppedPlayer()))
				{
					removeBids.add(bidTemp);
				}
				
			}
		}
		i++;
	}
	bidsList.removeAll(removeBids);
	
	System.out.println("Bid Won");
	
	email.append("\n\n\n BIDS WON\n\n******************************************\nOwner Name	Team Name	Picked Player	Dropped player	Amount");
	
	for(Bid bid:bidsList)
	{
		email.append(bid+"\n");
		
	}
	
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

static boolean processBidsAndUpdateSheets() throws IOException
{
	boolean processedBids=false;
	
	List<Bid> bids=getWonBids();
	//
	
	if(bids.size()!=0)
	{
		updateAvailablePlayers(bids);
		
		GridRange range=new GridRange();
		//TodaysSucessfulBids sheet
		range.setSheetId(619002502);
		UpdateCellsRequest clearSheetRequest = clearSheet(range);
		updateSheet(masterSpreadsheetId,  clearSheetRequest);
		
		UpdateCellsRequest updateSheetRequest=new UpdateCellsRequest();
		updateSheetRequest.setFields("userEnteredValue");
		List<RowData> rows=new ArrayList<RowData>();
		
		List<CellData> headingValues=new ArrayList<CellData>();
	
		headingValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Date")));
		headingValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Owner")));
		headingValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Player Picked")));
		headingValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Player Dropped")));
		headingValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Amount")));
		
		RowData row=new RowData();
		row.setValues(headingValues);
		rows.add(row);
		
		List<RowData> dataRows = getRowDatas(bids);
		rows.addAll(dataRows);
		
		
		updateSheetRequest.setRows(rows);
		range.setStartColumnIndex(0);
		range.setEndColumnIndex(20);
		range.setEndRowIndex(10000);
		range.setStartRowIndex(0);
		
		updateSheetRequest.setRange(range);
		
		//GridCoordinate start=new GridCoordinate();
		//start.setColumnIndex(0);
		//start.setRowIndex(10);
		//start.setSheetId(619002502);
		
		//todaysSuccessfulBids
		updateSheet(masterSpreadsheetId, updateSheetRequest);
		
		//AllSucessfulBids
		appendSheet(dataRows, 1168678591,masterSpreadsheetId);
	
		processedBids=true;
	}
	
	return processedBids;
	
	
}

private static void updateAvailablePlayers(List<Bid> bids) throws IOException {
	
	String sourceSheetRange="FullListPlayers!B1:I30";

	List<List<Object>> values = getDatafromSheets(sourceSheetRange);
	/*
	Map<String,Integer> playersByTeam=new HashMap<String, Integer>();
	
	Map<String,Integer> teamRanges=new HashMap<String, Integer>();
	teamRanges.put("BAN", 0);
	teamRanges.put("CHN", 1);
	teamRanges.put("DEL", 2);
	teamRanges.put("HYD", 3);
	teamRanges.put("KOL", 4);
	teamRanges.put("MUM", 5);
	teamRanges.put("RJS", 6);
	teamRanges.put("PNJ", 7);
*/
	Map<Integer,Set<String>> players=new HashMap<Integer,Set<String>>();
	
	//	List<RowData> rows=new ArrayList<RowData>();
	
		for(Bid bid: bids)
		{	
			
			for (List<?> row : values)
			{
				//List<CellData> updatedValues=new ArrayList<CellData>();
				for(int i=0;i<row.size();i++)
				{
					String player=row.get(i).toString();
				
					if(player!=null )
					{
						/*
						if(!bid.getBiddedPlayer().equals(player))
						{
							CellData cell=new CellData();
							cell.setUserEnteredValue(new ExtendedValue().setStringValue(player));
							updatedValues.add(cell);
						}*/
						
						if(bid.getDroppedPlayer().equals(player))
						{
							Set<String> droppedPlayers=players.get(i);
							if(droppedPlayers==null)
							{
								droppedPlayers=new HashSet<String>();
								
							}
							droppedPlayers.add(bid.getDroppedPlayer());
							
							players.put(i,droppedPlayers );
						}
						
					}
					
				}
				/*
				RowData dataRow=new RowData();
				dataRow.setValues(updatedValues);
				rows.add(dataRow);
				*/
			}
		}

		List<Request> requests =new ArrayList<Request>();
		for(Integer key: players.keySet())
		{
		/*
			GridRange range=new GridRange();
			range.setSheetId(2027324762);
			range.setStartColumnIndex(key);
			range.setStartRowIndex(2);
			range.setEndRowIndex(2+players.get(key).size());
			range.setEndColumnIndex(key);
			InsertRangeRequest insertRequest=new InsertRangeRequest();
			insertRequest.setRange(range);
			insertRequest.setShiftDimension("ROWS");
			Request request=new Request();
			request.setInsertRange(insertRequest);
			requests.add(request);
			*/
			UpdateCellsRequest updateCell=new UpdateCellsRequest();
			updateCell.setFields("userEnteredValue");
			GridRange range1=new GridRange();
			range1.setSheetId(2027324762);
			range1.setStartColumnIndex(key+1);
			range1.setStartRowIndex(27);
			
			updateCell.setRange(range1);
			RowData rowData=new RowData();
			List<RowData> list=new ArrayList<RowData>();
			
			for(String player:players.get(key))
			{
				
				
				CellData cellData=new CellData();
				cellData.setUserEnteredValue(new ExtendedValue().setStringValue(player));
				List<CellData> valuesCell=new ArrayList<CellData>();
				valuesCell.add(cellData);
				rowData.setValues(valuesCell);
				list.add(rowData);
			}
			
			updateCell.setRows(list);
			Request request1=new Request();
			request1.setUpdateCells(updateCell);
			requests.add(request1);
		}

	batchUpdateOfGoogleSheets("1zsZ3wjyrkJ-pxzNdbDR7gRHNslISPCgduvKL53KXmjA", requests);
	
	
}

private static List<List<Object>> getDatafromSheets( String sourceSheetRange) throws IOException {
	List<List<Object>> values = getDataFromSheet(sourceSheetRange, masterSpreadsheetId);
	return values;
}

private static void appendSheet(List<RowData> dataRows, int sheetId, String masterSpreadSheetId) throws IOException {
	AppendCellsRequest appendSheetRequest =new AppendCellsRequest();
	
	appendSheetRequest.setRows(dataRows);
	appendSheetRequest.setSheetId(sheetId);
	appendSheetRequest.setFields("userEnteredValue");
	
	appendSheet(masterSpreadSheetId, appendSheetRequest);
}

private static List<RowData> getRowDatas(List<Bid> bids) {
	RowData row;
	List<CellData> values;
	List<RowData> dataRows=new ArrayList<RowData>();
	
	
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
		dataRows.add(row);		
	
	}
	return dataRows;
}

private static UpdateCellsRequest clearSheet(GridRange range) {
	UpdateCellsRequest clearSheetRequest=new UpdateCellsRequest();
	clearSheetRequest.setRange(range);
	clearSheetRequest.setFields("userEnteredValue");
	return clearSheetRequest;
}

private static SheetProperties backUpSheet(String sourceSheetId, int sheetId, String destinationSpreadsheetId) throws IOException
{
	
CopySheetToAnotherSpreadsheetRequest requestBody = new CopySheetToAnotherSpreadsheetRequest();
requestBody.setDestinationSpreadsheetId(destinationSpreadsheetId);
Sheets service = getSheetsService();


Sheets.Spreadsheets.SheetsOperations.CopyTo request =
service.spreadsheets().sheets().copyTo(sourceSheetId, sheetId, requestBody);

SheetProperties response = request.execute();
return response;
}

private static String createNewSheet() throws IOException
{
	SpreadsheetProperties properties=new SpreadsheetProperties();
	properties.setTitle("IPL2018 - "+ new SimpleDateFormat("dd-MMM-yyyy hh:mm").format(new Date()));
	Spreadsheet requestBody = new Spreadsheet();
	 requestBody.setProperties(properties);

	    Sheets sheetsService =  getSheetsService();
	    Sheets.Spreadsheets.Create request = sheetsService.spreadsheets().create(requestBody);

	    Spreadsheet response = request.execute();
	    return response.getSpreadsheetId();

}

public static void lockThePlayers( GridRange availablePlayerRange)throws IOException
{
	String sourceSheetRange = "H2H Rounds/Cycle!A2:G57";
	List<List<Object>> games = getDatafromSheets(sourceSheetRange);
	
	String sourceSheetRange1="FullListPlayers!B2:I30";

	
	List<List<Object>> values = getDatafromSheets(sourceSheetRange1);
	
	Map<String,List<String>> playersByTeam=new HashMap<String, List<String>>();
	
	Map<Integer, String> teamRanges=new HashMap<Integer, String>();
	teamRanges.put(0,"BNG");
	teamRanges.put(1,"CHN");
	teamRanges.put(2,"DEL");
	teamRanges.put(3,"HYD");
	teamRanges.put(4,"KOL");
	teamRanges.put(5,"MUM");
	teamRanges.put(6,"RJS");
	teamRanges.put(7,"PNJ");
	
	for (List<?> row : values)
	{
		for(int i=0;i<row.size();i++)
		{
			System.out.println("players matched "+ row.get(i).toString() +" "+teamRanges.get(i).toString());
			
				if(row.get(i).toString()!=null && !row.get(i).toString().equals(""))
				{
					List<String> players=playersByTeam.get(teamRanges.get(i));
					if(players==null)
					{
						players=new ArrayList<String>();
						
					}
					
					players.add(row.get(i).toString());
					System.out.println("players matched "+ row.get(i).toString() +" "+teamRanges.get(i).toString());
					
					playersByTeam.put(teamRanges.get(i), players);
				}
		}
			
		}
	
			
	SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
	
	
	Calendar c = Calendar.getInstance();
	c.setTime(new Date());
	c.add(Calendar.DATE, 1);  // number of days to add
	String dt = sdf.format(c.getTime());
	List<String> players=new ArrayList<String>();
	
	//System.out.println("date is "+dt);
	for (List<?> row : games)
	{
		//System.out.println("date is "+dt);
		if(row.get(0)!= null && row.get(0).toString().equals(dt))
		{
			System.out.println("date matched "+ row.get(0).toString() +""+row.get(5).toString()+"  "+row.get(6).toString());
			players.addAll(playersByTeam.get(row.get(5).toString()));
			
			System.out.println("is team there"+ playersByTeam.containsKey(row.get(6).toString()));
			players.addAll(playersByTeam.get(row.get(6).toString()));
		}
	}
	//System.out.println(players);
	
	List<Request> requests=new ArrayList<Request>();
	
 	for (String player : players) {
		requests.add(getFindReplaceRequests(availablePlayerRange,player,""));
	}

 	if(requests.size()!=0)
 	{
 		BatchUpdateSpreadsheetResponse response = findReplaceInSheeets( requests);
 		System.out.println(response);
 	}
}

public static Gmail getGmailService() throws IOException {
    Credential credential = authorize();
    return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
}

public static void sendMessage(Gmail service, String userId, MimeMessage email)
	      throws MessagingException, IOException {
	    Message message = createMessageWithEmail(email);
	    message = service.users().messages().send(userId, message).execute();

	    System.out.println("Message id: " + message.getId());
	    System.out.println(message.toPrettyString());
	  }

public static Message createMessageWithEmail(MimeMessage email)
	      throws MessagingException, IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    email.writeTo(baos);
	   // Address[] cc = new InternetAddress[] {InternetAddress.parse("geothomas316@yahoo.com")};
	  //  email.addRecipients(RecipientType.TO, InternetAddress.parse("geothomas316@yahoo.com"));

	    String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
	    Message message = new Message();
	    message.setRaw(encodedEmail);
	    return message;
	  }

public static MimeMessage createEmail(String to,
        String from,
        String subject,
        String bodyText)
throws MessagingException {
Properties props = new Properties();
Session session = Session.getDefaultInstance(props, null);

MimeMessage email = new MimeMessage(session);

email.setFrom(new InternetAddress(from));
email.addRecipient(javax.mail.Message.RecipientType.TO,
new InternetAddress(to));
email.setSubject(subject);
email.setText(bodyText);
return email;
}

}


