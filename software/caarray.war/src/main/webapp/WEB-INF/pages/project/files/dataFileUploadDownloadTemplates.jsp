<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<!-- This file contains the JQuery Upload and Download template scripts used in Experiment Data File Uploads UI (uploadBackground.jsp) -->
<script id="template-upload" type="text/html">
    {% for (var i=0, files=o.files, l=files.length, file=files[0]; i<l; file=files[++i]) {
            var match = file.name.match('\.zip$');
            var chkId = "checkbox" + l;
    %}
        <tr class="template-upload fade">
            <td>
                {% if (match) { %}
                    <input type="checkbox" id="checkbox0" name="selectedFilesToUnpack" value="0" checked="true"/>
                {% } else { %}
                    <input type="checkbox" id="checkbox0" name="selectedFilesToUnpack" value="0" disabled="true"/>
                {% } %}
                <label for="uploadForm_selectedFilesToUnpack" value="Unpack Compressed Archive" />
            </td>
            <td class="name">{%=file.name%}</td>
            <td class="size">{%=o.formatFileSize(file.size)%}</td>
            {% if (file.error) { %}
                <td class="error" colspan="2"><span class="label label-important">Error</span> {%=fileUploadErrors[file.error] || file.error%}</td>
            {% } else if (o.files.valid && !i) { %}
                <td><div class="progress progress-success progress-striped active"><div class="bar" style="width:0%;"></div></div></td>
                <td class="start">
                    {% if (!o.options.autoUpload) { %}<button class="btn btn-primary"><i class="icon-upload icon-white"></i>Start</button>{% } %}
                </td>
            {% } else { %}
                <td colspan="2"></td>
            {% } %}
            <td class="cancel">
                {% if (!i) { %}<button class="btn btn-warning"><i class="icon-ban-circle icon-white"></i>Cancel</button>{% } %}
            </td>
        </tr>
    {% } %}
</script>

<script id="template-download" type="text/html">
    {% for (var i=0, files=o.files, l=files.length, file=files[0]; i<l; file=files[++i]) { %}
        <tr class="template-download fade">
            <td class="name">{%=file.name%}</td>
            <td class="size">{%=o.formatFileSize(file.size)%}</td>
            {% if (file.error) { %}
                <td class="error" colspan="4"><span class="label important">Error</span> {%=fileUploadErrors[file.error] || file.error%}</td>
            {% } else { %}
                <td colspan="4"><div style="width: 24px; height: 24px;"><img src="/caarray/images/ok.png" width="100%" height="100%"/></div></td>
            {% } %}
        </tr>
    {% } %}
</script>