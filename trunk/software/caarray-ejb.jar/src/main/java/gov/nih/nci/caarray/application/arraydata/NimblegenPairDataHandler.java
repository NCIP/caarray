//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.nimblegen.NimblegenArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.nimblegen.NimblegenQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Handles reading of nimblegen data.
 */
class NimblegenPairDataHandler extends AbstractDataFileHandler {
    private static final String LSID_AUTHORITY = "nimblegen.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final Logger LOG = Logger
            .getLogger(NimblegenPairDataHandler.class);

    private static final String SEQ_ID_HEADER = "SEQ_ID";
    private static final String PROBE_ID_HEADER = "PROBE_ID";
    private static final String CONTAINER_HEADER = "GENE_EXPR_OPTION";

    private CaArrayFile caArrayDataFile = null;

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        String fileName = dataFile.getName();
        return NimblegenArrayDataTypes.NIMBLEGEN;
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file) {
        return NimblegenQuantitationType.values();
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(
            DelimitedFileReader reader) throws IOException {
        return getQuantitationTypeDescriptors(getHeaders(reader));
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(
            List<String> headers) {
        List<QuantitationTypeDescriptor> descriptorList
            = new ArrayList<QuantitationTypeDescriptor>();
        List<String> qTypes = NimblegenQuantitationType.getTypeNames();
        for (String header : headers) {
            if (qTypes.contains(header)) {
                descriptorList.add(NimblegenQuantitationType.valueOf(header));
            }
        }
        return descriptorList.toArray(new QuantitationTypeDescriptor[descriptorList.size()]);
    }

    private List<String> getHeaders(DelimitedFileReader reader)
            throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
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
        } catch (IOException e) {
            throw new IllegalStateException("File could not be reset", e);
        }
    }

    private DelimitedFileReader getReader(File dataFile) {
        try {
            return DelimitedFileReaderFactory.INSTANCE
                    .getTabDelimitedReader(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException("File " + dataFile.getName()
                    + " could not be read", e);
        }

    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file,
            ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            prepareColumns(dataSet, types, getNumberOfDataRows(reader));
            if (dataSet.getDesignElementList() == null) {
                HybridizationData hd = dataSet.getHybridizationDataList().get(0);
                Experiment exp = hd.getHybridization().getExperiment();
                loadDesignElementList(dataSet, reader, arrayDesignService, exp);
            }
            for (HybridizationData hybridizationData : dataSet
                    .getHybridizationDataList()) {
                loadData(hybridizationData, reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadDesignElementList(DataSet dataSet,
                                       DelimitedFileReader reader,
                                       ArrayDesignService arrayDesignService,
                                       Experiment experiment)
            throws IOException {
        DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
        ArrayDesign design = getArrayDesign(arrayDesignService, reader);
        if (design == null) {
            design = getArrayDesign(experiment);
            if (design == null) {
                throw new IllegalStateException("Could not find array design for file.");
            }
        }
        ArrayDesignDetails designDetails = design.getDesignDetails();
        ProbeLookup probeLookup = new ProbeLookup(designDetails.getProbes());
        List<String> headers = getHeaders(reader);
        int seqIdIndex = headers.indexOf(SEQ_ID_HEADER);
        int probeIdIndex = headers.indexOf(PROBE_ID_HEADER);
        int containerIndex = headers.indexOf(CONTAINER_HEADER);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            String probeId = values.get(probeIdIndex);
            String sequenceId = values.get(seqIdIndex);
            String container = values.get(containerIndex);
            String probeName = container + "|" + sequenceId + "|" + probeId;
            probeList.getDesignElements().add(probeLookup.getProbe(probeName));
        }
    }

    private void loadData(HybridizationData hybridizationData,
                          DelimitedFileReader reader) throws IOException {
        List<String> headers = getHeaders(reader);
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            loadData(hybridizationData, values, headers, rowIndex++);
        }
    }

    private void loadData(HybridizationData hybridizationData,
            List<String> values, List<String> headers, int rowIndex) {
        Set<String> types
            = new HashSet<String>(NimblegenQuantitationType.getTypeNames());
        for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
            String header = headers.get(valueIndex);
            if (types.contains(header)) {
                    QuantitationTypeDescriptor valueType
                        = NimblegenQuantitationType.valueOf(header);
                    setValue(hybridizationData.getColumn(valueType), rowIndex,
                             values.get(valueIndex));
                }
        }
    }

    private int getNumberOfDataRows(DelimitedFileReader reader)
            throws IOException {
        int numberOfDataRows = 0;
        getHeaders(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file,
            MageTabDocumentSet mTabSet, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        caArrayDataFile = caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService,
            File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            return getArrayDesign(arrayDesignService, reader);
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private Map<String, String> getHeaderMetadata(DelimitedFileReader reader)
            throws IOException {
        reset(reader);
        Map<String, String> result = new HashMap<String, String>();
        List<String> line = reader.nextLine();
        line.set(0, line.get(0).substring(2));
        for (String value : line) {
            String[] v = value.split("=");
            if (v.length < 2) {
                continue;
            }
            result.put(v[0], v[1]);
        }
        return result;
    }

    private ArrayDesign getArrayDesign(Experiment experiment) {
        ArrayDesign result = null;
        for (ArrayDesign d : experiment.getArrayDesigns()) {
            if ("Nimblegen".equals(d.getProvider().getName())) {
                if (result == null) {
                    result = d;
                } else {
                    // This should create a validation error.
                    return null;
                }
            }
        }
        return result;
    }

    private ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService,
            DelimitedFileReader reader) throws IOException {
        Map<String, String> metadata = getHeaderMetadata(reader);
        String designName = metadata.get("designname");
        ArrayDesign design = arrayDesignService.getArrayDesign(LSID_AUTHORITY,
                                                               LSID_NAMESPACE,
                                                               designName);
        if (design == null && caArrayDataFile != null) {
            Experiment exp = caArrayDataFile.getProject().getExperiment();
            design = getArrayDesign(exp);
        }
        return design;
    }

}
