/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.platforms.datamatrix;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.DefaultValueParser;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.ProbeLookup;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.platforms.spi.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.inject.Inject;

/**
 * Handles reading of data matrix copy number data.
 * @author dharley
 *
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
final class DataMatrixCopyNumberHandler extends AbstractDataFileHandler {
    
    private static final String HYBRIDIZATION_REF = "Hybridization REF";
    private static final String REPORTER_REF = "Reporter REF";
    private static final String CHROMOSOME = "Chromosome";
    private static final String POSITION = "Position";
    private static final String LOG_2_RATIO = "Log2Ratio";
    private static final int UNINITIALIZED_INDEX = -1;
    private static final int DATA_COLUMN_ORDERED_ARRAY_SIZE = 3;
    private static final int CHROMOSOME_DATA_COLUMN_ARRAY_POSITION = 0;
    private static final int POSITION_DATA_COLUMN_ARRAY_POSITION = 1;
    private static final int LOG2RATIO_DATA_COLUMN_ARRAY_POSITION = 2;
    
    private int reporterRefColumnIndex = UNINITIALIZED_INDEX;
    private int chromosomeColumnIndex = UNINITIALIZED_INDEX;
    private int positionColumnIndex = UNINITIALIZED_INDEX;
    private String[] hybridizations = null;
    private Map<String, Integer> hybridizationNamesToColumnIndexesMapping = null;
    private Set<Integer> hybridizationColumnIndexesBasedOnLog2RatioColumnPosition = null;
    private List<String> headersOfColumnsToBeIgnored = null;
    private boolean hasBeenInitialized = false;
    private int numberOfProbes = UNINITIALIZED_INDEX;
    private final ValueParser valueParser = new DefaultValueParser();

    @Inject
    protected DataMatrixCopyNumberHandler(FileManager fileManager) {
        super(fileManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean acceptFileType(FileType type) {
        return FileType.MAGE_TAB_DATA_MATRIX_COPY_NUMBER.equals(type);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return DataMatrixCopyNumberDataTypes.MAGE_TAB_DATA_MATRIX_COPY_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return DataMatrixCopyNumberQuantitationTypes.values();
    }

    /**
     * {@inheritDoc}
     */
    public List<LSID> getReferencedArrayDesignCandidateIds() throws PlatformFileReadException {
        return new ArrayList<LSID>();
    }
    
    private DelimitedFileReader getDelimitedFileReader() throws IOException {
        return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(getFile());
    }
    
    private void initialize() throws PlatformFileReadException {
        if (!hasBeenInitialized) {
            DelimitedFileReader delimitedFileReader = null;
            try {
                delimitedFileReader = getDelimitedFileReader();
                readHybridizationRefNamesAndColumnIndexes(delimitedFileReader);
                readDataColumnMappings(delimitedFileReader);
                countNumberOfProbes(delimitedFileReader);
                hasBeenInitialized = true;
            } catch (IOException e) {
                throw new PlatformFileReadException(getFile(), "Cannot initialize: " + e.getMessage(), e);
            } finally {
                if (null != delimitedFileReader) {
                    delimitedFileReader.close();
                }
            }
        }
    }

    private void unInitializeAndClose(DelimitedFileReader delimitedFileReader) {
        hasBeenInitialized = false;
        hybridizations = null;
        hybridizationNamesToColumnIndexesMapping = null;
        hybridizationColumnIndexesBasedOnLog2RatioColumnPosition = null;
        headersOfColumnsToBeIgnored = null;
        if (null != delimitedFileReader) {
            delimitedFileReader.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design)
            throws PlatformFileReadException {
        DelimitedFileReader delimitedFileReader = null;
        try {
            initialize();
            delimitedFileReader = getDelimitedFileReader();
            dataSet.prepareColumns(types, numberOfProbes);
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet, delimitedFileReader, design);
            }
            Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
            typeSet.addAll(types);
            positionReaderAtStartOfData(delimitedFileReader);
            int rowIndex = 0;
            while (delimitedFileReader.hasNextLine()) {
                List<String> values = delimitedFileReader.nextLine();
                for (int i = 0; i < hybridizations.length; i++) {
                    String hybridization = hybridizations[i];
                    HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(i);
                    AbstractDataColumn[] orderedColumns = getOrderedColumns(hybridizationData);
                    valueParser.setValue(orderedColumns[CHROMOSOME_DATA_COLUMN_ARRAY_POSITION], rowIndex,
                            values.get(chromosomeColumnIndex));
                    valueParser.setValue(orderedColumns[POSITION_DATA_COLUMN_ARRAY_POSITION], rowIndex,
                            values.get(positionColumnIndex));
                    valueParser.setValue(orderedColumns[LOG2RATIO_DATA_COLUMN_ARRAY_POSITION], rowIndex,
                            values.get(hybridizationNamesToColumnIndexesMapping.get(hybridization).intValue()));
                }
                rowIndex++;
            }
        } catch (IOException e) {
            throw new PlatformFileReadException(getFile(), "", e);
        } finally {
            unInitializeAndClose(delimitedFileReader);
        }
    }
    
    private AbstractDataColumn[] getOrderedColumns(HybridizationData hybridizationData) {
        boolean chromosomeWasFound = false;
        boolean positionWasFound = false;
        boolean log2RatioWasFound = false;
        AbstractDataColumn[] orderedColumns = new AbstractDataColumn[DATA_COLUMN_ORDERED_ARRAY_SIZE];
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (column.getQuantitationType().getName().equalsIgnoreCase(
                    DataMatrixCopyNumberQuantitationTypes.CHROMOSOME.getName())) {
                orderedColumns[CHROMOSOME_DATA_COLUMN_ARRAY_POSITION] = column;
                chromosomeWasFound = true;
            } else if (column.getQuantitationType().getName().equalsIgnoreCase(
                    DataMatrixCopyNumberQuantitationTypes.POSITION.getName())) {
                orderedColumns[POSITION_DATA_COLUMN_ARRAY_POSITION] = column;
                positionWasFound = true;
            } else if (column.getQuantitationType().getName().equalsIgnoreCase(
                    DataMatrixCopyNumberQuantitationTypes.LOG_2_RATIO.getName())) {
                orderedColumns[LOG2RATIO_DATA_COLUMN_ARRAY_POSITION] = column;
                log2RatioWasFound = true;
            }
            if (chromosomeWasFound && positionWasFound && log2RatioWasFound) {
                break;
            }
        }
        return orderedColumns;
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader delimitedFileReader, ArrayDesign design)
            throws IOException {
        DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
        ProbeLookup probeLookup = new ProbeLookup(design.getDesignDetails().getProbes());
        positionReaderAtStartOfData(delimitedFileReader);
        while (delimitedFileReader.hasNextLine()) {
            List<String> values = delimitedFileReader.nextLine();
            String probeName = values.get(reporterRefColumnIndex);
            probeList.getDesignElements().add(probeLookup.getProbe(probeName));
        }
    }
    
    private void readHybridizationRefNamesAndColumnIndexes(DelimitedFileReader reader)
            throws PlatformFileReadException {
        hybridizationNamesToColumnIndexesMapping = new HashMap<String, Integer>();
        while (reader.hasNextLine()) {
            List<String> nextLineValues;
            try {
                nextLineValues = reader.nextLine();
            } catch (IOException e) {
                throw new PlatformFileReadException(getFile(), e.getMessage(), e);
            }
            if (!nextLineValues.isEmpty() && !StringUtils.isEmpty(nextLineValues.get(0))) {
                if (!HYBRIDIZATION_REF.equalsIgnoreCase(nextLineValues.get(0))) {
                    throw new PlatformFileReadException(getFile(), "The '" + HYBRIDIZATION_REF
                            + "' section is missing or incorrectly specified.");
                }
                for (int i = 1; i < nextLineValues.size(); i++) {
                    if (!StringUtils.isEmpty(nextLineValues.get(i))) {
                        hybridizationNamesToColumnIndexesMapping.put(nextLineValues.get(i), Integer.valueOf(i));
                    }
                }
            }
            break;
        }
        hybridizations = new String[hybridizationNamesToColumnIndexesMapping.size()];
        int index = 0;
        for (String hybridizationName : hybridizationNamesToColumnIndexesMapping.keySet()) {
            hybridizations[index] = hybridizationName;
            index++;
        }
    }
    
    private boolean listContainsString(String targetString, List<String> listToSearchThrough) {
        boolean listContainsString  = false;
        for (String string : listToSearchThrough) {
            if (targetString.equalsIgnoreCase(string)) {
                listContainsString = true;
            }
        }
        return listContainsString;
    }
    
    private void positionReaderAtStartOfData(DelimitedFileReader delimitedFileReader) throws IOException {
        delimitedFileReader.reset();
        while (delimitedFileReader.hasNextLine()) {
            List<String> peekLineValues = delimitedFileReader.peek();
            if ((!peekLineValues.isEmpty() && !StringUtils.isEmpty(peekLineValues.get(0)))
                    && (!HYBRIDIZATION_REF.equalsIgnoreCase(peekLineValues.get(0))
                            && !listContainsString(REPORTER_REF, peekLineValues))) {
                break;
            } else {
                delimitedFileReader.nextLine();
            }
        }
    }
    
    private void countNumberOfProbes(DelimitedFileReader delimitedFileReader) throws IOException {
        positionReaderAtStartOfData(delimitedFileReader);
        int probeCount = 0;
        while (delimitedFileReader.hasNextLine()) {
            delimitedFileReader.nextLine();
            probeCount++;
        }
        numberOfProbes = probeCount;
    }
    
    private void readDataColumnMappings(DelimitedFileReader reader) throws IOException, PlatformFileReadException {
        headersOfColumnsToBeIgnored = new ArrayList<String>();
        hybridizationColumnIndexesBasedOnLog2RatioColumnPosition = new HashSet<Integer>();
        List<String> columnHeaderStrings = reader.nextLine();
        for (int i = 0; i < columnHeaderStrings.size(); i++) {
            String columnHeader = columnHeaderStrings.get(i);
            if (REPORTER_REF.equalsIgnoreCase(columnHeader)) {
                reporterRefColumnIndex = i;
            } else if (CHROMOSOME.equalsIgnoreCase(columnHeader)) {
                chromosomeColumnIndex = i;
            } else if (POSITION.equalsIgnoreCase(columnHeader)) {
                positionColumnIndex = i;
            } else if (LOG_2_RATIO.equalsIgnoreCase(columnHeader)) {
                hybridizationColumnIndexesBasedOnLog2RatioColumnPosition.add(Integer.valueOf(i));
            } else {
                headersOfColumnsToBeIgnored.add(columnHeader);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean parsesData() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "PMD.NPathComplexity" })
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException {
        initialize();
        if (hybridizationNamesToColumnIndexesMapping.size()
                != hybridizationColumnIndexesBasedOnLog2RatioColumnPosition.size()) {
            result.addMessage(Type.ERROR, "There must be a one-to-one correspondence between hybridizations and "
                + LOG_2_RATIO + " columns.");
        } else {
            for (String hybridizationName : hybridizationNamesToColumnIndexesMapping.keySet()) {
                if (!hybridizationColumnIndexesBasedOnLog2RatioColumnPosition.contains(
                        hybridizationNamesToColumnIndexesMapping.get(hybridizationName))) {
                    result.addMessage(Type.ERROR, "Detected a hybridization name to "
                        + LOG_2_RATIO + " column misalignment for the hybridization '" + hybridizationName + "'.");
                }
            }
        }
        if (UNINITIALIZED_INDEX == reporterRefColumnIndex) {
            result.addMessage(Type.ERROR, "No '" + REPORTER_REF + "' column found.");
        }
        if (UNINITIALIZED_INDEX == chromosomeColumnIndex) {
            result.addMessage(Type.ERROR, "No '" + CHROMOSOME + "' column found.");
        }
        if (UNINITIALIZED_INDEX == positionColumnIndex) {
            result.addMessage(Type.ERROR, "No '" + POSITION + "' column found.");
        }
        for (String columnName : headersOfColumnsToBeIgnored) {
            result.addMessage(Type.WARNING, "The column '" + columnName + "' will be ignored during data parsing.");
        }
    }

    /**
     * {@inheritDoc}
     */        
    public boolean requiresMageTab() {
        return true;
    }
}
