package com.netsparker.teamcity;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WebsiteModelController extends AjaxControllerBase{
	private final PluginSettingsManager pluginSettingsManager;
	
	public WebsiteModelController(@NotNull final SBuildServer server,
	                              @NotNull final WebControllerManager webControllerManager,
	                              @NotNull final PluginSettingsManager pluginSettingsManager) {
		super(server);
		this.pluginSettingsManager = pluginSettingsManager;
		webControllerManager.registerController(ScanConstants.WEBSITEMODEL_CONTROLLER_RELATIVE_URL, this);
	}
	
	@Override
	@Nullable
	protected ModelAndView doGet(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) {
		return null;
	}
	
	@Override
	protected void doPost(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Element element) {
		Map<String, String> parameters = new HashMap<>();

		final PluginSettings pluginSettings = pluginSettingsManager.getPluginSettings();
		try {
			parameters.put("netsparkerEnterpriseServerURL", pluginSettings.getServerURL());
			parameters.put("netsparkerEnterpriseApiToken", pluginSettings.getApiToken());
			WebsiteModelRequest websiteModelRequest = new WebsiteModelRequest(parameters);
			websiteModelRequest.requestPluginWebSiteModels();
			int httpStatusCode = websiteModelRequest.getResponseStatusCode();
			
			element.addContent(new Element("httpStatusCode").setText(String.valueOf(httpStatusCode)));
			
			if (httpStatusCode == 200) {
				ServerLogger.logInfo("WebsiteModelController", "Netsparker Enterprise test connection succeeded.");
				try {
					SAXBuilder builder = new SAXBuilder();
					String xmlData = websiteModelRequest.getResponseContent();
					InputStream stream = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
					Document document = builder.build(stream);
					final Element tempElement = (Element) document.getContent().get(0);
					final Element dataElement = tempElement.setName("PluginWebsiteModels");
					dataElement.detach();
					element.addContent(dataElement);
					element.addContent(new Element("isModelParsed").setText("true"));
				} catch (Exception ex) {
					element.addContent(new Element("isModelParsed").setText("false"));
				}
			} else {
				ServerLogger.logError("WebsiteModelController", "Netsparker Enterprise rejected the request. HTTP status code: " + String.valueOf(httpStatusCode));
			}
			
		} catch (Exception e) {
			ServerLogger.logError("WebsiteModelController", e);
			element.removeContent();
			element.addContent(new Element("httpStatusCode").setText("0"));
		}
	}
}
