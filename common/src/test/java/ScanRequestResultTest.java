import com.netsparker.teamcity.ScanReport;
import com.netsparker.teamcity.ScanRequestResult;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ScanRequestResultTest{
	
	@Test
	public void ScanRequestResultReportTestRequest() throws IOException, URISyntaxException, ParseException {
		Map<String, String> parameters = new HashMap<>();
		AddAPIParameters(parameters);
		
		ScanRequestResult scanRequest = new ScanRequestResult(parameters);
		Map<String, String> parameters1=scanRequest.Parameters;
		ScanReport report = scanRequest.getReport("http://localhost:2097/","8a712f61e2584220019ca7ec0398725b");
		String contentType=report.getContentType();
		String content=report.getContent();
		boolean isReportGenerated=report.isReportGenerated();
		
		Assert.assertNotNull(report);
	}
	
	
	
	private void AddAPIParameters(Map<String, String> parameters) {
		parameters.put(ScanRequestResult.API_URL_Literal, "http://localhost:2097/");
		parameters.put(ScanRequestResult.API_TOKEN_Literal, "8a712f61e2584220019ca7ec0398725b");
		
		parameters.put(ScanRequestResult.ScanTaskID_Literal, "ca671196-6bcd-444f-0505-a82b03be71bc");
		parameters.put(ScanRequestResult.BuildID_Literal, "64");
		parameters.put(ScanRequestResult.Message_Literal, "message");
		parameters.put(ScanRequestResult.Data_Literal, "data");
		parameters.put(ScanRequestResult.IsError_Literal, "false");
		parameters.put(ScanRequestResult.HTTPStatusCode_Literal, "201");
	}
}
