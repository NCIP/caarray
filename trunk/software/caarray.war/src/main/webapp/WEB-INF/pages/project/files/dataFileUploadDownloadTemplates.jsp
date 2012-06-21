<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<!-- This file contains the JQuery Upload and Download template scripts used in Experiment Data File Uploads UI (uploadBackground.jsp) -->
<script id="template-upload" type="text/html">
    {% for (var i=0, files=o.files, numFiles=files.length; i<numFiles; i++) {
            var file = files[i];
            var isArchive = file.name.match('\.zip$');
    %}
        <tr class="template-upload fade">
            <td>
                {% if (isArchive) {
                %}
                    <input type="checkbox" name="selectedFilesToUnpack" value="{%=file.name%}" checked="true"/>
                    <label for="uploadForm_selectedFilesToUnpack" value="Unpack Compressed Archive">Unpack Archive?</label>
                {% } else { %}
                    &nbsp
                {% } %}
            </td>
            <td class="name">
                {%=file.name%}
            </td>
            <td class="size">
                {%=o.formatFileSize(file.size)%}
            </td>
            {% if (file.error) { %}
                <td class="error" colspan="2">
                    <span class="label label-important">Error</span> {%=fileUploadErrors[file.error] || file.error%}
                </td>
            {% } else if (o.files.valid && i !== undefined) { %}
                <td>
                    <div class="progress progress-success progress-striped active">
                        <div class="bar" style="width:0%;"></div>
                    </div>
                </td>
                <td class="start" style="display:none">
                    {% if (!o.options.autoUpload) { %}
                        <button class="btn btn-gray"><i class="icon-upload icon-white"></i>Start</button>
                    {% } %}
                </td>
            {% } else { %}
                <td colspan="2"></td>
            {% } %}
            <td class="cancel">
                <button class="btn btn-gray"><i class="icon-ban-circle icon-white"></i>Cancel</button>
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