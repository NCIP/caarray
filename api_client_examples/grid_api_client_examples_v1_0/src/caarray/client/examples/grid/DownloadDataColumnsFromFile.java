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
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.BooleanColumn;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.DesignElement;
import gov.nih.nci.caarray.external.v1_0.data.DoubleColumn;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FloatColumn;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.IntegerColumn;
import gov.nih.nci.caarray.external.v1_0.data.LongColumn;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.data.ShortColumn;
import gov.nih.nci.caarray.external.v1_0.data.StringColumn;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.axis.types.URI.MalformedURIException;

/**
 * A client downloading columns from a data file using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadDataColumnsFromFile {
    private static CaArraySvc_v1_0Client client = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String QUANTITATION_TYPES_CSV_STRING = BaseProperties.AFFYMETRIX_CHP_QUANTITATION_TYPES;

    public static void main(String[] args) {
        DownloadDataColumnsFromFile downloader = new DownloadDataColumnsFromFile();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            System.out.println("Downloading data columns from a file in the experiment " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading data columns from a file.");
            t.printStackTrace();
        }
    }

    private void download() throws RemoteException, MalformedURIException, IOException, Exception {
        DataSetRequest dataSetRequest = new DataSetRequest();
        // Select an experiment of interest.
        CaArrayEntityReference experimentRef = selectExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find any experiment with the requested criteria.");
            return;
        }

        // Select data files of interest.
        CaArrayEntityReference fileRef = selectDataFile(experimentRef);
        if (fileRef == null) {
            System.err.println("Could not find any files that match the search criteria.");
            return;
        }
        dataSetRequest.getDataFiles().add(fileRef);

        // Select the quantitation types (columns) of interest.
        Set<CaArrayEntityReference> quantitationTypeRefs = selectQuantitationTypes();
        if (quantitationTypeRefs == null) {
            System.err.println("Could not find one or more of the requested quantitation types: "
                    + QUANTITATION_TYPES_CSV_STRING);
            return;
        }
        dataSetRequest.setQuantitationTypes(quantitationTypeRefs);

        // Retrieve the parsed data set.
        DataSet dataSet = client.getDataSet(dataSetRequest);
        if (dataSet == null) {
            System.err.println("Retrieved null data set.");
            return;
        }

        // Ordered list of row headers (probe sets)
        List<DesignElement> probeSets = dataSet.getDesignElements();
        printProbeSets(probeSets);
        // Ordered list of column headers (quantitation types like Signal, Log Ratio etc.)
        List<QuantitationType> quantitationTypes = dataSet.getQuantitationTypes();
        // Data for the first hybridization (the only hybridization, in our case)
        HybridizationData data = dataSet.getDatas().get(0);
        // Ordered list of columns with values (columns are in the same order as column headers/quantitation types)
        List<AbstractDataColumn> dataColumns = data.getDataColumns();
        Iterator columnIterator = dataColumns.iterator();
        for (QuantitationType quantitationType : quantitationTypes) {
            System.out.println("Column = " + quantitationType.getName() + "; Data type = "
                    + quantitationType.getDataType());
            AbstractDataColumn dataColumn = (AbstractDataColumn) columnIterator.next();
            // Ordered list of values in the column (values are in the same order as row headers/probe sets)
            printColumnValues(quantitationType, dataColumn);
        }
    }

    /**
     * Search for experiments and select one.
     */
    private CaArrayEntityReference selectExperiment() throws RemoteException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        Experiment[] experiments = client.searchForExperiments(experimentSearchCriteria);
        if (experiments == null || experiments.length <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments[0];
        return experiment.getReference();
    }

    /**
     * Search for data files of a certain type in the experiment and select one.
     */
    private CaArrayEntityReference selectDataFile(CaArrayEntityReference experimentRef) throws RemoteException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        // Search for all AFFYMETRIX_CHP data files in the experiment.
        CaArrayEntityReference chpFileTypeRef = getChpFileType();
        fileSearchCriteria.getTypes().add(chpFileTypeRef);

        DataFile[] files = client.searchForFiles(fileSearchCriteria);
        if (files == null || files.length <= 0) {
            return null;
        }

        // The client application will typically let the user choose one out of the many files,
        // but we will just pick the first result here.
        DataFile file = files[0];
        return file.getReference();
    }

    private CaArrayEntityReference getChpFileType() throws RemoteException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        exampleFileType.setName("AFFYMETRIX_CHP");
        criteria.setExample(exampleFileType);
        List<FileType> fileTypes = (client.searchByExample(criteria)).getResults();
        FileType chpFileType = fileTypes.iterator().next();
        return chpFileType.getReference();
    }

    private Set<CaArrayEntityReference> selectQuantitationTypes() throws RemoteException {
        ExampleSearchCriteria<QuantitationType> criteria = new ExampleSearchCriteria<QuantitationType>();
        Set<CaArrayEntityReference> quantitationTypeRefs = new HashSet<CaArrayEntityReference>();
        String[] quantitationTypeNames = QUANTITATION_TYPES_CSV_STRING.split(",");
        for (String quantitationTypeName : quantitationTypeNames) {
            QuantitationType exampleQuantitationType = new QuantitationType();
            exampleQuantitationType.setName(quantitationTypeName);
            criteria.setExample(exampleQuantitationType);
            List<QuantitationType> quantitationTypes = (client.searchByExample(criteria)).getResults();
            if (quantitationTypes == null || quantitationTypes.size() <= 0) {
                return null;
            }
            QuantitationType quantitationType = quantitationTypes.iterator().next();
            quantitationTypeRefs.add(quantitationType.getReference());
        }
        return quantitationTypeRefs;
    }

    private void printProbeSets(List<DesignElement> probeSets) {
        System.out.print("Probe Sets: ");
        for (DesignElement probeSet : probeSets) {
            System.out.print(probeSet.getName() + " ");
        }
        System.out.println();
    }

    private void printColumnValues(QuantitationType quantitationType, AbstractDataColumn dataColumn) {
        // Extract individual values in the column according to its type.
        DataType columnDataType = quantitationType.getDataType();
        switch (columnDataType) {
        case BOOLEAN:
            boolean[] booleanValues = ((BooleanColumn) dataColumn).getValues();
            System.out.println("Retrieved " + booleanValues.length + " boolean values.");
            break;
        case INTEGER:
            int[] intValues = ((IntegerColumn) dataColumn).getValues();
            System.out.println("Retrieved " + intValues.length + " int values.");
            break;
        case DOUBLE:
            double[] doubleValues = ((DoubleColumn) dataColumn).getValues();
            System.out.println("Retrieved " + doubleValues.length + " double values.");
            break;
        case FLOAT:
            float[] floatValues = ((FloatColumn) dataColumn).getValues();
            System.out.println("Retrieved " + floatValues.length + " float values.");
            break;
        case SHORT:
            short[] shortValues = ((ShortColumn) dataColumn).getValues();
            System.out.println("Retrieved " + shortValues.length + " short values.");
            break;
        case LONG:
            long[] longValues = ((LongColumn) dataColumn).getValues();
            System.out.println("Retrieved " + longValues.length + " long values.");
            break;
        case STRING:
            String[] stringValues = ((StringColumn) dataColumn).getValues();
            System.out.println("Retrieved " + stringValues.length + " String values.");
            break;
        default:
            // Should never get here.
        }
    }
}
