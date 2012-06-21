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
package gov.nih.nci.caarray.test.api.legacy.java.data;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * A client downloading a full data set corresponding to one data file through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiOneFileDataSetDownload extends AbstractApiTest {
    private static final String[] EXPERIMENT_NAMES = {
        TestProperties.getAffymetricSpecificationName(),
        TestProperties.getAffymetricChpName(),
        TestProperties.getIlluminaRatName(),
        TestProperties.getGenepixCowName()
    };

    @Test
    public void testDownloadOneFileDataSet() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Downloading a full DataSet corresponding to one data file...");
            for (String experimentName : EXPERIMENT_NAMES) {
                logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + experimentName);
                getDataSetFromExperiment(searchService, experimentName);
            }
        } catch (ServerConnectionException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Server connection exception: " + e + "\nTrace: " + trace);
            assertTrue("Server connection exception: " + e, false);
        } catch (RuntimeException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Runtime exception: " + e + "\nTrace: " + trace);
            assertTrue("Runtime exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private void getDataSetFromExperiment(CaArraySearchService searchService, String experimentName) {
        Experiment experiment = lookupExperiment(searchService, experimentName);
        if (experiment != null) {
            Hybridization hybridization = getFirstHybridization(searchService, experiment);
            if (hybridization != null) {
                DataSet dataSet = getDataSet(searchService, hybridization);
                if (dataSet != null) {
                    int numValuesRetrieved = 0;
                    DesignElementList designElementList = dataSet.getDesignElementList();
                    DesignElementList populatedDesignElementList = searchService.search(designElementList).get(0);
                    // Get each HybridizationData in the DataSet.
                    for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
                        HybridizationData populatedHybData = searchService.search(oneHybData).get(0);
                        // Get each column in the HybridizationData.
                        for (AbstractDataColumn column : populatedHybData.getColumns()) {
                            AbstractDataColumn populatedColumn = searchService.search(column).get(0);
                            // Find the type of the column.
                            QuantitationType qType = populatedColumn.getQuantitationType();
                            Class typeClass = qType.getTypeClass();
                            // Retrieve the appropriate data depending on the type of the column.
                            if (typeClass == String.class) {
                                String[] values = ((StringColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else if (typeClass == Float.class) {
                                float[] values = ((FloatColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else if (typeClass == Short.class) {
                                short[] values = ((ShortColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else if (typeClass == Boolean.class) {
                                boolean[] values = ((BooleanColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else if (typeClass == Double.class) {
                                double[] values = ((DoubleColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else if (typeClass == Integer.class) {
                                int[] values = ((IntegerColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else if (typeClass == Long.class) {
                                long[] values = ((LongColumn) populatedColumn).getValues();
                                numValuesRetrieved += values.length;
                            } else {
                                // Should never get here.
                            }
                        }
                    }
                    logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + dataSet.getHybridizationDataList().size()
                            + " hybridization data elements, "
                            + populatedDesignElementList.getDesignElements().size() + " design elements of type "
                            + populatedDesignElementList.getDesignElementType() + ","
                            + dataSet.getQuantitationTypes().size() + " quantitation types and "
                            + numValuesRetrieved + " values.");
                    assertTrue((dataSet.getQuantitationTypes().size() > 0) && (numValuesRetrieved > 0));
                } else {
                    logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null data set.");
                    assertTrue("Error: Retrieved null data set.", false);
                }
            } else {
                logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null hybridization.");
                assertTrue("Error: Retrieved null hybridization.", false);
            }
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Could not find experiment.");
            assertTrue("Error: Could not find experiment.", false);
        }
    }

    private Experiment lookupExperiment(CaArraySearchService service, String experimentName) {
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);

        List<Experiment> experimentList = service.search(exampleExperiment);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Experiment)");
        if (experimentList.size() == 0) {
            return null;
        }
        Experiment experiment = experimentList.get(0);
        return experiment;
    }

    private Hybridization getFirstHybridization(CaArraySearchService service, Experiment experiment) {
        Set<Hybridization> allHybridizations = experiment.getHybridizations();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getHybridizations().size(): "
                + experiment.getHybridizations().size());
        for (Hybridization hybridization : allHybridizations) {
            Hybridization populatedHybridization = service.search(hybridization).get(0);
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Hybridization)");
            // Yes, we're returning only the first hybridization.
            return populatedHybridization;
        }
        return null;
    }

    private DataSet getDataSet(CaArraySearchService service, Hybridization hybridization) {
        DataSet dataSet = null;
        // Try to find raw data
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Hybridization.getArrayData().");
            for (RawArrayData rawArrayData : rawArrayDataSet) {
                // Return the data set associated with the first raw data.
                RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
                logForSilverCompatibility(API_CALL, "CaArraySearchService.search(RawArrayData)");
                dataSet = populatedArrayData.getDataSet();
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "RawArrayData.getDataSet().");
                break;
            }
        if (dataSet == null) {
            // If raw data doesn't exist, try to find derived data
            Set<DerivedArrayData> derivedArrayDataSet = hybridization.getDerivedDataCollection();
            for (DerivedArrayData derivedArrayData : derivedArrayDataSet) {
                // Return the data set associated with the first derived data.
                DerivedArrayData populatedArrayData = service.search(derivedArrayData).get(0);
                logForSilverCompatibility(API_CALL, "CaArraySearchService.search(DerivedArrayData)");
                dataSet = populatedArrayData.getDataSet();
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "DerivedArrayData.getDataSet().");
                break;
            }
        }
        if (dataSet == null) {
            return null;
        } else {
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(DataSet)");
            return service.search(dataSet).get(0);
        }
    }
}
