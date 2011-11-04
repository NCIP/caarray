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
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
public final class IlluminaCsvDesignHandler extends AbstractDesignFileHandler {
    static final String LSID_AUTHORITY = "illumina.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final int LOGICAL_PROBE_BATCH_SIZE = 1000;

    /**
     * File Type for illumina CSV array design.
     */
    public static final FileType DESIGN_CSV_FILE_TYPE = new FileType("ILLUMINA_DESIGN_CSV", FileCategory.ARRAY_DESIGN,
            true, "CSV");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(DESIGN_CSV_FILE_TYPE);

    private CaArrayFile designFile;
    private File fileOnDisk;
    private DelimitedFileReader reader;
    private AbstractCsvDesignHelper helper;

    /**
     * 
     */
    @Inject
    IlluminaCsvDesignHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
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
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1
                || !SUPPORTED_TYPES.contains(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.designFile = designFiles.iterator().next();
        this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
        try {
            this.reader = new DelimitedFileReaderFactoryImpl().createCommaDelimitedFileReader(this.fileOnDisk);
            this.helper = createHelper();
            return true;
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not open reader for file "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
        this.helper = null;
        this.fileOnDisk = null;
        if (this.designFile != null) {
            getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
        }
        this.designFile = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
         return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) {
        try {
            positionAtAnnotation();
            final ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(arrayDesign);
            getArrayDao().flushSession();
            int count = 0;
            while (this.reader.hasNextLine()) {
                final List<String> values = this.reader.nextLine();
                if (this.helper.isLineFollowingAnnotation(values)) {
                    break;
                }
                getArrayDao().save(this.helper.createProbe(details, values));
                if (++count % LOGICAL_PROBE_BATCH_SIZE == 0) {
                    flushAndClearSession();
                }
            }
            flushAndClearSession();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) {
        try {
            final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
            this.designFile.setValidationResult(fileResult);

            this.reader.reset();
            if (!this.reader.hasNextLine()) {
                fileResult.addMessage(ValidationMessage.Type.ERROR, "Illumina CSV file was empty");
            }
            final List<String> headers = getHeaders();
            validateHeader(headers, fileResult);
            if (result.isValid()) {
                validateContent(fileResult, headers);
            }
        } catch (final IOException e) {
            result.addMessage(this.designFile.getName(), ValidationMessage.Type.ERROR, "Unable to read file");
        }
    }

    private void validateContent(FileValidationResult result, List<String> headers) throws IOException {
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (this.helper.isLineFollowingAnnotation(values)) {
                break;
            }
            if (values.size() != headers.size()) {
                final ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                        "Invalid number of fields. Expected " + headers.size() + " but contained " + values.size());
                error.setLine(this.reader.getCurrentLineNumber());
            }
            this.helper.validateValues(values, result, this.reader.getCurrentLineNumber());
        }
    }

    void validateFieldLength(List<String> values, Enum header, FileValidationResult result, int lineNumber,
            int expectedLength) throws IOException {
        final int colIdx = this.helper.indexOf(header);
        final String val = values.get(colIdx);
        validateFieldLength(val, header, result, lineNumber, expectedLength, colIdx + 1);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    static void validateFieldLength(String value, Enum header, FileValidationResult result, int lineNumber,
            int expectedLength, int col) {
        if (value.length() != expectedLength) {
            final ValidationMessage error = result.addMessage(
                    ValidationMessage.Type.ERROR,
                    "Expected size of field for " + header.name() + " to be " + expectedLength + " but was "
                    + value.length());
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    void validateIntegerField(List<String> values, Enum[] headers, Enum header, FileValidationResult result,
            int lineNumber) throws IOException {
        final int colIdx = this.helper.indexOf(header);
        final String val = values.get(colIdx);
        validateIntegerField(val, header, result, lineNumber, colIdx + 1);
    }

    static void validateIntegerField(String value, Enum header, FileValidationResult result, int lineNumber, int col) {
        if (!Utils.isInteger(value)) {
            final ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected integer value for " + header.name() + ", but was " + value);
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    void validateLongField(List<String> values, Enum header, FileValidationResult result, int lineNumber)
    throws IOException {
        final int colIdx = this.helper.indexOf(header);
        final String val = values.get(colIdx);
        validateLongField(val, header, result, lineNumber, colIdx + 1);
    }

    static void validateLongField(String value, Enum header, FileValidationResult result, int lineNumber, int col) {
        if (!Utils.isLong(value)) {
            final ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected long integral value for " + header.name() + ", but was " + value);
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    private void validateHeader(List<String> headers, FileValidationResult result) throws IOException {
        final Set<? extends Enum> requiredHeaders = this.helper.getRequiredColumns();
        final Set<Enum> tmp = new HashSet<Enum>(requiredHeaders);
        for (final String v : headers) {
            for (final Enum h : requiredHeaders) {
                if (h.name().equalsIgnoreCase(v)) {
                    tmp.remove(h);
                }
            }
        }
        if (!tmp.isEmpty()) {
            result.addMessage(ValidationMessage.Type.ERROR, "Illumina CSV file didn't contain the expected columns "
                    + tmp.toString());
        }
    }

    private List<String> getHeaders() throws IOException {
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (this.helper.isHeaderLine(values)) {
                this.helper.initHeaderIndex(values);
                return values;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesign.getName());
        try {
            arrayDesign.setNumberOfFeatures(getNumberOfFeatures());
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    private int getNumberOfFeatures() throws IOException {
        positionAtAnnotation();
        int numberOfFeatures = 0;
        while (this.reader.hasNextLine()) {
            if (this.helper.isLineFollowingAnnotation(this.reader.nextLine())) {
                break;
            }
            numberOfFeatures++;
        }
        return numberOfFeatures;
    }

    private void positionAtAnnotation() throws IOException {
        reset();
        while (this.reader.hasNextLine()) {
            final List<String> line = this.reader.nextLine();
            if (this.helper.isHeaderLine(line)) {
                this.helper.initHeaderIndex(line);
                return;
            }
        }
    }

    private void reset() {
        try {
            this.reader.reset();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't reset file " + this.designFile.getName(), e);
        }
    }

    private AbstractCsvDesignHelper createHelper() throws IOException {
        final List<AbstractCsvDesignHelper> candidateHandlers = getCandidateHandlers();
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            for (final AbstractCsvDesignHelper candidateHandler : candidateHandlers) {
                if (candidateHandler.isHeaderLine(values)) {
                    return candidateHandler;
                }
            }
        }
        throw new IOException("Could not find helper for this CSV design; the headers were wrong");
    }

    private List<AbstractCsvDesignHelper> getCandidateHandlers() {
        final List<AbstractCsvDesignHelper> handlers = new ArrayList<AbstractCsvDesignHelper>();
        handlers.add(new ExpressionCsvDesignHelper());
        handlers.add(new GenotypingCsvDesignHandler());
        return handlers;
    }
}
