// Javascript methods used by uploadBackground.jsp (for Experiment Data File Uploads).

$(function() {
    if ($.browser.msie) {
        $("#ie_limitation_text").text("Limitation: The size of each file you upload must be less than 2 GB in Internet Explorer.");
    }
    if (navigator.userAgent.match(/windows/i) && navigator.userAgent.match(/safari/i)) {
        $("#upload0").removeAttr("multiple");
    }

});

function beginUpload() {
    window.onbeforeunload = confirmCloseWindow;
}

// This method is invoked from the "fileuploadstop" event in application.js
function onUploadDone() {
    document.getElementById("closeWindow").style.display = "block";
}

function closeAndGoToProjectData(projectDetailsUrl) {
    window.opener.location = projectDetailsUrl;
    window.close();
}

function confirmCloseWindow() {
    if (!processingFinished()) {
        return "Your files are currently uploading. Closing this window now could result in the upload being aborted.";
    }
}

function processingFinished() {
    var filesToBeUploaded = $("#fileupload .template-upload").length;
    return filesToBeUploaded == 0 ? true : false;
}