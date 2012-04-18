/*
 * jQuery File Upload Plugin JS Example 6.0.3
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/*jslint nomen: true, unparam: true, regexp: true */
/*global $, window, document */

$(function () {
    'use strict'; //What is this?

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
        maxChunkSize : 1500000, // ~1.5 MB
        add: function (e, data) {
            var that = this;
            var uploadCheckAction = $('#fileupload').prop('action').replace('upload.action','partialUploadCheck.action');
            var params = {
                    chunkedFileName: data.files[0].name,
                    chunkedFileSize: data.files[0].size,
                    'project.id': $('#uploadProjectId').val()
            };
            $.getJSON(uploadCheckAction, params, function (file) {
                data.uploadedBytes = file && file.size;
                $.blueimpUI.fileupload.prototype
                    .options.add.call(that, e, data);
            });
        }
    });

    // Load existing files:
    $.getJSON($('#fileupload').prop('action'), function (files) {
        var fu = $('#fileupload').data('fileupload'), template;
        fu._adjustMaxNumberOfFiles(-files.length);
        template = fu._renderDownload(files)
            .appendTo($('#fileupload .files'));
        // Force reflow:
        fu._reflow = fu._transition && template.length &&
            template[0].offsetWidth;
        template.addClass('in');
    });

    // Enable iframe cross-domain access via redirect page:
    var redirectPage = window.location.href.replace(
        /\/[^\/]*$/,
        '/cors/result.html?%s'
    );
    $('#fileupload').bind('fileuploadsend', function (e, data) {
        if (data.dataType.substr(0, 6) === 'iframe') {
            var target = $('<a/>').prop('href', data.url)[0];
            if (window.location.host !== target.host) {
                data.formData.push({
                    name: 'redirect',
                    value: redirectPage
                });
            }
        }
    });

    $('#fileupload').bind('fileuploadstop', function (e, data) {
        onUploadDone();
    });

    // Open download dialogs via iframes,
    // to prevent aborting current uploads:
    $('#fileupload .files').delegate(
        'a:not([rel^=gallery])',
        'click',
        function (e) {
            e.preventDefault();
            $('<iframe style="display:none;"></iframe>')
                .prop('src', this.href)
                .appendTo(document.body);
        }
    );

});