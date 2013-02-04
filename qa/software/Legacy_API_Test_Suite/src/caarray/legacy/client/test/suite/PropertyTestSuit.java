//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package caarray.legacy.client.test.suite;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.TestUtils;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.PropertyUtils;


/**
 *
 * @author gax
 */
public class PropertyTestSuit extends ConfigurableTestSuite {

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR + File.separator + "Property.csv";
    private static final String CLASS = "Class";
    private static final String ID = "Id";
    private static final String PROPERTY = "Property";
    private static final String VALUE = "Value";
    private static final String[] COLUMN_HEADERS = new String[] {TEST_CASE,CLASS,ID,PROPERTY,VALUE};
    private List<TestResult> results = new ArrayList<TestResult>();

    public PropertyTestSuit(ApiFacade apiFacade)
    {
        super(apiFacade);
//        try {
//            System.out.println(org.apache.axis.description.ElementDesc.class.getProtectionDomain().getCodeSource().getLocation());
//            java.lang.reflect.Method m = org.apache.axis.description.ElementDesc.class.getDeclaredMethod("setNillable", boolean.class);
//            System.out.println(m.toGenericString());
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void executeConfiguredTests(TestResultReport resultReport) {
        for (TestResult r : results) {
            resultReport.addTestResult(r);
        }
    }

    @Override
    protected void constructSearches(List<String> spreadsheetRows) throws TestConfigurationException {
        int index = 1;
        String row = spreadsheetRows.get(index);
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();

        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            TestResult testResult = new TestResult();
            results.add(testResult);
            long now = System.currentTimeMillis();
            String testCase = null;
            try {
                String[] input = TestUtils.split(row, DELIMITER);
                testCase = input[headerIndexMap.get(TEST_CASE)];
                String className = input[headerIndexMap.get(CLASS)];
                String id = input[headerIndexMap.get(ID)];
                String propertyName = input[headerIndexMap.get(PROPERTY)];
                String value = input[headerIndexMap.get(VALUE)];
                Class c = Class.forName(className);
                Object entity = getObject(className, id);
                Object property = PropertyUtils.getProperty(entity, propertyName);
                if (property instanceof AbstractCaArrayObject) {
                    AbstractCaArrayObject o = (AbstractCaArrayObject) property;
                    property = o.getId();
                }
                if (value.equals(String.valueOf(property))) {
                    String detail = "Found expected value " + value;
                    testResult.addDetail(detail);
                    testResult.setPassed(true);
                } else {
                    String detail = "Expected value " + value + ", but found " + property;
                    testResult.addDetail(detail);
                    testResult.setPassed(false);
                }
                
            } catch (Exception ex) {
                Logger.getLogger(PropertyTestSuit.class.getName()).log(Level.SEVERE, null, ex);
                String detail = ex.toString();
                testResult.addDetail(detail);
                testResult.setPassed(false);
            } finally {
                index++;
                if (index < spreadsheetRows.size()) {
                    row = spreadsheetRows.get(index);
                } else {
                    row = null;
                }
                testResult.setElapsedTime(System.currentTimeMillis() - now);
                testResult.setTestCase(Float.parseFloat(testCase));
            }
        }
        
    }

    private Object getObject(String className, String id) throws Exception {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(className);
        cqlQuery.setTarget(target);
        target.setAttribute(new Attribute("id", Predicate.EQUAL_TO, id));
        CQLQueryResults cqlResults = (CQLQueryResults) super.apiFacade.query("grid", cqlQuery);
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));
        return iter.next();
    }

    @Override
    protected String getConfigFilename() {
        return CONFIG_FILE;
    }

    @Override
    protected String[] getColumnHeaders() {
        return COLUMN_HEADERS;
    }

    @Override
    protected String getType() {
        return "Property transmission";
    }

}
