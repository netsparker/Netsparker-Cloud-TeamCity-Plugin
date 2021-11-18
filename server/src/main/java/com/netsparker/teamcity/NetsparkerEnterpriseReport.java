package com.netsparker.teamcity;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.util.Map;

public class NetsparkerEnterpriseReport extends LogTabBase {

	private PluginSettingsManager pluginSettingsManager;

	public NetsparkerEnterpriseReport(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server,
			@NotNull PluginDescriptor descriptor, @NotNull PluginSettingsManager pluginSettingsManager) {
		super("Netsparker Enterprise Report", "NetsparkerEnterpriseReportTab", pagePlaces, server);
		setTabTitle(getTitle());
		setPluginName(getClass().getSimpleName());
		setIncludeUrl(descriptor.getPluginResourcesPath("netsparkerEnterpriseReport.jsp"));
		this.pluginSettingsManager = pluginSettingsManager;
		// addCssFile(descriptor.getPluginResourcesPath("css/style.css"));
		// addJsFile(descriptor.getPluginResourcesPath("js/script.js"))
	}

	@Override
	protected void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request,
			@NotNull SBuild build) {
		try {
			Map<String, String> runnerProperties = getProperties(build);

			DataStorage dataStorage = new DataStorage(server);
			ScanRequestResult scanRequestResult = dataStorage.GetScanRequestResult(build.getBuildId(),
					runnerProperties);

			ServerLogger.logInfo("NetsparkerEnterpriseReport", "Requesting the report...");

			String apiToken = pluginSettingsManager.getPluginSettings().getApiToken();

			ScanReport report = scanRequestResult.getReport(apiToken);

			ServerLogger.logInfo("NetsparkerEnterpriseReport", "Parsing the report...");

			String content = report.getContent();

			// api response content can be different but if its report(html) then html
			// encode it
			if (report.isReportGenerated() && !StringUtils.isBlank(content)) {
				content = StringEscapeUtils.escapeHtml4(content);
			}

			model.put("content", content);

			model.put("isReportGenerated", String.valueOf(report.isReportGenerated()));

			boolean hasError = false;
			if (report.getStatusCode() != HttpURLConnection.HTTP_OK) {
				hasError = true;
			}
			model.put("hasError", hasError);

			String errorMessage = "";
			if (hasError) {
				errorMessage = content;
			}

			model.put("errorMessage", errorMessage);

			ServerLogger.logInfo("NetsparkerEnterpriseReport", "Getting the report info succeeded.");
		} catch (Exception exception) {
			ServerLogger.logError("NetsparkerEnterpriseReport", exception);

			model.put("isReportGenerated", "false");
			model.put("content", "''");
			model.put("hasError", "true");
			model.put("errorMessage", "An error occurred during the requesting scan report.");
		}
	}

	protected String getTitle() {
		return "Netsparker Enterprise Report";
	}

	protected String getJspName() {
		return "netsparkerEnterpriseReport.jsp";
	}
}
