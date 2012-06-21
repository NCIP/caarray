import utils.CsvBuilder;
import groovy.xml.MarkupBuilder;

/*******************************************************
 File format - 0,jar name,sha1,jar download url ,GAV
 Example Input File:
 2,ApacheJMeter_core-2.3.jar,2056461d5080bb3fdfab3618a50906d138bc448c, http://gforge.nci.nih.gov/svnroot/commonlibrary/trunk/ivy-repo/apache/jmeter/2.3/ApacheJMeter_core-2.3.jar, apache#jmeter;2.3!ApacheJMeter_core.jar
 3,ant-jmeter-2.3.jar,af3050a1dde074df4307a6e8730ae531e976e6d6, http://gforge.nci.nih.gov/svnroot/commonlibrary/trunk/ivy-repo/apache/jmeter/2.3/ant-jmeter-2.3.jar, apache#jmeter;2.3!ant-jmeter.jar
 Example Output File:
 2, ApacheJMeter_core-2.3.jar, 2056461d5080bb3fdfab3618a50906d138bc448c, group#artifact;version!repositoryUri, group#artifact;version!repositoryUri, group#artifact;version!repositoryUri
 3, ant-jmeter-2.3.jar,af3050a1dde074df4307a6e8730ae531e976e6d6, group#artifact;version!repositoryUri 
 *******************************************************/
def inputFile = "src/ivydependency/input/caArrayIvySha1.csv";
def ivyGavAnalysisCsvFileName = "src/ivydependency/output/caArrayIvy_Gav_Analysis.csv";
def missingJarsCsvFileName = "src/ivydependency/output/caArrayIvy_Missing_Jars.csv";
def nexusRootUrl = "https://ncimvn.nci.nih.gov/nexus/service/local/data_index?sha1=";

def CsvBuilder analysisData = new CsvBuilder();

def headers = ["Jar Name", "SHA-1 Checksum", "Preferred GAV Coordinates", "Artifact Type", "POM Included/Available", "Location of Artifact"];
def CsvBuilder missingJarsData = new CsvBuilder(headers);

println("Parsing the input file.............");
new File(inputFile).splitEachLine(",") { fields ->
	def analysisColumnValues = [];
	def missingJarsColumnValues = [];

	def rowCount = fields[0];
	def jarName = fields[1];
	def sha1 = fields[2];
	def jarIvyUrl = fields[3].trim();
	def jarIvyGav = fields[4];

	def requestUrl = (nexusRootUrl +sha1).toURL().text;
	def root = new XmlParser().parseText(requestUrl);
	
	def uniqueGAVs = [] as Set;
	root.data.artifact.each {
		uniqueGAVs.add("${it.groupId.text()}#${it.artifactId.text()};${it.version.text()}");
	}
	
	def gavValues = uniqueGAVs.join("---");
	
	// Analysis CSV File
	analysisColumnValues.add(rowCount);
	analysisColumnValues.add(jarName);
	analysisColumnValues.add(sha1);
	analysisColumnValues.addAll(gavValues);
	analysisData.append(analysisColumnValues);
	
	// Missing Jars CSV File - gavValues is false is string is blank/null.
	if (!gavValues) { 
		missingJarsColumnValues.add(jarName);
		missingJarsColumnValues.add(sha1);
		missingJarsColumnValues.add(getPreferredGAV(jarIvyGav));
		missingJarsColumnValues.add("Jar");
		missingJarsColumnValues.add("N");
		missingJarsColumnValues.add(jarIvyUrl);
		
		missingJarsData.append(missingJarsColumnValues);
	}
}

// Write out the analysis csv.
def File fileIvyGavAnalysisCsvFile = new File(ivyGavAnalysisCsvFileName);
fileIvyGavAnalysisCsvFile.write(analysisData.toString());

// Write out the missing jars csv.
def File fileMissingJarsCsvFile = new File(missingJarsCsvFileName);
fileMissingJarsCsvFile.write(missingJarsData.toString());

println("Done. Generated the output files.................");

def getPreferredGAV(jarIvyGav) {
	def groupId = jarIvyGav.split("#")[0].trim();
	def artifactId = jarIvyGav.split("#")[1].split(";")[0].trim();
	def version = jarIvyGav.split("#")[1].split(";")[1].split("!")[0].trim();
	return "<dependency><groupId>${groupId}</groupId><artifactId>${artifactId}</artifactId><version>${version}</version></dependency>";
}