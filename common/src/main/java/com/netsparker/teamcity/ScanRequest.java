package com.netsparker.teamcity;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScanRequest extends ApiRequestBase {
	// API settings property names start with "netsparkerEnterprise" to be unique in
	// Teamcity environment.
	public static final String SCAN_TYPE_Literal = "netsparkerEnterpriseScanType";
	public static final String WEBSITE_ID_Literal = "netsparkerEnterpriseWebsiteID";
	public static final String PROFILE_ID_Literal = "netsparkerEnterpriseProfileID";

	private final String json = "application/json";

	public ScanRequest(@NotNull Map<String, String> scanParameters)
			throws MalformedURLException, NullPointerException, URISyntaxException {
		super(scanParameters);
		scanUri = new URL(ApiURL, "api/1.0/scans/CreateFromPluginScanRequest").toURI();
		testUri = new URL(ApiURL, "api/1.0/scans/VerifyPluginScanRequest").toURI();
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

	public HttpResponse scanRequest() throws IOException {
		HttpClient client = getHttpClient();
		final HttpPost httpPost = new HttpPost(scanUri);
		httpPost.setHeader("Accept", json);
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

		List<NameValuePair> params = new ArrayList<>();
		setScanParams(params);
		vcsCommit.addVcsCommitInfo(params);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		return client.execute(httpPost);
	}

	public HttpResponse testRequest() throws IOException {
		HttpClient client = getHttpClient();
		final HttpPost httpPost = new HttpPost(testUri);
		httpPost.setHeader("Accept", json);
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

		List<NameValuePair> params = new ArrayList<>();
		setScanParams(params);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = client.execute(httpPost);

		return response;
	}

	private void setScanParams(List<NameValuePair> params) {
		switch (scanType) {
		case Incremental:
			params.add(new BasicNameValuePair("WebsiteId", websiteId));
			params.add(new BasicNameValuePair("ProfileId", profileId));
			params.add(new BasicNameValuePair("ScanType", "Incremental"));
			break;
		case FullWithPrimaryProfile:
			params.add(new BasicNameValuePair("WebsiteId", websiteId));
			params.add(new BasicNameValuePair("ScanType", "FullWithPrimaryProfile"));
			break;
		case FullWithSelectedProfile:
			params.add(new BasicNameValuePair("WebsiteId", websiteId));
			params.add(new BasicNameValuePair("ProfileId", profileId));
			params.add(new BasicNameValuePair("ScanType", "FullWithSelectedProfile"));
			break;
		}
	}
}
