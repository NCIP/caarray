//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;

/**
 * Implementation of NDF parser with NGD and POS file support.
 * 
 * @author Jim McCusker
 */
public class NimbleGenNdfHandler extends AbstractArrayDesignHandler {
    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;
    private static final Logger LOG = Logger
            .getLogger(NimbleGenNdfHandler.class);

    private static final String TEMP_TABLE_NAME = "NGImporterTempTable";
    private static final String CREATE_TEMP_TABLE_STMT = "CREATE TEMPORARY TABLE "
            + TEMP_TABLE_NAME
            + " ( PROBE_ID varchar(100), "
            + "SEQ_ID varchar(100), "
            + "CONTAINER varchar(100), "
            + "X int, "
            + "Y int, " + "INDEX(SEQ_ID));";

    private static final String Y = "Y";
    private static final String X = "X";
    private static final String PROBE_ID = "PROBE_ID";
    private static final String CONTAINER2 = "CONTAINER";
    private static final String SEQ_ID = "SEQ_ID";

    private Map<String, LogicalProbe> logicalProbes = new HashMap<String, LogicalProbe>();

    NimbleGenNdfHandler(VocabularyService vocabularyService,
            CaArrayDaoFactory daoFactory, Set<CaArrayFile> designFiles) {
        super(vocabularyService, daoFactory, designFiles);
    }

    private static boolean isBlank(String s) {
        return s.matches("^\\s$");
    }

    private ScrollableResults loadRows(File file) throws IOException {
        HibernateUtil.getCurrentSession().createSQLQuery(
                CREATE_TEMP_TABLE_STMT).executeUpdate();
        HibernateUtil.getCurrentSession().createSQLQuery(
                "LOCK TABLE " + TEMP_TABLE_NAME + " WRITE;")
                .executeUpdate();
        String loadQuery = "load data local infile '"
                + file.getAbsolutePath()
                + "' into table "
                + TEMP_TABLE_NAME
                + " fields terminated by '\t' ignore 1 lines "
                + "(@c1,CONTAINER,@c3,@c4,SEQ_ID,@c6,@c7,@c8,@c9,@c10,@c11,@c12,PROBE_ID,@c14,@c15,X,Y);";

        SQLQuery q = HibernateUtil.getCurrentSession()
                .createSQLQuery(loadQuery);
        q.executeUpdate();
        HibernateUtil.getCurrentSession().createSQLQuery("UNLOCK TABLES;")
                .executeUpdate();
        return getProbes();
    }

    ScrollableResults getProbes() {
        SQLQuery q = HibernateUtil.getCurrentSession().createSQLQuery(
                "select * from " + TEMP_TABLE_NAME + " order by SEQ_ID asc");
        return q.scroll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void createDesignDetails(ArrayDesign arrayDesign) {
        DelimitedFileReader reader = null;
        logicalProbes = new HashMap<String, LogicalProbe>();
        int count = 0;
        try {
            reader = DelimitedFileReaderFactory.INSTANCE
                    .getTabDelimitedReader(getFile());
            ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(arrayDesign);
            getArrayDao().save(details);

            ScrollableResults results = loadRows(getFile());
            count = loadProbes(details, results);
            arrayDesign.setNumberOfFeatures(count);
            HibernateUtil.getCurrentSession().createSQLQuery(
                    "DROP TABLE " + TEMP_TABLE_NAME + ";").executeUpdate();
        } catch (IOException e) {
            LOG.error("Error processing line " + count);
            throw new IllegalStateException("Couldn't read file: ", e);
        } finally {
            reader.close();
        }
    }

    private int loadProbes(ArrayDesignDetails details, ScrollableResults results)
            throws IOException {
        int count = 0;
        results.beforeFirst();
        String lastSeqId = null;
        while (results.next()) {
            Object[] values = results.get();
            Map<String, Object> vals = new HashMap<String, Object>();
            vals.put(PROBE_ID, values[0]);
            vals.put(SEQ_ID, values[1]);
            vals.put(CONTAINER2, values[2]);
            vals.put(X, values[3]);
            vals.put(Y, values[4]);

            if (lastSeqId != null && !vals.get(SEQ_ID).equals(lastSeqId)) {
                logicalProbes.clear();
                flushAndClearSession();
            }
            lastSeqId = (String) vals.get(SEQ_ID);

            PhysicalProbe p = createPhysicalProbe(details, vals);
            getArrayDao().save(p);
            ++count;
        }
        return count;
    }

    private LogicalProbe getLogicalProbe(String feature,
            ArrayDesignDetails details) {
        if (!logicalProbes.containsKey(feature)) {
            LogicalProbe p = new LogicalProbe(details);
            p.setName(feature);
            logicalProbes.put(feature, p);
            getArrayDao().save(p);
            return p;
        } else {
            return logicalProbes.get(feature);
        }
    }

    private PhysicalProbe createPhysicalProbe(ArrayDesignDetails details,
            Map<String, Object> values) {
        String sequenceId = (String) values.get(SEQ_ID);
        String container = (String) values.get(CONTAINER2);
        String probeId = (String) values.get(PROBE_ID);
        // ProbeGroup group = getProbeGroup(container, details);
        LogicalProbe lp = getLogicalProbe(sequenceId, details);
        PhysicalProbe p = new PhysicalProbe(details, null);
        lp.addProbe(p);
        p.setName(container + "|" + sequenceId + "|" + probeId);

        Feature f = new Feature(details);
        f.setColumn(((Integer) values.get(X)).shortValue());
        f.setRow(((Integer) values.get(Y)).shortValue());

        p.getFeatures().add(f);

        return p;
    }

    static Map<String, Object> getValues(List<String> values,
            Map<String, Integer> header) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String column : ndfColumnMapping.keySet()) {
            String value = values.get(header.get(column));
            if (ndfColumnMapping.get(column) == Integer.class) {
                result.put(column, getIntegerValue(value));
            } else if (ndfColumnMapping.get(column) == Long.class) {
                result.put(column, getLongValue(value));
            } else {
                result.put(column, value);
            }
        }
        return result;
    }

    static Integer getIntegerValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    static Long getLongValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            return Long.parseLong(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils
                .getBaseName(getDesignFile().getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE
                + ":" + arrayDesign.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void validate(ValidationResult result) {
        File ndfFile = getFile(".ndf");
        FileValidationResult fileResult = result
                .getFileValidationResult(ndfFile);
        if (fileResult == null) {
            fileResult = new FileValidationResult(ndfFile);
            result.addFile(ndfFile, fileResult);
        }
        doValidation(fileResult, ndfFile);

    }

    private void doValidation(FileValidationResult result, File file) {
        DelimitedFileReader reader = null;
        try {
            reader = DelimitedFileReaderFactory.INSTANCE
                    .getTabDelimitedReader(file);
            if (!reader.hasNextLine()) {
                result.addMessage(ValidationMessage.Type.ERROR,
                        "File was empty");
            }
            Map<String, Integer> headers = getHeaders(reader);
            validateHeader(headers, result);
            if (result.isValid()) {
                validateContent(reader, result, headers);
            }
        } catch (IOException e) {
            result.addMessage(ValidationMessage.Type.ERROR,
                    "Unable to read file");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void validateHeader(Map<String, Integer> headers,
            FileValidationResult result) throws IOException {
        Set<String> missing = new HashSet<String>(ndfColumnMapping.keySet());
        missing.removeAll(headers.keySet());

        for (String col : missing) {
            result.addMessage(ValidationMessage.Type.ERROR,
                    "Invalid column header in Nimblegen NDF. Missing " + col
                            + ".");
        }
    }

    private void validateContent(DelimitedFileReader reader,
            FileValidationResult result, Map<String, Integer> header)
            throws IOException {
        int expectedNumberOfFields = header.size();
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (values.size() < expectedNumberOfFields) {
                ValidationMessage error = result.addMessage(
                        ValidationMessage.Type.ERROR,
                        "Invalid number of fields. Expected at least "
                                + expectedNumberOfFields + " but contained "
                                + values.size());
                error.setLine(reader.getCurrentLineNumber());
            }
            if (!validateValues(values, result, reader.getCurrentLineNumber(),
                    header)) {
                return;
            }
        }
    }

    private boolean validateValues(List<String> values,
            FileValidationResult result, int currentLineNumber,
            Map<String, Integer> header) {
        boolean passed = true;
        for (String column : header.keySet()) {
            if (!ndfColumnMapping.containsKey(column)) {
                continue;
            }
            passed = passed
                    & validateValue(result, currentLineNumber, column, values
                            .get(header.get(column)));
        }
        return passed;
    }

    private boolean validateValue(FileValidationResult result,
            int currentLineNumber, String column, String value) {
        if (value == null || isBlank(value)) {
            result.addMessage(ValidationMessage.Type.ERROR,
                    "Missing required value at [" + currentLineNumber + ","
                            + column + "].");
        } else if (ndfColumnMapping.get(column) == Integer.class) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                ValidationMessage error = result.addMessage(
                        ValidationMessage.Type.ERROR,
                        "Expected integer value at [" + currentLineNumber + ","
                                + column + "] but found " + value);
                error.setLine(currentLineNumber);
                return false;
            }
        }
        return true;
    }

    private boolean isHeaderLine(List<String> values) {
        return values.containsAll(ndfColumnMapping.keySet());
    }

    private static Map<String, Class<?>> ndfColumnMapping = new HashMap<String, Class<?>>();
    static {
        ndfColumnMapping.put(CONTAINER2, String.class);
        ndfColumnMapping.put(SEQ_ID, String.class);
        ndfColumnMapping.put(PROBE_ID, String.class);
        ndfColumnMapping.put(X, Integer.class);
        ndfColumnMapping.put(Y, Integer.class);
    }

    private Map<String, Integer> getHeaders(DelimitedFileReader reader)
            throws IOException {
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isHeaderLine(values)) {
                int index = 0;
                Map<String, Integer> result = new HashMap<String, Integer>();
                for (String value : values) {
                    result.put(value.toUpperCase(Locale.getDefault()), index++);
                }
                return result;
            }
        }
        return null;
    }

    FileStatus getValidatedStatus() {
        return FileStatus.IMPORTED_NOT_PARSED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Logger getLog() {
        return LOG;
    }

}
