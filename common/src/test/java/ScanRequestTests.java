import com.netsparker.teamcity.AppCommon;
import com.netsparker.teamcity.ScanRequest;
import com.netsparker.teamcity.ScanRequestResult;
import com.netsparker.teamcity.ScanType;
import net.sf.corn.httpclient.HttpResponse;
import org.jdom.JDOMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanRequestTests{
	@Test
	public void ScanSuccessRequest() throws Exception {
		ScanRequest scanRequest = CreateScanRequest();
		HttpResponse httpResponse = scanRequest.scanRequest();
		boolean isValid= (boolean) AppCommon.ParseJsonValue(httpResponse.getData(),"IsValid");
		String Id=(String) AppCommon.ParseJsonValue(httpResponse.getData(),"ScanTaskId");
		String errorMessage = (String)AppCommon.ParseJsonValue(httpResponse.getData(),"ErrorMessage");
		
		ScanRequestResult result= new ScanRequestResult(httpResponse,"http://localhost:2097", 5,"","");
		
		Assert.assertNotNull(Id);
		Assert.assertEquals(201, httpResponse.getCode());
	}
	
	@Test
	public void ScanValidationRequest() throws IOException, URISyntaxException, ParseException {
		ScanRequest scanRequest = CreateScanRequest();
		HttpResponse httpResponse = scanRequest.testRequest();
		boolean isValid= (boolean) AppCommon.ParseJsonValue(httpResponse.getData(),"IsValid");
		String errorMessage = (String)AppCommon.ParseJsonValue(httpResponse.getData(),"ErrorMessage");
		
		Assert.assertTrue(isValid);
		Assert.assertEquals(200, httpResponse.getCode());
	}
	
	private ScanRequest CreateScanRequest() throws MalformedURLException, URISyntaxException {
		Map<String, String> parameters = new HashMap<>();
		AddAPIParameters(parameters);
		AddScanParameters(parameters);
		ScanRequest scanRequest = new ScanRequest(parameters);
		
		return scanRequest;
	}
	
	private void AddAPIParameters(Map<String, String> parameters) {
		parameters.put(ScanRequest.API_URL_Literal, "http://localhost:2097/");
		parameters.put(ScanRequest.API_TOKEN_Literal, "JC/+UUhs9BgSHHafXZLjjGElDf6Q0w+yxSWUudPxvQ0=");
	}
	
	private void AddScanParameters(Map<String, String> parameters) {
		parameters.put(ScanRequest.SCAN_TYPE_Literal, ScanType.FullWithPrimaryProfile.name());
		parameters.put(ScanRequest.WEBSITE_ID_Literal, "2752dce4-0a21-49e3-01fc-a86b009ca893");
		parameters.put(ScanRequest.PROFILE_ID_Literal, "dfecba1a-c0c1-48de-056a-a87203659ba5");
	}
}
