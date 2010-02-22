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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.util.Utils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
final class IlluminaCsvDesignHandler extends AbstractArrayDesignHandler {

    private static final Logger LOG = Logger.getLogger(IlluminaCsvDesignHandler.class);

    static final String LSID_AUTHORITY = "illumina.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";
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
                getArrayDao().save(getHandler().createProbe(details, values));
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
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (getHandler().isLineFollowingAnnotation(values)) {
                break;
            }
            if (values.size() != headers.size()) {
                ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                        "Invalid number of fields. Expected "
                        + headers.size() + " but contained " + values.size());
                error.setLine(reader.getCurrentLineNumber());
            }
            getHandler().validateValues(values, result, reader.getCurrentLineNumber());
        }
    }

    void validateFieldLength(List<String> values, Enum header, FileValidationResult result,
            int lineNumber, int expectedLength) throws IOException {
        int colIdx = getHandler().indexOf(header);
        String val = values.get(colIdx);
        validateFieldLength(val, header, result, lineNumber, expectedLength, colIdx + 1);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    static void validateFieldLength(String value, Enum header, FileValidationResult result, int lineNumber,
            int expectedLength, int col) {
        if (value.length() != expectedLength) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected size of field for " + header.name() + " to be " + expectedLength
                    + " but was " + value.length());
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    void validateIntegerField(List<String> values, Enum[] headers, Enum header, FileValidationResult result,
            int lineNumber) throws IOException {
        int colIdx = getHandler().indexOf(header);
        String val = values.get(colIdx);
        validateIntegerField(val, header, result, lineNumber, colIdx + 1);
    }

    static void validateIntegerField(String value, Enum header, FileValidationResult result, int lineNumber, int col) {
        if (!Utils.isInteger(value)) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected integer value for " + header.name() + ", but was " +  value);
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    void validateLongField(List<String> values, Enum header, FileValidationResult result,
            int lineNumber) throws IOException {
        int colIdx = getHandler().indexOf(header);
        String val = values.get(colIdx);
        validateLongField(val, header, result, lineNumber, colIdx + 1);
    }

    static void validateLongField(String value, Enum header, FileValidationResult result, int lineNumber, int col) {
        if (!Utils.isLong(value)) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected long integral value for " + header.name() + ", but was " +  value);
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    private void validateHeader(List<String> headers, FileValidationResult result) throws IOException {
        Set<? extends Enum> requiredHeaders = getHandler().getRequiredColumns();
        Set<Enum> tmp = new HashSet<Enum>(requiredHeaders);
        for (String v : headers) {
            for (Enum h : requiredHeaders) {
                if (h.name().equalsIgnoreCase(v)) {
                    tmp.remove(h);
                }
            }
        }
        if (!tmp.isEmpty()) {
            result.addMessage(ValidationMessage.Type.ERROR,
                    "Illumina CSV file didn't contain the expected columns " + tmp.toString());
        }
    }

    private List<String> getHeaders(DelimitedFileReader reader) throws IOException {
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (getHandler().isHeaderLine(values)) {
                getHandler().initHeaderIndex(values);
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
        while (reader.hasNextLine()) {
            List<String> line = reader.nextLine();
            if (getHandler().isHeaderLine(line)) {
                getHandler().initHeaderIndex(line);
                return;
            }
        }
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't reset file " + getDesignFile().getName(), e);
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
