var VALUE_DOMAIN_TAG = "CADSR Local Value Domain";
var REVIEWED_TAG = "CURATOR_REVIEWED";
var ENUMERATED_TAG = "CADSR_ValueDomainType";

var wshShell = WScript.CreateObject("WScript.Shell");
var fso;
var outputFile;
var repository;

main();

function main() {
    fso = WScript.CreateObject("Scripting.FileSystemObject");
    outputFile = fso.CreateTextFile("C:\\ncicb_svn\\caarray2\\docs\\implementation\\caarray_silver_compatibility_review\\08. caarray_value_domain_report.csv", true);
    repository = WScript.CreateObject("EA.Repository")
    var fileOpened = repository.OpenFile("C:\\ncicb_svn\\caarray2\\docs\\analysis_and_design\\models\\caarray_client_model.eap")
    writeHeaders();
    writeAllPackages();
    outputFile.Close();
    repository.Exit();
    repository = null;
    alert("Done");
}

function alert(message) {
    wshShell.Popup(message);
}

function confirm(message) {
    return wshShell.Popup(message, 9999, "Confirm", 33) == 1;
}

function writeHeaders() {
    outputFile.WriteLine("UML Class,UML Entity,Data Type,Verified,Enumerated,Valid Values,Comments/Definition");
}

function writeAllPackages() {
    for (var i = 0; i < repository.Models.Count; i++) {
        writePackage(repository.Models.GetAt(i));
    }
}

function writePackage(package) {
    for (var i = 0; i < package.Packages.Count; i++) {
        writePackage(package.Packages.GetAt(i));
    }
    writeClasses(package);
}

function writeClasses(package) {
    for (var i = 0; i < package.Elements.Count; i++) {
        writeClass(package, package.Elements.GetAt(i));
    }
}

function writeClass(package, element) {
    for (var i = 0; i < element.Attributes.Count; i++) {
        writeAttribute(package, element, element.Attributes.GetAt(i));
    }
}

function writeAttribute(package, element, attribute) {
    var attributeType = getAttributeType(attribute);
    var verifiedValue = getVerifiedValue(attribute);
    var enumeratedValue = getEnumeratedValue(attribute);

    outputFile.Write(package.Name + "." + element.Name);
    outputFile.Write(",");
    outputFile.Write(attribute.Name);
    outputFile.Write(",");
    outputFile.Write(attributeType);
    outputFile.Write(",");
    outputFile.Write("" + verifiedValue);
    outputFile.Write(",");
    outputFile.Write("" + enumeratedValue);
    outputFile.Write(",");
    if (enumeratedValue == 1) {
        outputFile.Write(getEnumeratedValues(attribute));
    }
    outputFile.Write(",");
    outputFile.WriteLine();
}

function getEnumeratedValues(attribute) {
    var valueDomain = getValueDomainClass(attribute);
    var result = "\"";
    for (var i = 0; i < valueDomain.Attributes.Count; i++) {
        var nextAttribute = valueDomain.Attributes.GetAt(i);
        if (i > 0) {
            result += ", ";
        }
        result += nextAttribute.Name;
    }
    result += "\"";
    return result;
}

function getAttributeType(attribute) {
    if (hasTaggedValue(VALUE_DOMAIN_TAG, attribute)) {
        return getTaggedValue(VALUE_DOMAIN_TAG, attribute);
    } else {
        return attribute.Type;
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

function getVerifiedValue(attribute) {
    return getTaggedValue(REVIEWED_TAG, attribute);
}

function getEnumeratedValue(attribute) {
    if (hasTaggedValue(VALUE_DOMAIN_TAG, attribute)) {
        var valueDomainClass = getValueDomainClass(attribute);
        return getTaggedValue(ENUMERATED_TAG, valueDomainClass) == "E" ? 1 : 0;
    } else {
        return 0;
    }
}

function getValueDomainClass(attribute) {
    return getClassByName(getTaggedValue(VALUE_DOMAIN_TAG, attribute));
}

function getClassByName(className) {
    for (var i = 0; i < repository.Models.Count; i++) {
        var theClass = getClassByNameFromPackage(repository.Models.GetAt(i), className);
        if (theClass != null) {
            return theClass;
        }
    }
    return null;
}

function getClassByNameFromPackage(package, className) {
    for (var i = 0; i < package.Elements.Count; i++) {
        var element = package.Elements.GetAt(i);
        if (element.Name == className) {
            return element;
        }
    }
    for (var i = 0; i < package.Packages.Count; i++) {
        var subpackage = package.Packages.GetAt(i);
        var element = getClassByNameFromPackage(subpackage, className);
        if (element != null) {
            return element;
        }
    }
    return null;
}
