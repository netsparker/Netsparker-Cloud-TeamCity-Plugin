<%@ page import="com.netsparker.teamcity.ApiRequestBase" %>
<%@include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<jsp:useBean id="pluginSettingsManager" type="com.netsparker.teamcity.PluginSettingsManager" scope="request"/>
<c:url var="controllerUrl" value="/netsparkercloud/pluginsettings.html"/>
<c:url var="logoUrl" value="${teamcityPluginResourcesPath}images/logo.svg"/>

<script type="text/javascript">
    var pluginSettingsForm = OO.extend(BS.AbstractPasswordForm, {
        formElement: function () {
            return $("netsparkerCloudPluginSettingsForm")
        },
        save: function () {
            BS.PasswordFormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener, {
                onNetsparkerCloudApiURLError: function (elem) {
                    $("error_netsparkerCloudApiURL").innerHTML = elem.firstChild.nodeValue;
                    pluginSettingsForm.highlightErrorField($("netsparkerCloudServerURL"));
                },

                onNetsparkerCloudApiTokenError: function (elem) {
                    $("error_netsparkerCloudApiToken").innerHTML = elem.firstChild.nodeValue;
                    pluginSettingsForm.highlightErrorField($("netsparkerCloudApiToken"));
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
        <form id="netsparkerCloudPluginSettingsForm" action="${controllerUrl}"
              method="post" onsubmit="{return pluginSettingsForm.save()}">
            <table class="runnerFormTable">
                <tr>
                    <td colspan="2">
                    <div style="color: #3f3f3f;display:inline;font-size: 130%;">
                        <img src="${logoUrl}" alt="Netsparker Cloud"
                             style="vertical-align:top; margin-bottom:1px;display: inline-block;height:1.1em;width: auto;"/>
                        <span style="display:inline-block;zoom:1;color: #3f3f3f;font-size: 130%;">
                            Netsparker Cloud
                        </span>
                    </div>
                    </td>
                </tr>
                <l:settingsGroup title="API Settings">
                <tr>
                    <th>
                        <label for="netsparkerCloudServerURL">Netsparker Cloud Server URL:<l:star/>
                            <bs:helpIcon iconTitle="Cloud URL, like 'https://www.netsparkercloud.com'"/>
                        </label>
                    </th>
                    <td>
                        <input type="text" name="netsparkerCloudServerURL" id="netsparkerCloudServerURL"
                               value="${pluginSettingsManager.pluginSettings.serverURL}" class="longField">
                        <span class="error" id="error_netsparkerCloudApiURL"></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="netsparkerCloudApiToken">API Token:<l:star/>
                            <bs:helpIcon iconTitle="It can be found at 'Your Account > API Settings' page in the Netsparker Cloud.<br/>
                         User must have 'Start Scans' permission for the target website."/>
                        </label>
                    </th>
                    <td>
                        <input type="password" name="netsparkerCloudApiToken" id="netsparkerCloudApiToken"
                               value="${pluginSettingsManager.random}"
                               class="longField textProperty"/>

                        <input type="hidden" id="netsparkerCloudEncryptedApiToken"
                               name="netsparkerCloudEncryptedApiToken"
                               value="${pluginSettingsManager.pluginSettings.encryptedApiToken}"/>

                        <input type="hidden" id="netsparkerCloudApiTokenInitial"
                               name="netsparkerCloudApiTokenInitialValue" value=""/>

                        <span class="error" id="error_netsparkerCloudApiToken"></span>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <a class="btn btn_hint" id="netsparkerCloudTestConnectionButton">Test Connection</a>
                        <span id="netsparkerCloudConnectionResult"></span>
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
                    ncServerURLInput = jQuery("#netsparkerCloudServerURL");
                    ncApiTokenInput = jQuery("#netsparkerCloudApiToken");
                    ncPublicKeyInput = jQuery("#publicKey");
                    ncApiTokenInitialValueInput = jQuery("#netsparkerCloudApiTokenInitial");
                    ncEncryptedApiTokenInput = jQuery("#netsparkerCloudEncryptedApiToken");

                    ncTestConnectionResultSpan = jQuery("#netsparkerCloudConnectionResult");
                    ncTestConnectionButton = jQuery("#netsparkerCloudTestConnectionButton");

                    ncTestConnectionButton.click(ncTestConnection);
                    ncServerURLInput.attr('placeholder', "Cloud URL, like 'https://www.netsparkercloud.com'");

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
                    ncTestRequestParams.netsparkerCloudServerURL = ncScanParams.serverURL;
                    ncTestRequestParams.netsparkerCloudApiToken = BS.Encrypt.encryptData(ncScanParams.apiToken, ncPublicKeyInput.val());
                    ncTestRequestParams.netsparkerCloudEncryptedApiToken = ncScanParams.encryptedApiToken;
                }

                function ncTestConnection() {
                    updateNcParams();
                    var request = jQuery.post("/netsparkercloud/testconnection.html", ncTestRequestParams);

                    request.done(function (data, statusText, xhr) {
                        var status = jQuery(data).find("httpStatusCode").text();
                        if (status == "200") {
                            ncTestConnectionResultSpan.text("Successfully connected to the Netsparker Cloud.");
                        } else {
                            if (status == "0") {
                                ncTestConnectionResultSpan.text("Failed to connect to the Netsparker Cloud. HTTP status code: 0");
                            } else {
                                ncTestConnectionResultSpan.text("Netsparker Cloud rejected the request. HTTP status code: " + status);
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