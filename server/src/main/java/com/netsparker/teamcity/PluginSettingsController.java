package com.netsparker.teamcity;

import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PluginSettingsController extends AjaxControllerBase{
	
	private PluginSettingsManager pluginSettingsManager;
	
	public PluginSettingsController(@NotNull final SBuildServer server,
	                                final PluginSettingsManager pluginSettingsManager) {
		super(server);
		this.pluginSettingsManager = pluginSettingsManager;
	}
	
	@Override
	protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
		
		ActionErrors errors = validate(request);
		if (errors.hasErrors()) {
			ServerLogger.logWarn("PluginSettingsController", errors.getErrors().size() + " Errors:");
			for (ActionErrors.Error error : errors.getErrors()) {
				ServerLogger.logWarn("PluginSettingsController", error.getMessage());
			}
			errors.serialize(xmlResponse);
			ServerLogger.logWarn("PluginSettingsController", "Parameters are not valid.");
			return;
		}
		
		setPluginSettings(request);
		
		getOrCreateMessages(request).addMessage("settingsSaved", "Settings saved successfully.");
	}
	
	private ActionErrors validate(final HttpServletRequest request) {
		ServerLogger.logInfo("PluginSettingsController", "Validating parameters...");
		ActionErrors errors = new ActionErrors();
		
		final String serverURL = request.getParameter("netsparkerEnterpriseServerURL");
		if (StringUtil.isEmptyOrSpaces(serverURL)) {
			errors.addError("netsparkerEnterpriseApiURL", "The parameter 'Netsparker Enterprise Server URL' must be specified.");
			ServerLogger.logWarn("PluginSettingsController", "Server URL is Empty.");
		}
		
		if (!StringUtil.isEmptyOrSpaces(serverURL) && !AppCommon.IsUrlValid(serverURL)) {
			errors.addError("netsparkerEnterpriseApiURL", "The parameter 'Netsparker Enterprise Server URL' is invalid.");
			ServerLogger.logWarn("PluginSettingsController", "Server URL is invalid.");
		}
		
		String apiToken = RSACipher.decryptWebRequestData(request.getParameter("encryptedNetsparkerEnterpriseApiToken"));
		if(apiToken == null || StringUtil.isEmptyOrSpaces(apiToken)){
			apiToken = RSACipher.decryptWebRequestData(request.getParameter("netsparkerEnterpriseEncryptedApiToken"));
		}

		if (StringUtil.isEmptyOrSpaces(apiToken)) {
			errors.addError("netsparkerEnterpriseApiToken", "The parameter 'API Token' must be specified.");
			ServerLogger.logWarn("PluginSettingsController", "API token is empty.");
		}

		final Boolean proxyUsed = request.getParameter("netsparkerEnterpriseProxyUsed") != null;
		if (proxyUsed) {
			final String proxyHost = request.getParameter("netsparkerEnterpriseProxyHost");
			if (StringUtil.isEmptyOrSpaces(proxyHost)) {
				errors.addError("netsparkerEnterpriseProxyHost", "The parameter 'Proxy Host' must be specified.");
				ServerLogger.logWarn("PluginSettingsController", "Proxy Host is Empty.");
			}

			final String proxyPort = request.getParameter("netsparkerEnterpriseProxyPort");
			if (StringUtil.isEmptyOrSpaces(proxyPort)) {
				errors.addError("netsparkerEnterpriseProxyPort", "The parameter 'Proxy Port' must be specified.");
				ServerLogger.logWarn("PluginSettingsController", "Proxy Port is Empty.");
			}
		}
		
		return errors;
	}
	
	private void setPluginSettings(HttpServletRequest request) {
		final PluginSettings settings = new PluginSettings();
		ServerLogger.logInfo("PluginSettingsController", "Saving parameters...");

		String serverURL = request.getParameter("netsparkerEnterpriseServerURL");
		String apiToken = RSACipher.decryptWebRequestData(request.getParameter("encryptedNetsparkerEnterpriseApiToken"));
		String apiTokenInitial = request.getParameter("netsparkerEnterpriseApiTokenInitialValue");

		settings.setServerURL(serverURL);
		if (!apiToken.equals(apiTokenInitial)) {
			settings.setApiToken(apiToken);
		} else {
			settings.setApiToken(pluginSettingsManager.getPluginSettings().getApiToken());
		}

		Boolean proxyUsed = request.getParameter("netsparkerEnterpriseProxyUsed") != null;

		if(proxyUsed){

			String proxyHost = request.getParameter("netsparkerEnterpriseProxyHost");
			String proxyPort = request.getParameter("netsparkerEnterpriseProxyPort");
			String proxyUsername = request.getParameter("netsparkerEnterpriseProxyUsername");

			String encryptedPassword = RSACipher.decryptWebRequestData(request.getParameter("encryptedNetsparkerEnterpriseProxyPassword"));
			if(encryptedPassword == null || StringUtil.isEmptyOrSpaces(encryptedPassword)){
				encryptedPassword = RSACipher.decryptWebRequestData(request.getParameter("netsparkerEnterpriseEncryptedProxyPassword"));
			}

			if (!StringUtil.isEmptyOrSpaces(encryptedPassword)) {

				String proxyPasswordInitial = request.getParameter("netsparkerEnterpriseProxyPasswordInitialValue");

				if (!encryptedPassword.equals(proxyPasswordInitial)) {
					settings.setProxyPassword(encryptedPassword);
				} else {
					settings.setProxyPassword(pluginSettingsManager.getPluginSettings().getProxyPassword());
				}
			}

			settings.setProxyUsed(true);
			settings.setProxyHost(proxyHost);
			settings.setProxyPort(proxyPort);
			settings.setProxyUsername(proxyUsername);
		}
		else{
			settings.setProxyUsed(false);
		}

		pluginSettingsManager.setPluginSettings(settings);
		pluginSettingsManager.save();
		ServerLogger.logInfo("PluginSettingsController", "Plugin parameters saved successfully.");
	}
}
