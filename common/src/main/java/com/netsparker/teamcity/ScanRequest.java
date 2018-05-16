package com.netsparker.teamcity;

import jetbrains.buildServer.serverSide.crypt.RSACipher;
import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class ScanRequest extends ScanRequestBase{
	//API settings property names start with "netsparkerCloud" to be unique in Teamcity environment.
	public static final String SCAN_TYPE_Literal = "netsparkerCloudScanType";
	public static final String WEBSITE_ID_Literal = "netsparkerCloudWebsiteID";
	public static final String PROFILE_ID_Literal = "netsparkerCloudProfileID";
	
	private final String json = "application/json";
	
	public ScanRequest(@NotNull Map<String, String> scanParameters) throws MalformedURLException, NullPointerException, URISyntaxException {
		super(scanParameters);
		scanUri = getRequestEndpoint();
		testUri = getTestEndpoint();
		scanType = ScanType.valueOf(scanParameters.get(SCAN_TYPE_Literal));
		websiteId = scanParameters.get(WEBSITE_ID_Literal);
		profileId = scanParameters.get(PROFILE_ID_Literal);
		vcsCommit = new VCSCommit(scanParameters);
	}
	
	public final URI scanUri;
	public final URI testUri;
	public final ScanType scanType;
	public final String websiteId;
	public final String profileId;
	public final VCSCommit vcsCommit;
	
	public HttpResponse scanRequest() throws IOException, URISyntaxException {
		HttpForm client = getHttpClient();
		HttpResponse response = client.doPost();
		
		return response;
	}
	
	public HttpResponse testRequest() throws IOException, URISyntaxException {
		HttpForm client = getHttpTestClient();
		HttpResponse response = client.doPost();
		
		if (response.getCode() == 401) {
			try {
				client = getHttpTestClientWithDecryptedToken();
				response = client.doPost();
			} catch (Exception ex) {
			
			}
		}
		
		return response;
	}
	
	@NotNull
	private URI getRequestEndpoint() throws MalformedURLException, URISyntaxException {
		String relativePath = "api/1.0/scans/CreateFromPluginScanRequest";
		return new URL(ApiURL, relativePath).toURI();
	}
	
	@NotNull
	private URI getTestEndpoint() throws MalformedURLException, URISyntaxException {
		String relativePath = "api/1.0/scans/VerifyPluginScanRequest";
		return new URL(ApiURL, relativePath).toURI();
	}
	
	private HttpForm getHttpTestClient() throws MalformedURLException, URISyntaxException {
		HttpForm client = new HttpForm(testUri);
		//default is XML
		client.setAcceptedType(json);
		// Basic Authentication
		client.setCredentials("", ApiToken);
		setScanParams(client);
		
		return client;
	}
	
	private HttpForm getHttpTestClientWithDecryptedToken() throws MalformedURLException, URISyntaxException {
		HttpForm client = new HttpForm(testUri);
		//default is XML
		client.setAcceptedType(json);
		// Basic Authentication
		client.setCredentials("", RSACipher.decryptWebRequestData(ApiEncryptedToken));
		setScanParams(client);
		
		return client;
	}
	
	private HttpForm getHttpClient() throws MalformedURLException, URISyntaxException {
		HttpForm client = new HttpForm(scanUri);
		//default is XML
		client.setAcceptedType(json);
		// Basic Authentication
		client.setCredentials("", ApiToken);
		setScanParams(client);
		vcsCommit.addVcsCommitInfo(client);
		
		return client;
	}
	
	private void setScanParams(HttpForm client) {
		switch (scanType) {
			case Incremental:
				client.putFieldValue("WebsiteId", websiteId);
				client.putFieldValue("ProfileId", profileId);
				client.putFieldValue("ScanType", "Incremental");
				break;
			case FullWithPrimaryProfile:
				client.putFieldValue("WebsiteId", websiteId);
				client.putFieldValue("ScanType", "FullWithPrimaryProfile");
				break;
			case FullWithSelectedProfile:
				client.putFieldValue("WebsiteId", websiteId);
				client.putFieldValue("ProfileId", profileId);
				client.putFieldValue("ScanType", "FullWithSelectedProfile");
				break;
		}
	}
}
