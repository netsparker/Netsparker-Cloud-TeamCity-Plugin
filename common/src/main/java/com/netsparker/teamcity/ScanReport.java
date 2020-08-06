package com.netsparker.teamcity;

import java.net.HttpURLConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ScanReport {
    private final HttpResponse reportRequestResponse;
    private final boolean scanRequestHasError;
    private final String scanRequestErrorMessage;
    private final boolean reportRequestHasError;
    private final String reportRequestErrorMessage;
    private final String requestURI;
    private final int statusCode;

    public ScanReport(HttpResponse reportRequestResponse, String requestURI) {
        this.reportRequestResponse = reportRequestResponse;
        this.scanRequestHasError = false;
        this.scanRequestErrorMessage = "";
        this.statusCode = reportRequestResponse.getStatusLine().getStatusCode();

        if (this.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            this.reportRequestHasError = true;
            this.reportRequestErrorMessage = "User is not authenticated.";
        } else {
            this.reportRequestHasError = false;
            this.reportRequestErrorMessage = "";
        }
        this.requestURI = requestURI;
    }

    public ScanReport(boolean scanRequestHasError, String scanRequestErrorMessage,
            boolean reportRequestHasError, String reportRequestErrorMessage, String requestURI) {
        this.reportRequestResponse = null;
        this.scanRequestHasError = scanRequestHasError;
        this.scanRequestErrorMessage = scanRequestErrorMessage;
        this.reportRequestHasError = reportRequestHasError;
        this.reportRequestErrorMessage = reportRequestErrorMessage;
        this.requestURI = requestURI;
        this.statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
    }

    public boolean isReportGenerated() {
        // when report stored, it will be loaded from disk for later requests. There is an exception
        // potential.
        try {
            return getContentType().equalsIgnoreCase("text/html");
        } catch (Exception ex) {
            return false;
        }
    }

    private String getContentType() {
        return reportRequestResponse.getHeaders("Content-Type")[0].getValue();
    }

    public String getContent() {
        String content = "";
        try {
            if (scanRequestHasError) {
                content = ExceptionContent(content, scanRequestErrorMessage);
            } else if (reportRequestHasError) {
                content = ExceptionContent(content, reportRequestErrorMessage);
            } else {
                String contentData = AppCommon.ParseResponseToString(reportRequestResponse);
                if (isReportGenerated()) {
                    content = contentData;
                } else {
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(contentData);
                    content = (String) obj.get("Message");
                }
            }
        } catch (ParseException ex) {
            content = ExceptionContent("Report result is not parsable.", ex.toString());
        } catch (Exception ex) {
            content = ExceptionContent(content, ex.toString());
        }

        return content;
    }

    private String ExceptionContent(String content, String ExceptionMessage) {
        if (!StringUtils.isBlank(content)) {
            content = "<p>" + content + "</p>";
        }
        if (requestURI != null) {
            content = content + "<p>Request URL: " + requestURI + "</p>";
        }
        if (reportRequestResponse != null && reportRequestResponse.getStatusLine() != null) {
            content = content + "<p>HttpStatusCode: "
                    + reportRequestResponse.getStatusLine().getStatusCode() + "</p>";
        }
        if (ExceptionMessage != null) {
            content = content + "<p>Exception Message: " + ExceptionMessage + "</p>";
        }

        return content;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
