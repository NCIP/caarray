var DOCUMENTATION_TAG = "documentation";
var DESCRIPTION_TAG = "description";

var wshShell = WScript.CreateObject("WScript.Shell");
var repository = WScript.CreateObject("EA.Repository");

main();

function main() {
    repository = WScript.CreateObject("EA.Repository")
    var fileOpened = repository.OpenFile("C:\\ncicb_svn\\caarray2\\docs\\analysis_and_design\\models\\caarray_client_model.eap")
    if (!fileOpened) {
        alert("Unable to open model file");
        return;
    }
    updateAllClasses();
    repository.Exit();
    repository = null;
    alert("Done");
}

function updateAllClasses() {
    for (var i = 0; i < repository.Models.Count; i++) {
        updatePackage(repository.Models.GetAt(i));
    }
}

function updatePackage(package) {
    for (var i = 0; i < package.Packages.Count; i++) {
        updatePackage(package.Packages.GetAt(i));
    }
    updateClasses(package);
}

function updateClasses(package) {
    for (var i = 0; i < package.Elements.Count; i++) {
        updateClass(package.Elements.GetAt(i));
    }
}

function updateClass(element) {
    updateClassNotes(element);
    for (var i = 0; i < element.Attributes.Count; i++) {
        updateAttribute(element.Attributes.GetAt(i));
    }
}

function updateClassNotes(element) {
    if (hasTaggedValue(DOCUMENTATION_TAG, element)) {
        element.Notes = getTaggedValue(DOCUMENTATION_TAG, element);
    }
}

function updateAttribute(attribute) {
    if (hasTaggedValue(DESCRIPTION_TAG, attribute)) {
        attribute.Notes = getTaggedValue(DESCRIPTION_TAG, attribute);
    }
}

function hasTaggedValue(tagName, parent) {
    for (var i = 0; i < parent.TaggedValues.Count; i++) {
        var tag = parent.TaggedValues.GetAt(i);
        if (tag.Name == tagName) {
            return true;
        }
    }
    return false;
}

function getTaggedValue(tagName, parent) {
    for (var i = 0; i < parent.TaggedValues.Count; i++) {
        var tag = parent.TaggedValues.GetAt(i);
        if (tag.name == tagName) {
            return tag.Value;
        }
    }
    return null;
}

function alert(message) {
    wshShell.Popup(message);
}

function confirm(message) {
    return wshShell.Popup(message, 9999, "Confirm", 33) == 1;
}