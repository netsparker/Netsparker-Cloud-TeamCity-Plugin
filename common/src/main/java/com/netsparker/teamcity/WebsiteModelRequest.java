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

public class WebsiteModelRequest extends ScanRequestBase{
	
	public WebsiteModelRequest(@NotNull Map<String, String> scanParameters) throws MalformedURLException, NullPointerException, URISyntaxException {
		super(scanParameters);
		pluginWebSiteModelsUri = getPluginWebSiteModelsEndpoint();
	}
	
	public final URI pluginWebSiteModelsUri;
	
	public HttpResponse getPluginWebSiteModels() throws IOException, URISyntaxException {
		HttpForm client = new HttpForm(pluginWebSiteModelsUri);
		// Basic Authentication
		client.setCredentials("", ApiToken);
		HttpResponse response = client.doGet();
		
		if (response.getCode() == 401) {
			try {
				String decryptedToken = RSACipher.decryptWebRequestData(ApiEncryptedToken);
				client = new HttpForm(pluginWebSiteModelsUri);
				// Basic Authentication
				client.setCredentials("", decryptedToken);
				response = client.doGet();
			} catch (Exception ex) {
			}
		}
		
		return response;
	}
	
	@NotNull
	private URI getPluginWebSiteModelsEndpoint() throws MalformedURLException, URISyntaxException {
		String relativePath = "api/1.0/scans/PluginWebSiteModels";
		return new URL(ApiURL, relativePath).toURI();
	}
}
