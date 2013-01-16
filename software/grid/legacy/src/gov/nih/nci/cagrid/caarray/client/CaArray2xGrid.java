//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.client;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;

import java.io.File;
import java.io.FileWriter;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CaArray2xGrid extends TestCase {

    private static Log LOG = LogFactory.getLog(CaArray2xGrid.class);

    public CaArray2xGrid() {
        super();
    }

    public CaArray2xGrid(String string) {
        super(string);
    }

    @Override
    public void setUp() {

        /*QA URL*/
//        System.setProperty("test.serviceUrl", "http://cbvapp-q1001.nci.nih.gov:59580/wsrf/services/cagrid/CaArraySvc");
        /*DEV URL*/
//        System.setProperty("test.serviceUrl", "http://cbvapp-d1002.nci.nih.gov:59580/wsrf/services/cagrid/CaArraySvc");
        /*DEV URL*/
//        System.setProperty("test.serviceUrl", "http://cbvapp-d1002.nci.nih.gov:59580/wsrf/services/cagrid/CaArraySvc");
        /*Local URL*/
        System.setProperty("test.serviceUrl", "http://localhost:8080/wsrf/services/cagrid/CaArraySvc");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new CaArray2xGrid("testFindAllFiles"));
        suite.addTest(new CaArray2xGrid("testFindAllFactors"));
        suite.addTest(new CaArray2xGrid("testFindAllOrganizations"));
        suite.addTest(new CaArray2xGrid("testFindAllAddresses"));
        suite.addTest(new CaArray2xGrid("testFindAllCategorys"));
        suite.addTest(new CaArray2xGrid("testFindAllExperiments"));
        suite.addTest(new CaArray2xGrid("testFindAllProjects"));
        suite.addTest(new CaArray2xGrid("testFindAllAbstractBioMaterials"));
        suite.addTest(new CaArray2xGrid("testFindAllHybridizations"));
        suite.addTest(new CaArray2xGrid("testFindAllRawArrayData"));
        suite.addTest(new CaArray2xGrid("testFindAllArrayDesigns"));
        return suite;
    }

    public void testFindAllOrganizations() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.contact.Organization");
    }

    public void testFindAllFiles() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.file.CaArrayFile");
    }

    public void testFindAllAddresses() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.contact.Address");
    }

    public void testFindAllFactors() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.project.Factor");
    }

    public void testFindAllCategorys() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.vocabulary.Category");
    }

    public void testFindAllProjects() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.project.Project");
    }

    public void testFindAllExperiments() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.project.Experiment");
    }

    public void testFindAllAbstractBioMaterials() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.sample.AbstractBioMaterial");
    }

    public void testFindAllHybridizations() throws Exception {
        findAllHelper(Hybridization.class.getName());
    }

    public void testFindAllRawArrayData() throws Exception {
        findAllHelper(RawArrayData.class.getName());
    }

    public void testFindAllArrayDesigns() throws Exception {
        findAllHelper(ArrayDesign.class.getName());
    }

    private void findAllHelper(String target) throws Exception {
        CQLQuery query = new CQLQuery();
        query.setTarget(new gov.nih.nci.cagrid.cqlquery.Object());
        query.getTarget().setName(target);
        CQLQueryResults results = executeCQLQuery(query);
        printResults(results, "findAll." + target + ".xml");
    }

    public void testDomainModel() throws Exception {

    }

    public static CQLQueryResults executeCQLQuery(CQLQuery query) {
        try {
            DataServiceClient client = new DataServiceClient(System.getProperty("test.serviceUrl"));

            CQLQueryResults cqlQueryResult = client.query(query);
            return cqlQueryResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected synchronized long printResults(CQLQueryResults results, String outFileName) {
        try {
            // StringWriter w = new StringWriter();
            String fileName = "test/logs/" + outFileName;
            new File(fileName).delete();
            FileWriter w = new FileWriter(fileName);
            Utils.serializeObject(results, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLResultSet",
                    "CQLResultSet"), w);
            w.flush();
            w.close();
            long fileSize = new File(fileName).length();

            LOG.debug("... done printing results to : " + outFileName + " size=" + fileSize + " bytes");
            return fileSize;
        } catch (Exception ex) {
            throw new RuntimeException("Error printing results: " + ex.getMessage(), ex);
        }
    }
}
