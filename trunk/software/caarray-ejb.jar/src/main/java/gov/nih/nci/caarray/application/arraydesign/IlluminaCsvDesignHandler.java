//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
final class IlluminaCsvDesignHandler extends AbstractArrayDesignHandler {

    private static final Logger LOG = Logger.getLogger(IlluminaCsvDesignHandler.class);

    private static final String LSID_AUTHORITY = "illumina.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final int LOGICAL_PROBE_BATCH_SIZE = 1000;

    private AbstractIlluminaDesignHandler handler;

    IlluminaCsvDesignHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory,
            CaArrayFile designFile) {
        super(vocabularyService, daoFactory, designFile);
    }

    @Override
    void createDesignDetails(ArrayDesign arrayDesign) {
        DelimitedFileReader reader = getReader();
        try {
            positionAtAnnotation(reader);
            ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(arrayDesign);
            getArrayDao().flushSession();
            int count = 0;
            while (reader.hasNextLine()) {
                List<String> values = reader.nextLine();
                if (getHandler().isLineFollowingAnnotation(values)) {
                    break;
                }
                getArrayDao().save(getHandler().createLogicalProbe(details, values));
                if (++count % LOGICAL_PROBE_BATCH_SIZE == 0) {
                    flushAndClearSession();
                }
            }
            flushAndClearSession();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read file: ", e);
        } finally {
            reader.close();
        }
    }

    @Override
    void validate(ValidationResult result) {
        try {
            if (getHandler() == null) {
            result.addMessage(getFile(), Type.ERROR, "The file " + getFile().getName()
                        + " is not a recognizable Illumina array annotation format.");
            } else {
                FileValidationResult fileResult = result.getFileValidationResult(getFile());
                if (fileResult == null) {
                    fileResult = new FileValidationResult(getFile());
                    result.addFile(getFile(), fileResult);
                }
            doValidation(fileResult);
            }
        } catch (IOException e) {
            result.addMessage(getFile(), ValidationMessage.Type.ERROR, "Unable to read file");
        }
    }

    private void doValidation(FileValidationResult result) {
        DelimitedFileReader reader = null;
        try {
            reader = DelimitedFileReaderFactory.INSTANCE.getCsvReader(getFile());
            if (!reader.hasNextLine()) {
                result.addMessage(ValidationMessage.Type.ERROR, "Illumina CSV file was empty");
            }
            List<String> headers = getHeaders(reader);
            validateHeader(headers, result);
            if (result.isValid()) {
                validateContent(reader, result, headers);
            }
        } catch (IOException e) {
            result.addMessage(ValidationMessage.Type.ERROR, "Unable to read file");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void validateContent(DelimitedFileReader reader, FileValidationResult result, List<String> headers)
    throws IOException {
        int expectedNumberOfFields = headers.size();
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (getHandler().isLineFollowingAnnotation(values)) {
                break;
            }
            if (values.size() != expectedNumberOfFields) {
                ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                        "Invalid number of fields. Expected "
                        + expectedNumberOfFields + " but contained " + values.size());
                error.setLine(reader.getCurrentLineNumber());
            }
            getHandler().validateValues(values, result, reader.getCurrentLineNumber());
        }
    }

    static void validateFieldLength(List<String> values, Enum header, FileValidationResult result,
            int lineNumber, int expectedLength) {
        if (getValue(values, header).length() != expectedLength) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected size of field for " + header.name() + "to be " + expectedLength
                    + " but was " + getValue(values, header).length());
            error.setLine(lineNumber);
            error.setColumn(header.ordinal() + 1);
        }
    }

    static void validateIntegerField(List<String> values, Enum header, FileValidationResult result,
            int lineNumber) {
        if (!isInteger(values.get(header.ordinal()))) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected integer value for " + header.name() + ", but was " +  values.get(header.ordinal()));
            error.setLine(lineNumber);
            error.setColumn(header.ordinal() + 1);
        }
    }

    static void validateLongField(List<String> values, Enum header, FileValidationResult result,
            int lineNumber) {
        if (!isLong(values.get(header.ordinal()))) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected long integral value for " + header.name() + ", but was " +  values.get(header.ordinal()));
            error.setLine(lineNumber);
            error.setColumn(header.ordinal() + 1);
        }
    }

    static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateHeader(List<String> headers, FileValidationResult result) throws IOException {
        Enum[] expectedHeaders = getHandler().getExpectedHeaders(headers);
        if (headers.size() != expectedHeaders.length) {
            result.addMessage(ValidationMessage.Type.ERROR,
                    "Illumina CSV file didn't contain the expected number of columns");
            return;
        }
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!headers.get(i).equalsIgnoreCase(expectedHeaders[i].name())) {
                result.addMessage(ValidationMessage.Type.ERROR, "Invalid column header in Illumina CSV. Expected "
                        + expectedHeaders[i] + " but was " + headers.get(i));
            }
        }
    }

    private List<String> getHeaders(DelimitedFileReader reader) throws IOException {
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (getHandler().isHeaderLine(values)) {
                return values;
            }
        }
        return null;
    }

    @Override
    void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(getDesignFile().getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesign.getName());
        try {
            arrayDesign.setNumberOfFeatures(getNumberOfFeatures());
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    private int getNumberOfFeatures() throws IOException {
        DelimitedFileReader reader = getReader();
        try {
            positionAtAnnotation(reader);
            int numberOfFeatures = 0;
            while (reader.hasNextLine()) {
                if (getHandler().isLineFollowingAnnotation(reader.nextLine())) {
                    break;
                }
                numberOfFeatures++;
            }
            return numberOfFeatures;
        } finally {
            reader.close();
        }
    }

    private void positionAtAnnotation(DelimitedFileReader reader) throws IOException {
        reset(reader);
        boolean isHeader = false;
        while (!isHeader && reader.hasNextLine()) {
            isHeader = getHandler().isHeaderLine(reader.nextLine());
        }
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't reset file " + getDesignFile().getName(), e);
        }
    }

    static String getValue(List<String> values, Enum header) {
        return values.get(header.ordinal());
    }

    static Integer getIntegerValue(List<String> values, Enum header) {
        String stringValue = getValue(values, header);
        if (StringUtils.isBlank(stringValue)) {
            return null;
        } else {
            return Integer.parseInt(stringValue);
        }
    }

    static Long getLongValue(List<String> values, Enum header) {
        String stringValue = getValue(values, header);
        if (StringUtils.isBlank(stringValue)) {
            return null;
        } else {
            return Long.parseLong(stringValue);
        }
    }

    private AbstractIlluminaDesignHandler getHandler() throws IOException {
        if (handler == null) {
            handler = createHandler();
        }
        return handler;
    }

    private AbstractIlluminaDesignHandler createHandler() throws IOException {
        DelimitedFileReader reader = getReader();
        try {
            List<AbstractIlluminaDesignHandler> candidateHandlers = getCandidateHandlers();
            while (reader.hasNextLine()) {
                List<String> values = reader.nextLine();
                for (AbstractIlluminaDesignHandler candidateHandler : candidateHandlers) {
                    if (candidateHandler.isHeaderLine(values)) {
                        return candidateHandler;
                    }
                }
            }
            return null;
        } finally {
            reader.close();
        }
    }

    private List<AbstractIlluminaDesignHandler> getCandidateHandlers() {
        List<AbstractIlluminaDesignHandler> handlers = new ArrayList<AbstractIlluminaDesignHandler>();
        handlers.add(new IlluminaExpressionCsvDesignHandler());
        handlers.add(new IlluminaGenotypingCsvDesignHandler());
        return handlers;
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    private DelimitedFileReader getReader() {
        return getReader(getFile());
    }

    static DelimitedFileReader getReader(File illuminaCsvFile) {
        try {
            return DelimitedFileReaderFactory.INSTANCE.getCsvReader(illuminaCsvFile);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read file " + illuminaCsvFile.getName(), e);
        }

    }

}
