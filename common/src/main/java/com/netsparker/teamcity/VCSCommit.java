package com.netsparker.teamcity;

import jetbrains.buildServer.version.ServerVersionHolder;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;
import java.util.Map;

public class VCSCommit{
	public static final String BUILD_ID_LITERAL = "netsparkerEnterpriseBuildID";
	public static final String BUILD_CONFIGURATION_NAME_LITERAL = "netsparkerEnterpriseBuildConfigurationName";
	public static final String BUILD_URL_LITERAL = "netsparkerEnterpriseBuildURL";
	public static final String BUILD_HAS_CHANGE = "netsparkerEnterpriseBuildHasChange";
	public static final String VCS_NAME_LITERAL = "netsparkerEnterpriseVCS_Name";
	public static final String VCS_VERSION_LITERAL = "netsparkerEnterpriseVCS_BranchVersion";
	public static final String VCS_Timestamp = "netsparkerEnterpriseVCS_TimeStamp";
	public static final String COMMITTER_USERNAME_LITERAL = "netsparkerEnterpriseCommitterUserName";
	public static final String pluginVersion = "1.1.0";

	
	private final Map<String, String> parametersWithPrefix;
	
	public VCSCommit(Map<String, String> parameters) {
		this.parametersWithPrefix = parameters;
		
		serverVersion=ServerVersionHolder.getVersion().getDisplayVersion();
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
	public final String buildId;
	public final String buildConfigurationName;
	public final String buildURL;
	public final boolean buildHasChange;
	public final String vcsName;
	public final String vcsVersion;
	public final String ciTimestamp;
	public final String committer;
	
	public void addVcsCommitInfo(List<NameValuePair> params) {
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildId", buildId));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.IntegrationSystem", "Teamcity"));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildServerVersion", ServerVersionHolder.getVersion().getDisplayVersion()));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiNcPluginVersion",pluginVersion));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildConfigurationName", buildConfigurationName));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildUrl", buildURL));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildHasChange", String.valueOf(buildHasChange)));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.CiTimestamp", ciTimestamp));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.VcsName", vcsName));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.VcsVersion", vcsVersion));
		params.add(new BasicNameValuePair("VcsCommitInfoModel.Committer", committer));
	}
}
