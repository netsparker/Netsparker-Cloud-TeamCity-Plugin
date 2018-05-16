package com.netsparker.teamcity;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.http.HttpUtil;
import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class ScanRequestResult extends ScanRequestBase{
	public static final String IsError_Literal = "IsError";
	public static final String HTTPStatusCode_Literal = "HTTPStatusCode";
	public static final String Message_Literal = "Message";
	public static final String Data_Literal = "Data";
	public static final String ScanTaskID_Literal = "ScanTaskID";
	public static final String BuildID_Literal = "BuildID";
	
	private final URL serverURL;
	private final URI scanDataControllerURI;
	//Response from Netsparker Cloud API
	private final boolean isInServer;
	private final String teamCityUserID;
	private final String teamCityPassword;
	private final long buildID;
	private final int httpStatusCode;
	private final String data;
	
	private String scanTaskID;
	private boolean isError;
	private String errorMessage;
	
	//called from agent with scan request response
	public ScanRequestResult(HttpResponse response, String serverURL, long buildID, String teamCityUserID, String teamCityPassword) throws RunBuildException, MalformedURLException, URISyntaxException {
		super();
		isInServer = false;
		this.serverURL = new URL(serverURL);
		this.teamCityUserID = teamCityUserID;
		this.teamCityPassword = teamCityPassword;
		scanDataControllerURI = new URL(this.serverURL, ScanConstants.SCAN_DATA_CONTROLLER_RELATIVE_URL).toURI();
		
		isError = response.hasError();
		httpStatusCode = response.getCode();
		data = response.getData();
		
		try {
			isError = !(boolean) AppCommon.ParseJsonValue(data, "IsValid");
			if (!isError) {
				scanTaskID = (String) AppCommon.ParseJsonValue(data, "ScanTaskId");
			} else {
				errorMessage = (String) AppCommon.ParseJsonValue(data, "ErrorMessage");
			}
		} catch (Exception ex) {
			isError = true;
			errorMessage = "Scan request result is not parsable.";
		}
		
		this.buildID = buildID;
	}
	
	//Called from server with request params
	public ScanRequestResult(Map<String, String> parameters) throws MalformedURLException {
		super(parameters);
		isInServer = true;
		serverURL = null;
		teamCityUserID = null;
		teamCityPassword = null;
		scanDataControllerURI = null;
		
		scanTaskID = parameters.get(ScanTaskID_Literal);
		buildID = Long.parseLong(parameters.get(BuildID_Literal));
		data = parameters.get(Data_Literal);
		isError = Boolean.parseBoolean(parameters.get(IsError_Literal));
		httpStatusCode = Integer.parseInt(parameters.get(HTTPStatusCode_Literal));
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public long getBuildID() {
		return buildID;
	}
	
	public String getScanTaskID() {
		return scanTaskID;
	}
	
	public int SendToServer() throws IOException {
		if (isInServer) {
			//do nothing you are already in the server
			return 0;
		} else {
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(teamCityUserID, teamCityPassword);
			//UserAgent:Jakarta Commons-HttpClient/3.1
			//Content-Type: application/x-www-form-urlencoded
			HttpClient httpClient = HttpUtil.createHttpClient(
					60, scanDataControllerURI.toURL(), credentials, true);
			
			PostMethod postMethod = new PostMethod(scanDataControllerURI.toString());
			NameValuePair[] nameValuePairs = new NameValuePair[5];
			nameValuePairs[0] = new NameValuePair(ScanTaskID_Literal, scanTaskID);
			nameValuePairs[1] = new NameValuePair(BuildID_Literal, String.valueOf(buildID));
			nameValuePairs[2] = new NameValuePair(Data_Literal, data);
			nameValuePairs[3] = new NameValuePair(IsError_Literal, String.valueOf(isError));
			nameValuePairs[4] = new NameValuePair(HTTPStatusCode_Literal, String.valueOf(httpStatusCode));
			
			postMethod.setRequestBody(nameValuePairs);
			
			return httpClient.executeMethod(postMethod);
		}
	}
	
	public ScanReport getReport(String apiURL, String apiToken) throws IOException, URISyntaxException {
		ReportType reportType = ReportType.ExecutiveSummary;
		String reportFormatCode = "3";
		
		String reportEndPoint_RelativeURL = "api/1.0/scans/report/%s";
		HttpForm client = new HttpForm(
				new URL(
						AppCommon.getBaseURL(apiURL),
						String.format(reportEndPoint_RelativeURL, scanTaskID)
				).toURI());
		
		//default is XML
		client.setAcceptedType("text/html");
		// Basic Authentication
		client.setCredentials("", apiToken);
		
		client.putFieldValue("Type", reportType.getNumberAsString());
		client.putFieldValue("Format", reportFormatCode);
		
		HttpResponse response = client.doGet();
		
		return new ScanReport(response);
	}
}
