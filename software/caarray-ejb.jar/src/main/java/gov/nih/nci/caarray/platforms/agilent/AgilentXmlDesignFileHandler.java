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
package gov.nih.nci.caarray.platforms.agilent;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.Inject;

/**
 * @author jscott
 *
 */
class AgilentXmlDesignFileHandler extends AbstractDesignFileHandler {
    static final String LSID_AUTHORITY = "Agilent.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final int BATCH_SIZE = 1000;
 
    private final VocabularyDao vocabularyDao;
    private CaArrayFile designFile;
    private File fileOnDisk;
    private ArrayDesignDetails arrayDesignDetails;
    private Collection<Feature> features;
    private Collection<PhysicalProbe> probes;
    private Collection<ProbeGroup> probeGroups;
    private Reader inputReader = null;
    private AgilentGELMTokenizer tokenizer = null;
    
    /**
     * @param sessionTransactionManager the SessionTransactionManager to use
     * @param fileManager the FileManager to use
     * @param arrayDao the ArrayDao to use
     * @param searchDao the SearchDao to use
     */
    @Inject
    protected AgilentXmlDesignFileHandler(SessionTransactionManager sessionTransactionManager, FileManager fileManager,
            ArrayDao arrayDao, SearchDao searchDao, VocabularyDao vocabularyDao) {
        super(sessionTransactionManager, fileManager, arrayDao, searchDao);
        this.vocabularyDao = vocabularyDao;
    }

    /**
     * {@inheritDoc}
     */
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1) {
            return false;
        }
        
        this.designFile = designFiles.iterator().next();
        
        if (designFile.getFileType() != FileType.AGILENT_XML) {
            return false;
        }
              
        try {
            this.fileOnDisk = getFileManager().openFile(designFile);
            inputReader = new FileReader(this.fileOnDisk);
            tokenizer = new AgilentGELMTokenizer(inputReader);
            
           return true;
        } catch (Exception e) {
            closeFiles();

            throw new PlatformFileReadException(this.fileOnDisk,
                        "Could not open reader for file " + designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void validate(ValidationResult result) throws PlatformFileReadException {
        try {
            FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.fileOnDisk);
            designFile.setValidationResult(fileResult);
            
            AgilentGELMParser parser = new AgilentGELMParser(tokenizer);
            
            if (!parser.validate()) {
                fileResult.addMessage(ValidationMessage.Type.ERROR, "Validation failed.");
            }
        } catch (Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                        "Unexpected error while validating " + designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void load(ArrayDesign arrayDesign) throws PlatformFileReadException {
        try {
            arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
            arrayDesign.setLsidForEntity(
                    String.format("%s:%s:%s", LSID_AUTHORITY, LSID_NAMESPACE, arrayDesign.getName()));
            getArrayDao().save(arrayDesign);
        } catch (Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                        "Unexpected error while loading " + designFile.getName(), e);
        }
    }
    
   /**
     * {@inheritDoc}
     */
    public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {
        parseArrayDesign(arrayDesign);
        try {
            arrayDesign.setNumberOfFeatures(features.size());       
            arrayDesign.setDesignDetails(this.arrayDesignDetails);
            getArrayDao().save(arrayDesign);
            getSessionTransactionManager().flushSession();
            saveEntities(true, features);
            features = null;
            saveEntities(true, probeGroups);
            probeGroups = null;
            saveEntities(false, probes);
            flushAndClearSession();
            probes = null;
        } catch (Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                        "Unexpected error while validating " + designFile.getName(), e);
        }
    }
    
    private void parseArrayDesign(ArrayDesign arrayDesign) throws PlatformFileReadException {
        boolean parseResult;
        
        try {
            AgilentGELMParser parser = new AgilentGELMParser(tokenizer);

            ArrayDesignBuilderImpl builder = new ArrayDesignBuilderImpl(vocabularyDao);
            parseResult = parser.parse(builder);
            
            this.arrayDesignDetails = builder.getArrayDesignDetails();
            this.features = builder.getFeatures();
            this.probes = builder.getPhysicalProbes().values();
            this.probeGroups = builder.getProbeGroups().values();           
        } catch (Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not parse file " + designFile.getName(), e);
        }
        
        if (!parseResult) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not load file " + designFile.getName());
        }
    }

    private void saveEntities(final boolean shouldBatchFlushAndClear,
            final Collection<? extends PersistentObject> persistentObjects) {
        int count = 0;
        for (PersistentObject persistentObject : persistentObjects) {
            getArrayDao().save(persistentObject);
            if (shouldBatchFlushAndClear && ++count % BATCH_SIZE == 0) {
                flushAndClearSession();
            }
        }       
    }

    /**
     * {@inheritDoc}
     */
    public void closeFiles() {
        try {
            closeTokenizer();
            closeReader();
            getFileManager().closeFile(this.designFile);
       } catch (PlatformFileReadException e) {
            // Closing anyway, ignore the exception and move on
           return;
        }
    }

    private void closeReader() throws PlatformFileReadException {
        try {
            if (null != inputReader) {
                inputReader.close();
                inputReader = null;
            }
        } catch (IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                    "Could not close file " + designFile.getName(), e);
        }
    }

    private void closeTokenizer() throws PlatformFileReadException {
        if (null != tokenizer) {
            tokenizer.close();
            tokenizer = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean parsesData() {
         return true;
    }
}
