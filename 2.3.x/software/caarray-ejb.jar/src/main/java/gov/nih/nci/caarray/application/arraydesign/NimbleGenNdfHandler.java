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

import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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

    private static final int LOGICAL_PROBE_BATCH_SIZE = 1000;

    private Map<String, ProbeGroup> probeGroups = new HashMap<String, ProbeGroup>();

    private Map<String, LogicalProbe> logicalProbes = new HashMap<String, LogicalProbe>();

    NimbleGenNdfHandler(VocabularyService vocabularyService,
            CaArrayDaoFactory daoFactory, Set<CaArrayFile> designFiles) {
        super(vocabularyService, daoFactory, designFiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void createDesignDetails(ArrayDesign arrayDesign) {
        DelimitedFileReader reader = null;
        probeGroups = new HashMap<String, ProbeGroup>();
        logicalProbes = new HashMap<String, LogicalProbe>();
        Map<String,Map<String,Object>> annotations = buildAnnotations();
        try {
            reader = DelimitedFileReaderFactory.INSTANCE
                    .getTabDelimitedReader(getFile());
            positionAtAnnotation(reader, ndfColumnNames);
            ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(arrayDesign);
            getArrayDao().flushSession();
            int count = 0;
            while (reader.hasNextLine()) {
                List<String> values = reader.nextLine();
                getArrayDao().save(
                        createPhysicalProbe(details, getValues(values,
                                ndfColumnNames, ndfColumnTypes)));
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

    private Map<String, Map<String, Object>> buildAnnotations() {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

        return result;
    }

    private ProbeGroup getProbeGroup(String feature, ArrayDesignDetails details) {
        if (!probeGroups.containsKey(feature)) {
            ProbeGroup pg = new ProbeGroup(details);
            pg.setName(feature);
            probeGroups.put(feature, pg);
            getArrayDao().save(pg);
            return pg;
        } else {
            return probeGroups.get(feature);
        }
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
        String featureId = (String) values.get("FEATURE_ID");
        ProbeGroup group = getProbeGroup(featureId, details);
        LogicalProbe lp = getLogicalProbe(featureId, details);
        PhysicalProbe p = new PhysicalProbe(details, group);
        lp.addProbe(p);
        String probeId = (String) values.get("PROBE_ID");
        p.setName(probeId);

        Feature f = new Feature(details);
        f.setColumn(((Integer) values.get("X")).shortValue());
        f.setRow(((Integer) values.get("Y")).shortValue());

        p.getFeatures().add(f);

        // Add information about QC probes vs experimental probes.
        // Nimblegen data based on nucleotide ids, not genes.
        // ExpressionProbeAnnotation annotation = new
        // ExpressionProbeAnnotation();
        //        
        // annotation.setGene(new Gene());
        // annotation.getGene().set
        // annotation.getGene().setSymbol(getValue(values, Header.));
        // annotation.getGene().setFullName(getValue(values,
        // Header.DEFINITION));
        // p.setAnnotation(annotation);
        return p;
    }

    static Map<String, Object> getValues(List<String> values,
            String[] colNames, Class<?>[] colTypes) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < colNames.length; ++i) {
            if (colTypes[i] == Integer.class) {
                result.put(colNames[i], getIntegerValue(values.get(i)));
            } else if (colTypes[i] == Integer.class) {
                result.put(colNames[i], getLongValue(values.get(i)));
            } else {
                result.put(colNames[i], values.get(i));
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

    private void positionAtAnnotation(DelimitedFileReader reader,
            String[] colNames) throws IOException {
        reset(reader);
        boolean isHeader = false;
        while (!isHeader && reader.hasNextLine()) {
            isHeader = isHeaderLine(reader.nextLine(), colNames);
        }
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't reset file "
                    + getDesignFile().getName(), e);
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
        doValidation(fileResult, ndfFile, ndfColumnNames, ndfColumnTypes);

        File annotationFile = getFile(".ngd");
        if (annotationFile != null) {
            fileResult = result.getFileValidationResult(annotationFile);
            result.addFile(annotationFile, fileResult);
            doValidation(fileResult, annotationFile, ngdColumnNames,
                    ngdColumnTypes);
        } else {
            annotationFile = getFile(".pos");
            if (annotationFile != null) {
                fileResult = result.getFileValidationResult(annotationFile);
                result.addFile(annotationFile, fileResult);

                doValidation(fileResult, annotationFile, posColumnNames,
                        posColumnTypes);
            }
        }
    }

    protected void doValidation(FileValidationResult result, File file,
            String[] colNames, Class<?>[] colTypes) {
        DelimitedFileReader reader = null;
        try {
            reader = DelimitedFileReaderFactory.INSTANCE
                    .getTabDelimitedReader(file);
            if (!reader.hasNextLine()) {
                result.addMessage(ValidationMessage.Type.ERROR,
                        "File was empty");
            }
            List<String> headers = getHeaders(reader, colNames);
            validateHeader(headers, result, colNames);
            if (result.isValid()) {
                validateContent(reader, result, headers, colNames, colTypes);
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

    private void validateHeader(List<String> headers,
            FileValidationResult result, String[] colNames) throws IOException {
        if (headers.size() != colNames.length) {
            result
                    .addMessage(ValidationMessage.Type.ERROR,
                            "Nimblegen NDF file didn't contain the expected number of columns");
            return;
        }
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null
                    && !headers.get(i).equalsIgnoreCase(colNames[i])) {
                result.addMessage(ValidationMessage.Type.ERROR,
                        "Invalid column header in Nimblegen NDF. Expected "
                                + colNames[i] + " but was " + headers.get(i));
            }
        }
    }

    private void validateContent(DelimitedFileReader reader,
            FileValidationResult result, List<String> headers,
            String[] colNames, Class<?>[] colTypes) throws IOException {
        int expectedNumberOfFields = headers.size();
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (values.size() != expectedNumberOfFields) {
                ValidationMessage error = result.addMessage(
                        ValidationMessage.Type.ERROR,
                        "Invalid number of fields. Expected "
                                + expectedNumberOfFields + " but contained "
                                + values.size());
                error.setLine(reader.getCurrentLineNumber());
            }
            if (!validateValues(values, result, reader.getCurrentLineNumber(),
                    colNames, colTypes)) {
                return;
            }
        }
    }

    private boolean validateValues(List<String> values,
            FileValidationResult result, int currentLineNumber,
            String[] colNames, Class<?>[] colTypes) {
        boolean passed = true;
        for (int i = 0; i < colNames.length; ++i) {
            if (colTypes[i] == String.class) {
                continue;
            } else if (colTypes[i] == Integer.class) {
                try {
                    Integer.parseInt(values.get(i));
                } catch (NumberFormatException e) {
                    ValidationMessage error = result.addMessage(
                            ValidationMessage.Type.ERROR,
                            "Expected integer but found " + values.get(i));
                    error.setLine(currentLineNumber);
                    passed = false;
                }
            }
        }
        return passed;
    }

    boolean isHeaderLine(List<String> values, String[] colNames) {
        if (values.size() != colNames.length) {
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            if (colNames[i] != null
                    && !values.get(i).equalsIgnoreCase(colNames[i])) {
                return false;
            }
        }
        return true;
    }

    private static String[] ndfColumnNames = new String[] { "PROBE_DESIGN_ID",
            "CONTAINER", "DESIGN_NOTE", "SELECTION_CRITERIA", "SEQ_ID",
            "PROBE_SEQUENCE", "MISMATCH", "MATCH_INDEX", "FEATURE_ID",
            "ROW_NUM", "COL_NUM", "PROBE_CLASS", "PROBE_ID", "POSITION",
            "DESIGN_ID", "X", "Y" };

    private static Class<?>[] ndfColumnTypes = new Class[] { String.class,
            String.class, String.class, String.class, String.class,
            String.class, Integer.class, Integer.class, String.class,
            Integer.class, Integer.class, String.class, String.class,
            Integer.class, String.class, Integer.class, Integer.class };

    private static String[] ngdColumnNames = new String[] { "SEQ_ID", null };

    private static Class<?>[] ngdColumnTypes = new Class[] {
            String.class,
            String.class
    };

    private static String[] posColumnNames = new String[] {
        "PROBE_DESIGN_ID",
            "CONTAINER", "DESIGN_NOTE", "SELECTION_CRITERIA", "SEQ_ID",
            "PROBE_SEQUENCE", "MISMATCH", "MATCH_INDEX", "FEATURE_ID",
            "ROW_NUM", "COL_NUM", "PROBE_CLASS", "PROBE_ID", "POSITION",
            "DESIGN_ID", "X", "Y" };

    private static Class<?>[] posColumnTypes = new Class[] {
        String.class,
            String.class, String.class, String.class, String.class,
            String.class, Integer.class, Integer.class, String.class,
            Integer.class, Integer.class, String.class, String.class,
            Integer.class, String.class, Integer.class, Integer.class };

    private List<String> getHeaders(DelimitedFileReader reader,
            String[] colNames) throws IOException {
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isHeaderLine(values, colNames)) {
                return values;
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
