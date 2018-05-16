import com.netsparker.teamcity.ScanRequest;
import com.netsparker.teamcity.WebsiteModelRequest;
import net.sf.corn.httpclient.HttpResponse;
import org.jdom.JDOMException;
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

import java.util.Map;

public class WebSiteModelsRequestTest{
	
	@Test
	public void getPluginWebSiteModelsRequest() throws IOException, URISyntaxException, ParseException, JDOMException {
		WebsiteModelRequest websiteModelRequest = CreateWebSiteRequest();
		HttpResponse httpResponse = websiteModelRequest.getPluginWebSiteModels();
		
		Element element = new Element("data");
		element.addContent(new Element("httpStatusCode").setText(String.valueOf(httpResponse.getCode())));
		
		SAXBuilder builder = new SAXBuilder();
		String exampleXML = httpResponse.getData();
		InputStream stream = new ByteArrayInputStream(exampleXML.getBytes("UTF-8"));
		Document anotherDocument = builder.build(stream);
		final Element dataElement = (Element) anotherDocument.getContent().get(0);
		final Element element1 = dataElement.setName("PluginWebsiteModels");
		
		element1.detach();
		element.addContent(element1);
		
		Assert.assertEquals(200, httpResponse.getCode());
	}
	
	private WebsiteModelRequest CreateWebSiteRequest() throws MalformedURLException, URISyntaxException {
		Map<String, String> parameters = new HashMap<>();
		AddAPIParameters(parameters);
		WebsiteModelRequest websiteModelRequest = new WebsiteModelRequest(parameters);
		
		return websiteModelRequest;
	}
	
	private void AddAPIParameters(Map<String, String> parameters) {
		parameters.put(ScanRequest.API_URL_Literal, "http://localhost:2097/");
		parameters.put(ScanRequest.API_TOKEN_Literal, "JC/+UUhs9BgSHHafXZLjjGElDf6Q0w+yxSWUudPxvQ0=");
	}
	
}
