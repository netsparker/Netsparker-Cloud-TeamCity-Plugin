<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>
    <p id="netsparkerScanResultWarning"></p>
    <iframe id="netsparkerScanResult" style="display:none;width:100%;height:70vh;"></iframe>
    <script id="netsparkerScanResultContent" type="text/html">
        ${content}
    </script>
    <script>
    jQuery(document).ready(function () {
        var isReportGenerated =  ${isReportGenerated};
        var content=jQuery('#netsparkerScanResultContent').html();
        var hasError=${hasError};
        var errorMessage="${errorMessage}";
        var warning=jQuery('#netsparkerScanResultWarning');
        var iframe = document.getElementById('netsparkerScanResult');


        if(hasError){
            warning.text(errorMessage);
            jQuery('#netsparkerScanResult').hide();
            warning.show();
        }
        else if(isReportGenerated){
            iframe = iframe.contentWindow || ( iframe.contentDocument.document || iframe.contentDocument);
            iframe.document.open();
            iframe.document.write(content);
            iframe.document.close();
            jQuery('#netsparkerScanResult').show();
            warning.hide();
        }else{
            warning.text(content);
            jQuery('#netsparkerScanResult').hide();
            warning.show();
        }
    });
    </script>
</div>

