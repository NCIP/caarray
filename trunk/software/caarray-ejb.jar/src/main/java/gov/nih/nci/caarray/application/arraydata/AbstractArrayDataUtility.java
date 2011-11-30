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
 */package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Base class for array data helper classes that interface with the platform handlers. Contains common functionality.
 * 
 * @author dkokotov
 */
abstract class AbstractArrayDataUtility {
    private static final Logger LOG = Logger.getLogger(AbstractArrayDataUtility.class);

    private final ArrayDao arrayDao;
    private final Set<DataFileHandler> handlers;
    private final Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider;

    /**
     * @param arrayDao
     * @param arrayDesignService
     * @param handlers
     */
    @Inject
    AbstractArrayDataUtility(ArrayDao arrayDao, Set<DataFileHandler> handlers,
            Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider) {
        this.arrayDao = arrayDao;
        this.handlers = new HashSet<DataFileHandler>(handlers);
        this.fallbackHandlerProvider = fallbackHandlerProvider;
    }

    /**
     * Find the appropriate data handler for the given data file, and initialize it.
     * 
     * @param arrayDataFile the data file to be processed
     * @param mTabSet parsed MageTabDocumentSet containing SDRF specifying data file to array design mappings. 
     * May be null if not applicable.
     * @return the DataFileHandler instance capable of processing the arrayDataFile. The handler will
     * have been initialized with arrayDataFile and mTabSet. 
     */
    protected DataFileHandler findAndSetupHandler(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet) 
            throws PlatformFileReadException {
        DataFileHandler handler = findHandlerAndSetDataFile(arrayDataFile); 
        handler.setMageTabDocumentSet(mTabSet);
        return handler;
    }
    
    /**
     * Find the appropriate data handler for the given data file.
     * 
     * @param arrayDataFile the data file to be processed
     * @return the DataFileHandler instance capable of processing the arrayDataFile. The handler will
     * have been initialized with arrayDataFile. 
     */
    protected DataFileHandler findHandlerAndSetDataFile(CaArrayFile arrayDataFile) 
            throws PlatformFileReadException {
        DataFileHandler handler = getOpeningHandler(arrayDataFile);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type " + arrayDataFile.getFileType());
        }
        if (!handler.parsesData()) {
            return handler;
        }
        final ArrayDesign ad = getArrayDesign(arrayDataFile, handler);
        if (ad == null || !ad.isImportedAndParsed()) {
            handler.closeFiles();
            handler = getUnparsedDataHandler(arrayDataFile);
        }
        return handler;
    }
    
    /**
     * Find the array design corresponding to the given data file. 
     * <ol>
     * <li>Tries to determine the design from the arrayDataFile; </li>
     * <li>Else if the data file does not encode a design reference, tries to determine it based on the 
     * designs associated with the experiment, if there is exactly one such design.</li>
     * <li>Else tries to resolve using the data file to array design mapping specified by the SDRF file(s)
     * The SDRF files, if available, are contained in the MageTabDocumentSet referenced by the handlerArg.</li> 
     * </ol>
     * 
     * @param arrayDataFile the data file
     * @param handler the handler for the data file
     * @return the array design corresponding to the file, or null if it is not found or could not be determined
     *         uniquely from the file or experiment.
     * @throws PlatformFileReadException
     * 
     */
    protected ArrayDesign getArrayDesign(CaArrayFile arrayDataFile, DataFileHandler handlerArg)
            throws PlatformFileReadException {
        ArrayDesign ad = findArrayDesignFromFile(handlerArg);
        if (ad == null) {
            ad = findArrayDesignFromExperiment(arrayDataFile.getProject().getExperiment());
        }
        if (ad == null) {
            ad = findArrayDesignViaSdrf(arrayDataFile, handlerArg.getMageTabDocumentSet());
        }
        return ad;
    }

    private ArrayDesign findArrayDesignFromFile(DataFileHandler handler) throws PlatformFileReadException {
        final List<LSID> designLsids = handler.getReferencedArrayDesignCandidateIds();
        for (final LSID lsid : designLsids) {
            final ArrayDesign ad = this.arrayDao.getArrayDesign(lsid.getAuthority(), lsid.getNamespace(),
                    lsid.getObjectId());
            if (ad != null) {
                return ad;
            }
        }
        return null;
    }

    private ArrayDesign findArrayDesignFromExperiment(Experiment experiment) {
        final Set<ArrayDesign> experimentDesigns = experiment.getArrayDesigns();
        if (experimentDesigns.size() == 1) {
            return experimentDesigns.iterator().next();
        }
        return null;
    }

    /** 
     * Iterates through all SDRF files contained in mTabSet, looking for the first array design LSID
     * that corresponds to arrayDataFile. If a match is found, the LSID is used to retrieve 
     * the corresponding ArrayDesign from the persistence layer. 
     * @param arrayDataFile
     * @param mTabSetArg
     * @return first array design found that corresponds to arrayDataFile. null if no match found 
     * or if mTabSetArg is null. 
     */
    private ArrayDesign findArrayDesignViaSdrf(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSetArg) {
        if (mTabSetArg == null) {
            return null;
        }
        String dataFileName = arrayDataFile.getName();
        LOG.info("findArrayDesignFromSdrf for arrayDataFile=[" + dataFileName + "]. ");
        for (SdrfDocument sdrf : mTabSetArg.getSdrfDocuments()) {
            String adLsidName = sdrf.getArrayDesignNameForArrayDataFileName(dataFileName);
            LOG.info(String.format(
                    "findArrayDesignFromSdrf for arrayDataFile=[%s], found matching arrayDesign LSID=[%s]. ", 
                    dataFileName, adLsidName));
            if (adLsidName != null) {
                LSID adLsid = new LSID(adLsidName);
                ArrayDesign ad = arrayDao.getArrayDesign(adLsid.getAuthority(), adLsid.getNamespace(), adLsid
                        .getObjectId());
                if (ad != null) {
                    return ad;
                } else {
                    LOG.warn(String.format(
                            "findArrayDesignFromSdrf for arrayDataFile=[%s], found matching arrayDesign LSID=[%s],"
                            + " but arrayDesign object not found in persistent store. ", 
                            dataFileName, adLsidName));
                }
            }
        }
        
        LOG.info(String.format(
                "findArrayDesignFromSdrf for arrayDataFile=[%s], found no matching arrayDesign. ", 
                dataFileName));
        return null; 
    }
    
    protected ArrayDao getArrayDao() {
        return this.arrayDao;
    }

    private DataFileHandler getUnparsedDataHandler(CaArrayFile caArrayFile) throws PlatformFileReadException {
        final DataFileHandler handler = this.fallbackHandlerProvider.get();
        handler.openFile(caArrayFile);
        return handler;
    }

    private DataFileHandler getOpeningHandler(CaArrayFile caArrayFile) throws PlatformFileReadException {
        for (final DataFileHandler handler : this.handlers) {
            try {
                if (handler.openFile(caArrayFile)) {
                    return handler;
                }
            } catch (final PlatformFileReadException e) {
                handler.closeFiles();
                throw e;
            }
        }
        return null;
    }
}
