package com.netsparker.teamcity;

import jetbrains.buildServer.version.ServerVersionHolder;
import net.sf.corn.httpclient.HttpForm;

import java.util.Map;

public class VCSCommit{
	public static final String BUILD_ID_LITERAL = "netsparkerCloudBuildID";
	public static final String BUILD_CONFIGURATION_NAME_LITERAL = "netsparkerCloudBuildConfigurationName";
	public static final String BUILD_URL_LITERAL = "netsparkerCloudBuildURL";
	public static final String BUILD_HAS_CHANGE = "netsparkerCloudBuildHasChange";
	public static final String VCS_NAME_LITERAL = "netsparkerCloudVCS_Name";
	public static final String VCS_VERSION_LITERAL = "netsparkerCloudVCS_BranchVersion";
	public static final String VCS_Timestamp = "netsparkerCloudVCS_TimeStamp";
	public static final String COMMITTER_USERNAME_LITERAL = "netsparkerCloudCommitterUserName";
	
	
	private final Map<String, String> parametersWithPrefix;
	
	public VCSCommit(Map<String, String> parameters) {
		this.parametersWithPrefix = parameters;
		
		serverVersion=ServerVersionHolder.getVersion().getDisplayVersion();
		pluginVersion="1.0.0";
		buildId = parameters.get(BUILD_ID_LITERAL);
		buildConfigurationName = parameters.get(BUILD_CONFIGURATION_NAME_LITERAL);
		buildURL = parameters.get(BUILD_URL_LITERAL);
		buildHasChange = Boolean.parseBoolean(parameters.get(BUILD_HAS_CHANGE));
		vcsName = parameters.get(VCS_NAME_LITERAL);
		vcsVersion = parameters.get(VCS_VERSION_LITERAL);
		committer = parameters.get(COMMITTER_USERNAME_LITERAL);
		ciTimestamp = parameters.get(VCSCommit.VCS_Timestamp);
	}
	
	public final String serverVersion;
	public final String pluginVersion;
	public final String buildId;
	public final String buildConfigurationName;
	public final String buildURL;
	public final boolean buildHasChange;
	public final String vcsName;
	public final String vcsVersion;
	public final String ciTimestamp;
	public final String committer;
	
	public void addVcsCommitInfo(HttpForm client) {
		client.putFieldValue("VcsCommitInfoModel.CiBuildId", buildId);
		client.putFieldValue("VcsCommitInfoModel.IntegrationSystem", "Teamcity");
		client.putFieldValue("VcsCommitInfoModel.CiBuildServerVersion", ServerVersionHolder.getVersion().getDisplayVersion());
		client.putFieldValue("VcsCommitInfoModel.CiNcPluginVersion","1.0.0");
		client.putFieldValue("VcsCommitInfoModel.CiBuildConfigurationName", buildConfigurationName);
		client.putFieldValue("VcsCommitInfoModel.CiBuildUrl", buildURL);
		client.putFieldValue("VcsCommitInfoModel.CiBuildHasChange", String.valueOf(buildHasChange));
		client.putFieldValue("VcsCommitInfoModel.CiTimestamp", ciTimestamp);
		client.putFieldValue("VcsCommitInfoModel.VcsName", vcsName);
		client.putFieldValue("VcsCommitInfoModel.VcsVersion", vcsVersion);
		client.putFieldValue("VcsCommitInfoModel.Committer", committer);
	}
}
