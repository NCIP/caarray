/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
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
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author jscott
 * 
 */
public class AgilentXmlDesignFileHandler extends AbstractDesignFileHandler implements FeatureCountListener {
    static final String LSID_AUTHORITY = "Agilent.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";

    /**
     * File Type for AGILENT_XML array design.
     */
    public static final FileType XML_FILE_TYPE = new FileType("AGILENT_XML", FileCategory.ARRAY_DESIGN, true);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(XML_FILE_TYPE);

    private final VocabularyDao vocabularyDao;
    private CaArrayFile designFile;
    private File fileOnDisk;
    private ArrayDesignDetails arrayDesignDetails;
    private Reader inputReader = null;
    private AgilentGELMTokenizer tokenizer = null;
    private int featureCount = 0;

    /**
     * @param sessionTransactionManager the SessionTransactionManager to use
     * @param dataStorageFacade data storage facade to use
     * @param arrayDao the ArrayDao to use
     * @param searchDao the SearchDao to use
     * @param vocabularyDao the VocabularyDao to use
     */
    @Inject
    protected AgilentXmlDesignFileHandler(SessionTransactionManager sessionTransactionManager,
            DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao, VocabularyDao vocabularyDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
        this.vocabularyDao = vocabularyDao;
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
        if (designFiles == null || designFiles.size() != 1) {
            return false;
        }

        this.designFile = designFiles.iterator().next();

        if (!SUPPORTED_TYPES.contains(this.designFile.getFileType())) {
            return false;
        }

        try {
            this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
            this.inputReader = new FileReader(this.fileOnDisk);
            this.tokenizer = new AgilentGELMTokenizer(this.inputReader);

            return true;
        } catch (final Exception e) {
            closeFiles();

            throw new PlatformFileReadException(this.fileOnDisk, "Could not open reader for file "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) throws PlatformFileReadException {
        try {
            final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
            this.designFile.setValidationResult(fileResult);

            final AgilentGELMParser parser = new AgilentGELMParser(this.tokenizer);

            if (!parser.validate()) {
                fileResult.addMessage(ValidationMessage.Type.ERROR, "Validation failed.");
            }
        } catch (final Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unexpected error while validating "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) throws PlatformFileReadException {
        try {
            arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
            arrayDesign.setLsidForEntity(String.format("%s:%s:%s", LSID_AUTHORITY, LSID_NAMESPACE,
                    arrayDesign.getName()));
            getArrayDao().save(arrayDesign);
        } catch (final Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unexpected error while loading "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {
        try {
            arrayDesignDetails = new ArrayDesignDetails();
            getArrayDao().save(arrayDesignDetails);
            arrayDesign.setDesignDetails(arrayDesignDetails);
            getArrayDao().save(arrayDesign);
            parseArrayDesign(arrayDesign);
            arrayDesign.setNumberOfFeatures(Integer.valueOf(featureCount));
            getArrayDao().save(arrayDesign);
            flushAndClearSession();
        } catch (Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unexpected error while creating design details: "
                    + designFile.getName() + ": " + e.getMessage(), e);
        }
    }

    private void parseArrayDesign(ArrayDesign arrayDesign) throws PlatformFileReadException {
        boolean parseResult;

        try {
            AgilentGELMParser parser = new AgilentGELMParser(tokenizer);
            parser.addFeatureCountListener(this);

            ArrayDesignBuilderImpl builder = new ArrayDesignBuilderImpl(arrayDesignDetails, vocabularyDao,
                    getArrayDao(), getSearchDao());
            parseResult = parser.parse(builder);

        } catch (final Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                    "Could not parse file " + this.designFile.getName(), e);
        }

        if (!parseResult) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not load file " + this.designFile.getName());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        try {
            closeTokenizer();
            closeReader();
            getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
            this.fileOnDisk = null;
            this.designFile = null;
        } catch (final PlatformFileReadException e) {
            // Closing anyway, ignore the exception and move on
            return;
        }
    }

    private void closeReader() throws PlatformFileReadException {
        try {
            if (null != this.inputReader) {
                this.inputReader.close();
                this.inputReader = null;
            }
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                    "Could not close file " + this.designFile.getName(), e);
        }
    }

    private void closeTokenizer() throws PlatformFileReadException {
        if (null != this.tokenizer) {
            this.tokenizer.close();
            this.tokenizer = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
         return true;
    }

    /**
     * Increment feature count. 
     */
    public void incrementFeatureCount() {
        featureCount++;
    }
}
