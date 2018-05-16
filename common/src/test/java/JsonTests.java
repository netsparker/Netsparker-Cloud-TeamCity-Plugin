import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class JsonTests{
	@Test
	public void jsonGetRequestParsingTest() throws IOException, URISyntaxException, ParseException {
		//Fake Online REST API for Testing and Prototyping
		HttpForm client = new HttpForm(new URL("https://jsonplaceholder.typicode.com/posts/1").toURI());
		HttpResponse httpResponse = client.doGet();
		String jsonString = httpResponse.getData();
		
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(jsonString);
		
		Object userId = obj.get("userId");
		String actual = String.valueOf(userId);
		
		//Assert.assertEquals("1", actual);
	}
	
	@Test
	public void jsonPostRequestParsingTest() throws IOException, URISyntaxException, ParseException {
		//Fake response generator-https://www.mocky.io/
		HttpForm client = new HttpForm(new URL("http://www.mocky.io/v2/5a04e3de300000b911fe0906").toURI());
		client.putFieldValue("title", "foo");
		HttpResponse httpResponse = client.doPost();
		String jsonString = httpResponse.getData();
		
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(jsonString);
		
		Object userId = obj.get("userId");
		String actual = String.valueOf(userId);
		
		//Assert.assertEquals("1", actual);
	}
	
	@Test
	public void jsonParseTest() throws Exception {
		String jsonString = "{\"Foo3\":\"Bar3\",\"VeryImportantList\":{\"Foo\":\"Bar\",\"Foo2\":\"Bar2\"},\"Foo4\":\"Bar4\"}";
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(jsonString);
		Object foo3 = obj.get("Foo4");
		
		Assert.assertEquals("Bar2", String.valueOf(foo3));
	}
	
	@Test
	public void booleanParseTest(){
		boolean x=Boolean.parseBoolean("true");
		Assert.assertTrue(x);
	}
}

