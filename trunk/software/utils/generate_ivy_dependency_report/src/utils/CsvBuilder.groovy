/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils

class CsvBuilder {

private StringBuffer content = new StringBuffer()

/**
* Constructors. Setting column names on a header row is optional.
*/
CsvBuilder(List colnames) {
append(colnames)
}

CsvBuilder() {
// without header row
}

/**
* Returns a field value escaped for special characters
* @param input A String to be evaluated
* @return A properly formatted String
*/
String escape(String input) {
String output = new String()

if (input.contains(",") || input.contains("\n") ||
(!input.trim().equals(input))) {
output = "\"${input}\""
} else if (input.contains("\"")) {
output = "\"${input.replace("\"","\"\"")}\""
} else {
output = input
}

return output
}

/**
* Appends a row of values to the output
* @param values A list of values
* @return this CsvBuffer instance
*/
CsvBuilder append(List values) {
values.eachWithIndex() {value, i ->
// Insert a comma to delimit each field after the first
if (i > 0) {
content.append(",")
}
if (value != null) {
content.append(escape(value.toString()))
} // else null becomes ',,' - an empty string
}
content.append("\r\n")
return this
}

/**
* Outputs the contents of the buffer.
* @return Buffer contents as a String
*/
String toString() {
return content.toString()
}

/**
* Outputs the contents of the buffer.
* @return Buffer contents as a byte array
*/
byte[] toByteArray() {
return content.toString().getBytes()
}

}