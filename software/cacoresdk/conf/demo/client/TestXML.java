import gov.nih.nci.system.applicationservice.*;
import java.util.*;


import gov.nih.nci.cabio.domain.*;
import gov.nih.nci.cabio.domain.impl.*;
import gov.nih.nci.common.util.*;

import org.hibernate.criterion.*;
import javax.xml.parsers.*;

import javax.xml.validation.*;
import javax.xml.transform.*;
import java.io.*;

import javax.xml.transform.dom.*;
import org.xml.sax.*;

import javax.xml.validation.*;
import javax.xml.*;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

import java.io.*;

/**
 * <!-- LICENSE_TEXT_START -->
* Copyright 2001-2004 SAIC. Copyright 2001-2003 SAIC. This software was developed in conjunction with the National Cancer Institute,
* and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
* in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
* materials provided with the distribution.
* 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
* "This product includes software developed by the SAIC and the National Cancer Institute."
* If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
* wherever such third-party acknowledgments normally appear.
* 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
* 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
* the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
* 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
* SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author caBIO Team
 * @version 1.0
 */



/**
	* TestClient.java demonstartes various ways to execute searches with and without
      * using Application Service Layer (convenience layer that abstracts building criteria
      * Uncomment different scenarios below to demonstrate the various types of searches
*/

public class TestXML {


    public static void main(String[] args) {

       System.out.println("*** XML TestClient...");
	 try{
ApplicationService appService = ApplicationServiceProvider.getRemoteInstance(
//"http://cbiodev104.nci.nih.gov:29080/cacore30/server/HTTPServer"
//"http://cbioqa101.nci.nih.gov:29080/cacore30/server/HTTPServer"
//"http://cbioapp101.nci.nih.gov:29080/cacore30/server/HTTPServer"
//http://cabio-stage.nci.nih.gov/cacore30/server/HTTPServer"
);

try {
	System.out.println("Scenario 1: Retrieving a Gene based on a Gene id.");
	Gene gene = new Gene();
	gene.setId(new Long("2"));

	try {
		XMLUtility myUtil = new XMLUtility();
		List resultList = appService.search(Gene.class, gene);
		System.out.println("Result list size: " + resultList.size() + "\n");
		long startTime = System.currentTimeMillis();
		for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) {
			Gene returnedGene = (Gene)resultsIterator.next();
			System.out.println("Gene object right after search: \n\n");
			System.out.println("   Id: "+ returnedGene.getId() + "\n");
			System.out.println("   Title: "+ returnedGene.getTitle() + "\n");
			System.out.println("   Symbol: "+ returnedGene.getSymbol() + "\n");
			System.out.println("   LocusLinkId: "+ returnedGene.getLocusLinkId() + "\n\n\n");
			File myFile = new File("@CLIENT_DIR@/test.xml");
			FileWriter myWriter = new FileWriter(myFile);
			myUtil.toXML(returnedGene,myWriter);
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = parser.parse(myFile);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(new File("@SCHEMA_DIR@/gov.nih.nci.cabio.domain.xsd"));
			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			System.out.println("Validating gene against the schema......\n\n");
			validator.validate(new DOMSource(document));
			System.out.println("Gene has been validated!!!\n\n");
			Gene myGene = (Gene) myUtil.fromXML(myFile);
			System.out.println("Retrieving gene from xml ....\n\n");
			System.out.println("   Id: "+ myGene.getId() + "\n");
			System.out.println("   Title: "+ myGene.getTitle() + "\n");
			System.out.println("   Symbol: "+ myGene.getSymbol() + "\n");
			System.out.println("   LocusLinkId: "+ myGene.getLocusLinkId() + "\n\n\n");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("latency in miliseconds = "+ (endTime - startTime));

	} catch (ParserConfigurationException ea) {
		ea.printStackTrace();
	} catch (SAXException eb) {
		eb.printStackTrace();
	} catch (IOException ec) {
		ec.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
} catch (RuntimeException e2) {
	e2.printStackTrace();
}


}
catch(Exception ex){
ex.printStackTrace();
}
}
}