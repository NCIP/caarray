/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The test
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This test Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the test Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the test Software; (ii) distribute and
 * have distributed to and by third parties the test Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.test.api.legacy.grid.data;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.test.api.legacy.grid.AbstractLegacyGridApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;
import java.util.Set;

import org.junit.Test;

/**
 * A client downloading a data set through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridDataSetDownload extends AbstractLegacyGridApiTest {
    private static final String[] EXPERIMENT_NAMES = {
        TestProperties.getAffymetricSpecificationName(),
        TestProperties.getAffymetricChpName(),
        TestProperties.getIlluminaRatName(),
        TestProperties.getGenepixCowName()
    };
    private static final String[] QUANTITATION_TYPES_CSV_STRINGS = {
        TestProperties.AFFYMETRIX_CEL_QUANTITATION_TYPES,
        TestProperties.AFFYMETRIX_CHP_QUANTITATION_TYPES,
        TestProperties.ILLUMINA_QUANTITATION_TYPES,
        TestProperties.GENEPIX_QUANTITATION_TYPES
    };

    @Test
    public void testDownloadDataSet() {
        try {
            logForSilverCompatibility(TEST_NAME, "Grid-Downloading a DataSet...");
            int i = 0;
            for (String experimentName : EXPERIMENT_NAMES) {
                String quantitationTypesCsv = QUANTITATION_TYPES_CSV_STRINGS[i++];

                downloadDataSet(experimentName, quantitationTypesCsv);
            }
        } catch (RemoteException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Remote exception: " + e + "\nTrace: " + trace);
            assertTrue("Remote exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private void downloadDataSet(String experimentName, String quantitationTypesCsv)
    throws RemoteException {
        logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + experimentName);
        DataRetrievalRequest request = new DataRetrievalRequest();
        lookupExperiment(request, experimentName);
        lookupQuantitationTypes(request, quantitationTypesCsv);

        DataSet dataSet = gridClient.getDataSet(request);
        logForSilverCompatibility(API_CALL, "Grid getDataSet(DataRetrievalRequest)");
        int numValuesRetrieved = 0;
        if (dataSet == null) {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null DataSet.");
            assertTrue("Error: Retrieved null DataSet.", false);
        }

        // Get each HybridizationData in the DataSet.
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "DataSet.getHybridizationDataList().size(): "
                + dataSet.getHybridizationDataList().size());
        for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
            // Get each column in the HybridizationData.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "HybridizationData.getColumns().size(): "
                    + oneHybData.getColumns().size());
            for (AbstractDataColumn column : oneHybData.getColumns()) {
                // Find the type of the column.
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "AbstractDataColumn.getQuantitationType().getName(): "
                        + column.getQuantitationType().getName());
                QuantitationType qType = column.getQuantitationType();
                Class<?> typeClass = qType.getTypeClass();
                // Retrieve the appropriate data depending on the type of the column.
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "AbstractDataColumn.getValues().");
                if (typeClass == String.class) {
                    String[] values = ((StringColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Float.class) {
                    float[] values = ((FloatColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Short.class) {
                    short[] values = ((ShortColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Boolean.class) {
                    boolean[] values = ((BooleanColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Double.class) {
                    double[] values = ((DoubleColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Integer.class) {
                    int[] values = ((IntegerColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Long.class) {
                    long[] values = ((LongColumn) column).getValues();
                    numValuesRetrieved += values.length;
                } else {
                    // Should never get here.
                }
            }
        }
        logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + dataSet.getHybridizationDataList().size()
                + " hybridization data elements, " + dataSet.getQuantitationTypes().size() + " quantitation types and "
                + numValuesRetrieved + " values.");
        assertTrue((dataSet.getHybridizationDataList().size() > 0) && (dataSet.getQuantitationTypes().size() > 0)
                && (numValuesRetrieved > 0));
        assertTrue((dataSet.getHybridizationDataList().size() > 0) && (dataSet.getQuantitationTypes().size() > 0));

    }

    private void lookupExperiment(DataRetrievalRequest request, String experimentName)
    throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");
        Attribute titleAttribute = new Attribute();
        titleAttribute.setName("title");
        titleAttribute.setValue(experimentName);
        titleAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(titleAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid query(CQLQuery)");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return;
        }
        Experiment experiment = (Experiment) iter.next();
        Set<Hybridization> allHybs = experiment.getHybridizations();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getHybridizations().size(): "
                + experiment.getHybridizations().size());
        if (allHybs.isEmpty()) {
            return;
        }
        request.getHybridizations().addAll(allHybs);
    }

    private void lookupQuantitationTypes(DataRetrievalRequest request, String quantitationTypesCsv)
            throws RemoteException {
        String[] quantitationTypeNames = quantitationTypesCsv.split(",");
        if (quantitationTypeNames == null) {
            return;
        }

        // Locate each quantitation type and add it to the request.
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.data.QuantitationType");
        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("name");
        nameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(nameAttribute);
        cqlQuery.setTarget(target);
        for (int i = 0; i < quantitationTypeNames.length; i++) {
            String quantitationTypeName = quantitationTypeNames[i];
            nameAttribute.setValue(quantitationTypeName);
            CQLQueryResults cqlResults = gridClient.query(cqlQuery);
            logForSilverCompatibility(API_CALL, "Grid query(CQLQuery)");
            CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                    .getResourceAsStream("client-config.wsdd"));
            if (!(iter.hasNext())) {
                continue;
            }
            QuantitationType quantitationType = (QuantitationType) iter.next();
            request.getQuantitationTypes().add(quantitationType);
        }
    }
}
