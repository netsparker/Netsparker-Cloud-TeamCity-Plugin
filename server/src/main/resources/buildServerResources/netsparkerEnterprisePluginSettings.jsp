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
                    <td colspan="2">
                        <a class="btn btn_hint" id="netsparkerEnterpriseTestConnectionButton">Test Connection</a>
                        <span id="netsparkerEnterpriseConnectionResult"></span>
                    </td>
                </tr>
                </l:settingsGroup>
            </table>
            <script>
                var ncServerURLInput, ncApiTokenInput, ncApiTokenInitialValueInput, ncEncryptedApiTokenInput,
                    ncPublicKeyInput;
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

                    ncTestConnectionResultSpan = jQuery("#netsparkerEnterpriseConnectionResult");
                    ncTestConnectionButton = jQuery("#netsparkerEnterpriseTestConnectionButton");

                    ncTestConnectionButton.click(ncTestConnection);
                    ncServerURLInput.attr('placeholder', "URL, like 'https://www.netsparkercloud.com'");

                    ncTestRequestParams.ApiTokenInitialValue = ncApiTokenInput.val();
                    ncApiTokenInitialValueInput.val(ncApiTokenInput.val());

                    updateNcParams();
                }

                function updateNcParams() {
                    ncScanParams.serverURL = ncServerURLInput.val();
                    ncScanParams.apiToken = ncApiTokenInput.val();
                    ncScanParams.encryptedApiToken = ncEncryptedApiTokenInput.val();
                    if (ncScanParams.apiToken != ncTestRequestParams.ApiTokenInitialValue) {
                        ncScanParams.encryptedApiToken = "";
                        ncEncryptedApiTokenInput.val("");
                    }
                    ncTestRequestParams.netsparkerEnterpriseServerURL = ncScanParams.serverURL;
                    ncTestRequestParams.netsparkerEnterpriseApiToken = BS.Encrypt.encryptData(ncScanParams.apiToken, ncPublicKeyInput.val());
                    ncTestRequestParams.netsparkerEnterpriseEncryptedApiToken = ncScanParams.encryptedApiToken;
                }

                function ncTestConnection() {
                    updateNcParams();
                    var request = jQuery.post("/netsparkerenterprise/testconnection.html", ncTestRequestParams);

                    request.done(function (data, statusText, xhr) {
                        var status = jQuery(data).find("httpStatusCode").text();
                        if (status == "200") {
                            ncTestConnectionResultSpan.text("Successfully connected to the Netsparker Enterprise.");
                        } else {
                            if (status == "0") {
                                ncTestConnectionResultSpan.text("Failed to connect to the Netsparker Enterprise. HTTP status code: 0");
                            } else {
                                ncTestConnectionResultSpan.text("Netsparker Enterprise rejected the request. HTTP status code: " + status);
                            }
                        }
                    });

                    request.fail(function (xhr, statusText) {
                        ncTestConnectionResultSpan.text("Controller not found. HTTP status code: " + xhr.status);
                    });
                }
            </script>
            <div class="saveButtonsBlock" id="saveButtons" style="display:block">
                <forms:submit label="Save"></forms:submit>
                <a class="btn cancel" href="${pluginSettingsManager.cameFromSupport.cameFromUrl}">Cancel</a>
                <forms:saving/>
                <input type="hidden" value="0" name="numberOfSettingsChangesEvents">
                <input type="hidden" id="publicKey" name="publicKey"
                       value="${pluginSettingsManager.hexEncodedPublicKey}"/>
            </div>
        </form>
    </bs:refreshable>
</div>