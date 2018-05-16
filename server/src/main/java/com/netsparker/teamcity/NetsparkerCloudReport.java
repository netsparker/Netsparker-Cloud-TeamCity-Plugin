package com.netsparker.teamcity;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class NetsparkerCloudReport extends LogTabBase{
	
	private PluginSettingsManager pluginSettingsManager;
	
	public NetsparkerCloudReport(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server, @NotNull PluginDescriptor descriptor, @NotNull PluginSettingsManager pluginSettingsManager) {
		super("Netsparker Cloud Report", "NetsparkerCloudReportTab", pagePlaces, server);
		setTabTitle(getTitle());
		setPluginName(getClass().getSimpleName());
		setIncludeUrl(descriptor.getPluginResourcesPath("netsparkerCloudReport.jsp"));
		this.pluginSettingsManager = pluginSettingsManager;
		//addCssFile(descriptor.getPluginResourcesPath("css/style.css"));
		//addJsFile(descriptor.getPluginResourcesPath("js/script.js"))
	}
	
	@Override
	protected void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request, @NotNull SBuild build) {
		try {
			Map<String, String> runnerProperties = getProperties(build);
			
			DataStorage dataStorage = new DataStorage(server);
			ScanRequestResult scanRequestResult = dataStorage.GetScanRequestResult(build.getBuildId(), runnerProperties);
			
			ServerLogger.logInfo("NetsparkerCloudReport", "Requesting the report...");
			PluginSettings pluginSettings = pluginSettingsManager.getPluginSettings();
			ScanReport report = scanRequestResult.getReport(pluginSettings.getServerURL(), pluginSettings.getApiToken());
			
			ServerLogger.logInfo("NetsparkerCloudReport", "Parsing the report...");
			model.put("content", report.getContent());
			model.put("isReportGenerated", String.valueOf(report.isReportGenerated()));
			model.put("hasError", "false");
			model.put("errorMessage", "''");
			
			ServerLogger.logInfo("NetsparkerCloudReport", "Getting the report info succeeded.");
		} catch (Exception exception) {
			ServerLogger.logError("NetsparkerCloudReport", exception);
			
			model.put("isReportGenerated", "false");
			model.put("content", "''");
			model.put("hasError", "true");
			model.put("errorMessage", "An error occurred during the requesting scan report.");
		}
	}
	
	protected String getTitle() {
		return "Netsparker Cloud Report";
	}
	
	protected String getJspName() {
		return "netsparkerCloudReport.jsp";
	}
}
