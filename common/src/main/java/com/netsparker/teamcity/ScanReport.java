package com.netsparker.teamcity;

import net.sf.corn.httpclient.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ScanReport{
	private final HttpResponse reportRequestResponse;

	
	public ScanReport(HttpResponse reportRequestResponse) {
		this.reportRequestResponse = reportRequestResponse;
	}
	
	public boolean isReportGenerated(){
		return getContentType().equalsIgnoreCase("text/html");
	}
	
	public String getContentType(){
		return reportRequestResponse.getHeaderFields().get("Content-Type").get(0);
	}
	
	public String getContent() throws ParseException {
		String content;
		String contentData=reportRequestResponse.getData();
		if(isReportGenerated()){
			content=contentData;
		}else {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(contentData);
			content = (String) obj.get("Message");
		}
		return content;
	}
}
