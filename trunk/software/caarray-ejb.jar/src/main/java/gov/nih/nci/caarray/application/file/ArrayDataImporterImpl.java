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
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Manages import of array data files.
 */
final class ArrayDataImporterImpl implements ArrayDataImporter {
    private static final Logger LOG = Logger.getLogger(ArrayDataImporterImpl.class);

    private final ArrayDataService arrayDataService;

    private final FileDao fileDao;
    private final ProjectDao projectDao;
    private final SearchDao searchDao;

    @Inject
    ArrayDataImporterImpl(ArrayDataService arrayDataService, FileDao fileDao, ProjectDao projectDao, 
            SearchDao searchDao) {
        super();
        this.arrayDataService = arrayDataService;
        this.fileDao = fileDao;
        this.projectDao = projectDao;
        this.searchDao = searchDao;
    }

    @Override
    public void importFiles(CaArrayFileSet fileSet, DataImportOptions dataImportOptions, MageTabDocumentSet mTabSet) {
        final Set<CaArrayFile> dataFiles = fileSet.getArrayDataFiles();
        fileSet.getFiles().clear();
        int fileCount = 0;
        final int totalNumberOfFiles = dataFiles.size();
        for (final Iterator<CaArrayFile> fileIt = dataFiles.iterator(); fileIt.hasNext();) {
            final CaArrayFile file = fileIt.next();
            LOG.info("Importing data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
            importFile(file, dataImportOptions, mTabSet);
            fileIt.remove();
        }
    }

    private void importFile(CaArrayFile file, DataImportOptions dataImportOptions, MageTabDocumentSet mTabSet) {
        try {
            this.searchDao.refresh(file);
            this.arrayDataService.importData(file, true, dataImportOptions, mTabSet);
        } catch (final InvalidDataFileException e) {
            file.setFileStatus(FileStatus.VALIDATION_ERRORS);
            file.setValidationResult(e.getFileValidationResult());
        }
        this.projectDao.save(file);
        this.projectDao.flushSession();
        this.projectDao.clearSession();
    }

    @Override
    public void validateFiles(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet, boolean reimport) {
        final Set<CaArrayFile> dataFiles = fileSet.getArrayDataFiles();
        final Set<CaArrayFile> sdrfFiles = fileSet.getFilesByType(FileTypeRegistry.MAGE_TAB_SDRF);
        int fileCount = 0;
        final int totalNumberOfFiles = dataFiles.size();
        for (final CaArrayFile file : dataFiles) {
            if (file.getFileStatus() != FileStatus.VALIDATION_ERRORS) {
                LOG.info("Validating data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
                validateFile(file, mTabSet, reimport);
            }
        }
        checkSdrfHybNames(dataFiles, mTabSet, sdrfFiles);
    }

    private void validateFile(CaArrayFile file, MageTabDocumentSet mTabSet, boolean reimport) {
        this.arrayDataService.validate(file, mTabSet, reimport);
    }

    private void checkSdrfHybNames(Set<CaArrayFile> dataFiles, MageTabDocumentSet mTabSet, Set<CaArrayFile> sdrfFiles) {
        if (mTabSet != null) {
            checkSdrfDataRelationshipNodeNames(dataFiles, mTabSet.getSdrfHybridizations(), sdrfFiles,
                    FileValidationResult.HYB_NAME);
        }
    }

    private <T extends AbstractSampleDataRelationshipNode> List<String> getNodeValueNames(String key,
            Map<String, List<T>> values) {
        final List<String> names = new ArrayList<String>();
        final List<T> mageTabNodeList = values.get(key);
        for (final T node : mageTabNodeList) {
            names.add(node.getName());
        }

        return names;
    }

    private <T extends AbstractSampleDataRelationshipNode> Map<String, List<T>> pruneOutDataFileNodeNames(
            Map<String, List<T>> sdrfNodeNames, List<String> dataFileNodeNames) {

        final Map<String, List<T>> updatedMap = new HashMap<String, List<T>>();

        for (final String key : sdrfNodeNames.keySet()) {
            final List<T> updatedList = new ArrayList<T>();
            final List<T> mageTabNodeList = sdrfNodeNames.get(key);

            for (final T sdrfNodeName : mageTabNodeList) {
                if (!dataFileNodeNames.contains(sdrfNodeName.getName())) {
                    updatedList.add(sdrfNodeName);
                }
            }

            updatedMap.put(key, updatedList);

        }
        return updatedMap;
    }

    private <T extends AbstractSampleDataRelationshipNode> void checkSdrfDataRelationshipNodeNames(
            Set<CaArrayFile> dataFiles, Map<String, List<T>> values, Set<CaArrayFile> sdrfFiles, String keyName) {
        // generate list of node names from data files and compare
        // to list of node names from the sdrf

        final List<String> dataFileNodeNames = collectDataRelationshipNodeNames(dataFiles, keyName);

        if (!dataFileNodeNames.isEmpty() && !values.isEmpty() && !values.values().isEmpty()) {

            doAddSdrfErrors(pruneOutDataFileNodeNames(values, dataFileNodeNames), keyName, sdrfFiles);
        }
    }

    private List<String> collectDataRelationshipNodeNames(Set<CaArrayFile> dataFiles, String keyName) {
        final List<String> dataFileRelationshipNodeNames = new ArrayList<String>();
        for (final CaArrayFile caf : dataFiles) {
            if (caf.getValidationResult() != null) {
                dataFileRelationshipNodeNames.addAll(getRelationshipNodeName(caf.getValidationResult(), keyName));
            }
        }

        return dataFileRelationshipNodeNames;
    }

    @SuppressWarnings("unchecked")
    private List<String> getRelationshipNodeName(FileValidationResult result, String keyName) {
        final List<String> names = new ArrayList<String>();
        if (result != null && result.getValidationProperties(keyName) != null) {
            names.addAll((List<String>) result.getValidationProperties(keyName));
        }
        return names;
    }

    private <T extends AbstractSampleDataRelationshipNode> void doAddSdrfErrors(Map<String, List<T>> values,
            String materialName, Set<CaArrayFile> sdrfFiles) {

        if (!values.isEmpty() && !values.values().isEmpty()) {
            // add error to matching SDRF
            for (final String mtSdrfName : values.keySet()) {
                doAddSdrfError(mtSdrfName, values, materialName, sdrfFiles);
            }
        }
    }

    private <T extends AbstractSampleDataRelationshipNode> void doAddSdrfError(String mtSdrfName,
            Map<String, List<T>> values, String materialName, Set<CaArrayFile> sdrfFiles) {
        for (final CaArrayFile sdrf : sdrfFiles) {
            final List<String> mtSdrfNodeNameValues = getNodeValueNames(mtSdrfName, values);
            if (mtSdrfName.equals(sdrf.getName()) && !mtSdrfNodeNameValues.isEmpty()) {
                sdrf.setFileStatus(FileStatus.VALIDATION_ERRORS);
                final StringBuilder errorMessage = new StringBuilder(materialName);
                errorMessage.append(" ");
                errorMessage.append(mtSdrfNodeNameValues.toString());
                errorMessage.append(" were not found in data files provided.");
                if (sdrf.getValidationResult() == null) {
                    sdrf.setValidationResult(new FileValidationResult());
                }
                sdrf.getValidationResult().addMessage(Type.ERROR, errorMessage.toString());
                this.fileDao.save(sdrf);
            }
        }
    }
}
