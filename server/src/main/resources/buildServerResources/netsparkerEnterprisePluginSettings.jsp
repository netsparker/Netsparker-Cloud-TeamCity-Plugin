<%@ page import="com.netsparker.teamcity.ApiRequestBase" %>
<%@include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<jsp:useBean id="pluginSettingsManager" type="com.netsparker.teamcity.PluginSettingsManager" scope="request"/>
<c:url var="controllerUrl" value="/netsparkerenterprise/pluginsettings.html"/>
<c:url var="logoUrl" value="${teamcityPluginResourcesPath}images/logo.svg"/>
<jsp:useBean id="serverTC" type="jetbrains.buildServer.serverSide.SBuildServer" scope="request"/>

<script type="text/javascript">
    var pluginSettingsForm = OO.extend(BS.AbstractPasswordForm, {
        formElement: function () {
            return $("netsparkerEnterprisePluginSettingsForm")
        },
        save: function () {
            BS.PasswordFormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener, {
                onNetsparkerEnterpriseApiURLError: function (elem) {
                    $("error_netsparkerEnterpriseApiURL").innerHTML = elem.firstChild.nodeValue;
                    pluginSettingsForm.highlightErrorField($("netsparkerEnterpriseServerURL"));
                },

                onNetsparkerEnterpriseApiTokenError: function (elem) {
                    $("error_netsparkerEnterpriseApiToken").innerHTML = elem.firstChild.nodeValue;
                    pluginSettingsForm.highlightErrorField($("netsparkerEnterpriseApiToken"));
                },

                onNetsparkerEnterpriseProxyHostError: function (elem) {
                    $("error_netsparkerEnterpriseProxyHost").innerHTML = elem.firstChild.nodeValue;
                    pluginSettingsForm.highlightErrorField($("error_netsparkerEnterpriseProxyHost"));
                },

                onNetsparkerEnterpriseProxyPortError: function (elem) {
                    $("error_netsparkerEnterpriseProxyPort").innerHTML = elem.firstChild.nodeValue;
                    pluginSettingsForm.highlightErrorField($("error_netsparkerEnterpriseProxyPort"));
                },

                onSuccessfulSave: function () {
                    pluginSettingsForm.enable();
                },

                onCompleteSave: function (form, responseXml, wereErrors) {
                    BS.ErrorsAwareListener.onCompleteSave(form, responseXml, wereErrors);
                    if (!wereErrors) {
                        $('pluginSettingsContainer').refresh();
                    }
                }
            }));

            return false;
        }
    });
</script>
<div>
    <bs:refreshable containerId="pluginSettingsContainer" pageUrl="${pageUrl}">
        <bs:messages key="settingsSaved"/>
        <form id="netsparkerEnterprisePluginSettingsForm" action="${controllerUrl}"
              method="post" onsubmit="{return pluginSettingsForm.save()}">
            <table class="runnerFormTable">
                <tr>
                    <td colspan="2">
                    <div style="color: #3f3f3f;display:inline;font-size: 130%;">
                        <img src="${logoUrl}" alt="Netsparker Enterprise"
                             style="vertical-align:top; margin-bottom:1px;display: inline-block;height:1.1em;width: auto;"/>
                        <span style="display:inline-block;zoom:1;color: #3f3f3f;font-size: 130%;">
                            Netsparker Enterprise
                        </span>
                    </div>
                    </td>
                </tr>
                <l:settingsGroup title="API Settings">
                <tr>
                    <th>
                        <label for="netsparkerEnterpriseServerURL">Netsparker Enterprise Server URL:<l:star/>
                            <bs:helpIcon iconTitle="URL, like 'https://www.netsparkercloud.com'"/>
                        </label>
                    </th>
                    <td>
                        <input type="text" name="netsparkerEnterpriseServerURL" id="netsparkerEnterpriseServerURL"
                               value="${pluginSettingsManager.pluginSettings.serverURL}" class="longField">
                        <span class="error" id="error_netsparkerEnterpriseApiURL"></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="netsparkerEnterpriseApiToken">API Token:<l:star/>
                            <bs:helpIcon iconTitle="It can be found at 'Your Account > API Settings' page in the Netsparker Enterprise.<br/>
                         User must have 'Start Scans' permission for the target website."/>
                        </label>
                    </th>
                    <td>
                        <input type="password" name="netsparkerEnterpriseApiToken" id="netsparkerEnterpriseApiToken"
                               value="${pluginSettingsManager.random}"
                               class="longField textProperty"/>

                        <input type="hidden" id="netsparkerEnterpriseEncryptedApiToken"
                               name="netsparkerEnterpriseEncryptedApiToken"
                               value="${pluginSettingsManager.pluginSettings.encryptedApiToken}"/>

                        <input type="hidden" id="netsparkerEnterpriseApiTokenInitial"
                               name="netsparkerEnterpriseApiTokenInitialValue" value=""/>

                        <span class="error" id="error_netsparkerEnterpriseApiToken"></span>
                    </td>
                </tr>

                    <tr>
                            <th>
                                <label for="netsparkerEnterpriseProxyUsed">Use Proxy:
                                    <bs:helpIcon iconTitle="Proxy Use"/>
                                </label>
                            </th>
                            <td>
                                <forms:checkbox name="netsparkerEnterpriseProxyUsed" checked="${pluginSettingsManager.pluginSettings.proxyUsed}"
                                                onclick="$('proxySettings').toggle()" id="netsparkerEnterpriseProxyUsed"/>
                                <div id="proxySettings" style="display: ${pluginSettingsManager.pluginSettings.proxyUsed ? 'block' : 'none'}; margin-left: -7px;">
                                    <table style="display: flex">
                                        <tr>
                                            <th>
                                                <label for="netsparkerEnterpriseProxyHost">Host:<l:star/></label>
                                            </th>
                                            <td>
                                                <input type="text" name="netsparkerEnterpriseProxyHost"
                                                       id="netsparkerEnterpriseProxyHost"
                                                       value="${pluginSettingsManager.pluginSettings.proxyHost}"
                                                       class="mediumField">
                                                <span class="error" id="error_netsparkerEnterpriseProxyHost"></span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                <label for="netsparkerEnterpriseProxyPort">Port:<l:star/></label>
                                            </th>
                                            <td>
                                                <input type="number" name="netsparkerEnterpriseProxyPort"
                                                       id="netsparkerEnterpriseProxyPort"
                                                       value="${pluginSettingsManager.pluginSettings.proxyPort}"
                                                       class="mediumField">
                                                <span class="error" id="error_netsparkerEnterpriseProxyPort"></span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                <label for="netsparkerEnterpriseProxyUsername">Username:</label>
                                            </th>
                                            <td>
                                                <input type="text" name="netsparkerEnterpriseProxyUsername"
                                                       id="netsparkerEnterpriseProxyUsername"
                                                       value="${pluginSettingsManager.pluginSettings.proxyUsername}"
                                                       class="mediumField">
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                <label for="netsparkerEnterpriseProxyPassword">Password:</label>
                                            </th>
                                            <td>
                                                <input type="password" name="netsparkerEnterpriseProxyPassword"
                                                       id="netsparkerEnterpriseProxyPassword"
                                                        <c:if test="${pluginSettingsManager.pluginSettings.proxyPassword != ''}">value="${pluginSettingsManager.random}"</c:if>
                                                       class="mediumField">

                                                <input type="hidden" id="netsparkerEnterpriseEncryptedProxyPassword"
                                                       name="netsparkerEnterpriseEncryptedProxyPassword"
                                                       value="${pluginSettingsManager.pluginSettings.encryptedProxyPassword}"/>

                                                <input type="hidden" id="netsparkerEnterpriseProxyPasswordInitial"
                                                       name="netsparkerEnterpriseProxyPasswordInitialValue" value=""/>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </td>
                    </tr>
                </l:settingsGroup>
            </table>
            <script>
                var ncServerURLInput, ncApiTokenInput, ncApiTokenInitialValueInput, ncEncryptedApiTokenInput,
                    ncPublicKeyInput,ncProxyUsed,ncProxyHost,ncProxyPort,ncProxyUsername,ncProxyPassword,ncEncryptedProxyPasswordInput,ncProxyPasswordInitialValueInput;
                var ncTestConnectionResultSpan, ncTestConnectionButton;
                var ncScanParams = {}, ncTestRequestParams = {};
                //do noy use $ for Jquery instead use jQuery
                jQuery(document).ready(function () {
                    initializeNcElementsAndParams();
                })

                function initializeNcElementsAndParams() {
                    ncServerURLInput = jQuery("#netsparkerEnterpriseServerURL");
                    ncApiTokenInput = jQuery("#netsparkerEnterpriseApiToken");
                    ncPublicKeyInput = jQuery("#publicKey");
                    ncApiTokenInitialValueInput = jQuery("#netsparkerEnterpriseApiTokenInitial");
                    ncEncryptedApiTokenInput = jQuery("#netsparkerEnterpriseEncryptedApiToken");
                    ncProxyPasswordInitialValueInput = jQuery("#netsparkerEnterpriseProxyPasswordInitial");
                    ncEncryptedProxyPasswordInput = jQuery("#netsparkerEnterpriseEncryptedProxyPassword");


                    ncProxyUsed = jQuery("#netsparkerEnterpriseProxyUsed");
                    ncProxyHost = jQuery("#netsparkerEnterpriseProxyHost");
                    ncProxyPort = jQuery("#netsparkerEnterpriseProxyPort");
                    ncProxyUsername = jQuery("#netsparkerEnterpriseProxyUsername");
                    ncProxyPassword = jQuery("#netsparkerEnterpriseProxyPassword");

                    ncTestConnectionResultSpan = jQuery("#netsparkerEnterpriseConnectionResult");
                    ncTestConnectionButton = jQuery("#netsparkerEnterpriseTestConnectionButton");

                    ncTestConnectionButton.click(ncTestConnection);
                    ncServerURLInput.attr('placeholder', "URL, like 'https://www.netsparkercloud.com'");

                    ncTestRequestParams.ApiTokenInitialValue = ncApiTokenInput.val();
                    ncApiTokenInitialValueInput.val(ncApiTokenInput.val());

                    ncTestRequestParams.ProxyPasswordInitialValue = ncProxyPassword.val();
                    ncProxyPasswordInitialValueInput.val(ncProxyPassword.val());

                    updateNcParams();
                }

                function updateNcParams() {
                    ncScanParams.serverURL = ncServerURLInput.val();
                    ncScanParams.apiToken = ncApiTokenInput.val();
                    ncScanParams.encryptedApiToken = ncEncryptedApiTokenInput.val();
                    ncScanParams.encryptedProxyPassword = ncEncryptedProxyPasswordInput.val();
                    ncScanParams.proxyUsed = ncProxyUsed.is(':checked');

                    ncScanParams.proxyHost = ncProxyHost.val();
                    ncScanParams.proxyPort = ncProxyPort.val();
                    ncScanParams.proxUsername = ncProxyUsername.val();
                    ncScanParams.proxyPassword = ncProxyPassword.val();

                    if (ncScanParams.proxyPassword != ncTestRequestParams.ProxyPasswordInitialValue) {
                        ncScanParams.encryptedProxyPassword= "";
                        ncEncryptedProxyPasswordInput.val("");
                    }

                    if (ncScanParams.apiToken != ncTestRequestParams.ApiTokenInitialValue) {
                        ncScanParams.encryptedApiToken = "";
                        ncEncryptedApiTokenInput.val("");
                    }
                    ncTestRequestParams.netsparkerEnterpriseServerURL = ncScanParams.serverURL;
                    ncTestRequestParams.netsparkerEnterpriseApiToken = BS.Encrypt.encryptData(ncScanParams.apiToken, ncPublicKeyInput.val());
                    ncTestRequestParams.netsparkerEnterpriseEncryptedApiToken = ncScanParams.encryptedApiToken;

                    ncTestRequestParams.netsparkerEnterpriseProxyUsed = ncScanParams.proxyUsed;
                    ncTestRequestParams.netsparkerEnterpriseProxyHost = ncScanParams.proxyHost;
                    ncTestRequestParams.netsparkerEnterpriseProxyPort = ncScanParams.proxyPort;
                    ncTestRequestParams.netsparkerEnterpriseProxyUsername = ncScanParams.proxUsername;
                    ncTestRequestParams.netsparkerEnterpriseProxyPassword = BS.Encrypt.encryptData(ncScanParams.proxyPassword, ncPublicKeyInput.val());
                    ncTestRequestParams.netsparkerEnterpriseEncryptedProxyPassword = ncScanParams.encryptedProxyPassword;
                }

                function ncTestConnection() {
                    updateNcParams();
                    var request = jQuery.post("${serverTC.rootUrl}/netsparkerenterprise/testconnection.html", ncTestRequestParams);

                    request.done(function (data, statusText, xhr) {
                        var status = jQuery(data).find("httpStatusCode").text();
                        if (status == "200") {
                            ncTestConnectionResultSpan.text("Successfully connected to the Netsparker Enterprise.").css("color","green");
                        } else {
                            if (status == "0") {
                                ncTestConnectionResultSpan.text("Failed to connect to the Netsparker Enterprise. HTTP status code: 0").css("color","red");
                            } else {
                                ncTestConnectionResultSpan.text("Netsparker Enterprise rejected the request. HTTP status code: " + status).css("color","firebrick");
                            }
                        }
                    });

                    request.fail(function (xhr, statusText) {
                        ncTestConnectionResultSpan.text("Controller not found. HTTP status code: " + xhr.status).css("color","red");
                    });

                    setTimeout(function(){
                        ncTestConnectionResultSpan.text('');
                    }, 3000)
                }

            </script>
            <div class="saveButtonsBlock" id="saveButtons" style="display:block">
                <forms:submit label="Save"></forms:submit>
                <forms:saving/>
                <input type="hidden" value="0" name="numberOfSettingsChangesEvents">
                <input type="hidden" id="publicKey" name="publicKey"
                       value="${pluginSettingsManager.hexEncodedPublicKey}"/>
                <a class="btn btn_hint" id="netsparkerEnterpriseTestConnectionButton">Test Connection</a>
            </div>
            <span id="netsparkerEnterpriseConnectionResult"></span>
        </form>
    </bs:refreshable>
</div>