package com.netsparker.teamcity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public abstract class ScanRequestBase{
	//API settings property names start with "netsparkerCloud" to be unique in Teamcity environment.
	public static final String API_URL_Literal = "netsparkerCloudServerURL";
	public static final String API_TOKEN_Literal = "netsparkerCloudApiToken";
	public static final String API_ENCRYPTED_TOKEN_Literal = "netsparkerCloudEncryptedApiToken";
	
	public ScanRequestBase(Map<String, String> parameters) throws MalformedURLException {
		Parameters = parameters;
		if (parameters.get(API_URL_Literal) != null) {
			ApiURL = AppCommon.getBaseURL(parameters.get(API_URL_Literal));
			ApiToken = parameters.get(API_TOKEN_Literal);
			ApiEncryptedToken = parameters.get(API_ENCRYPTED_TOKEN_Literal);
		} else {
			ApiURL = null;
			ApiToken = null;
			ApiEncryptedToken = null;
		}
	}
	
	public ScanRequestBase() {
		Parameters = null;
		ApiURL = null;
		ApiToken = null;
		ApiEncryptedToken = null;
	}
	
	public final Map<String, String> Parameters;
	public final URL ApiURL;
	public final String ApiToken;
	public final String ApiEncryptedToken;
}
