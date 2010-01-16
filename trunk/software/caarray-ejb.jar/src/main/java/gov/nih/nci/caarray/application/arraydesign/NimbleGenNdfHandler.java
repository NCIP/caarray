/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
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
