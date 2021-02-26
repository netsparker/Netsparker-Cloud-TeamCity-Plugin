<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>
    <p id="netsparkerScanResultWarning"></p>
    <iframe id="netsparkerScanResult" style="display:none;width:100%;height:100vh;" srcdoc="${content}"></iframe>
    <script>
        jQuery(document).ready(function () {
            var isReportGenerated = "${isReportGenerated}";
            var hasError = "${hasError}";
            var content = jQuery('#netsparkerScanResultContent').html();
            var errorMessage = "${errorMessage}";
            var warning = jQuery('#netsparkerScanResultWarning');

            if (hasError == 'true') {
                warning.html(errorMessage);
                jQuery('#netsparkerScanResult').hide();
                warning.show();
            }
            else if (isReportGenerated == 'true') {
                jQuery('#netsparkerScanResult').show();
                warning.hide();
            } else {
                warning.html(content);
                jQuery('#netsparkerScanResult').hide();
                warning.show();
            }
        });
    </script>
</div>