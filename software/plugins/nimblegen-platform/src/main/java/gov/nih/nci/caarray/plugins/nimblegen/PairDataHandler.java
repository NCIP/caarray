//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.DefaultValueParser;
import gov.nih.nci.caarray.platforms.ProbeLookup;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Handles reading of nimblegen data.
 */
public class PairDataHandler extends AbstractDataFileHandler {
    private static final String LSID_AUTHORITY = "nimblegen.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";

    private static final String SEQ_ID_HEADER = "SEQ_ID";
    private static final String PROBE_ID_HEADER = "PROBE_ID";
    private static final String CONTAINER_HEADER = "GENE_EXPR_OPTION";
    private static final int BATCH_SIZE = 1000;

    /**
     * File Type for normalized PAIR data files.
     */
    public static final FileType NORMALIZED_PAIR_FILE_TYPE = new FileType("NIMBLEGEN_NORMALIZED_PAIR",
            FileCategory.DERIVED_DATA, true);
    /**
     * File Type for raw PAIR data files.
     */
    public static final FileType RAW_PAIR_FILE_TYPE = new FileType("NIMBLEGEN_RAW_PAIR", FileCategory.RAW_DATA, true);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(NORMALIZED_PAIR_FILE_TYPE, RAW_PAIR_FILE_TYPE);

    private final ValueParser valueParser = new DefaultValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * @param dataStorageFacade dataStorageFacade to use
     */
    @Inject
    PairDataHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
        super(dataStorageFacade);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return NimblegenArrayDataTypes.NIMBLEGEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return NimblegenQuantitationType.values();
    }

    // returns the column headers in the file, and positions reader at start of data
    private List<String> getHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            if (values.get(0).startsWith("#")) {
                continue;
            }
            if (values.size() > 1 && !StringUtils.isEmpty(values.get(1))) {
                return values;
            }
        }
        return null;
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (final IOException e) {
            throw new IllegalStateException("File could not be reset", e);
        }
    }

    private DelimitedFileReader getReader(File dataFile) {
        try {
            return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(dataFile);
        } catch (final IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            dataSet.prepareColumns(types, getNumberOfDataRows(reader));
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet, reader, design);
            }
            for (final HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
                loadData(hybridizationData, reader);
            }
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader reader, ArrayDesign design)
    throws IOException {
        final DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
        final ArrayDesignDetails designDetails = design.getDesignDetails();
        final ProbeLookup probeLookup = new ProbeLookup(designDetails.getProbes());
        final List<String> headers = getHeaders(reader);
        final int seqIdIndex = headers.indexOf(SEQ_ID_HEADER);
        final int probeIdIndex = headers.indexOf(PROBE_ID_HEADER);
        final int containerIndex = headers.indexOf(CONTAINER_HEADER);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            final String probeId = values.get(probeIdIndex);
            final String sequenceId = values.get(seqIdIndex);
            final String container = values.get(containerIndex);
            final String probeName = container + "|" + sequenceId + "|" + probeId;
            probeList.getDesignElements().add(probeLookup.getProbe(probeName));
        }
    }

    private void loadData(HybridizationData hybridizationData, DelimitedFileReader reader) throws IOException {
        final List<String> headers = getHeaders(reader);
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            loadData(hybridizationData, values, headers, rowIndex++);
        }
    }

    private void loadData(HybridizationData hybridizationData, List<String> values, List<String> headers,
            int rowIndex) {
        final Set<String> types = new HashSet<String>(NimblegenQuantitationType.getTypeNames());
        for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
            final String header = headers.get(valueIndex);
            if (types.contains(header)) {
                final QuantitationTypeDescriptor valueType = NimblegenQuantitationType.valueOf(header);
                this.valueParser.setValue(hybridizationData.getColumn(valueType), rowIndex, values.get(valueIndex));
            }
        }
    }

    private int getNumberOfDataRows(DelimitedFileReader reader) throws IOException {
        int numberOfDataRows = 0;
        getHeaders(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
    throws PlatformFileReadException {
        try {
            validateProbeNames(getReader(getFile()), design, result);
        } catch (final IOException ioException) {
            throw new PlatformFileReadException(getFile(), "Cannot validate pair data file: "
                    + ioException.getMessage(), ioException);
        }
    }

    private void validateProbeNames(final DelimitedFileReader reader, final ArrayDesign design,
            final FileValidationResult fileValidationResult) throws IOException {
        final ProbeNamesValidator probeNamesValidator = new ProbeNamesValidator(this.arrayDao, design);
        final List<String> probeNamesBatch = new ArrayList<String>();
        int probeCounter = 0;
        final List<String> headers = getHeaders(reader);
        final int seqIdIndex = headers.indexOf(SEQ_ID_HEADER);
        final int probeIdIndex = headers.indexOf(PROBE_ID_HEADER);
        final int containerIndex = headers.indexOf(CONTAINER_HEADER);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            final String probeId = values.get(probeIdIndex);
            final String sequenceId = values.get(seqIdIndex);
            final String container = values.get(containerIndex);
            final String probeName = container + "|" + sequenceId + "|" + probeId;
            probeNamesBatch.add(probeName);
            probeCounter++;
            if (0 == probeCounter % BATCH_SIZE) {
                probeNamesValidator.validateProbeNames(fileValidationResult, probeNamesBatch);
                probeNamesBatch.clear();
            }
        }
        if (!probeNamesBatch.isEmpty()) {
            probeNamesValidator.validateProbeNames(fileValidationResult, probeNamesBatch);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            final Map<String, String> metadata = getHeaderMetadata(reader);
            final String designName = metadata.get("designname");
            return Collections.singletonList(new LSID(LSID_AUTHORITY, LSID_NAMESPACE, designName));
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private Map<String, String> getHeaderMetadata(DelimitedFileReader reader) throws IOException {
        reset(reader);
        final Map<String, String> result = new HashMap<String, String>();
        final List<String> line = reader.nextLine();
        line.set(0, line.get(0).substring(2));
        for (final String value : line) {
            final String[] v = value.split("=");
            if (v.length < 2) {
                continue;
            }
            result.put(v[0], v[1]);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }
}
