/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplicable;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.web.action.CaArrayActionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.Transformer;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;

/**
 * @author Scott Miller
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public abstract class AbstractProjectProtocolAnnotationListTabAction extends AbstractProjectListTabAction {
    static final Transformer FILE_TYPE_TRANSFORMER = new Transformer() {
        /**
         * Transforms files to their extensions.
         */
        public Object transform(Object o) {
            CaArrayFile f = (CaArrayFile) o;
            return f.getFileType().getName();
        }
    };
    private static final String UNKNOWN_FILE_TYPE = "(Unknown File Types)";
    private static final String KNOWN_FILE_TYPE = "(Supported File Types)";

    private static final String IMPORTED = "IMPORTED";
    private static final String VALIDATED = "VALIDATED";

    private Term protocolType;
    private Set<Term> protocolTypes;
    private List<Protocol> selectedProtocols = new ArrayList<Protocol>();
    private List<Protocol> protocols = new ArrayList<Protocol>();
    private String protocolName;
    private List<DownloadGroup> downloadFileGroups = new ArrayList<DownloadGroup>();
    private Set<String> fileTypes = new TreeSet<String>();
    private List<String> fileStatuses = new ArrayList<String>();
    private String fileType;
    private int downloadGroupNumber = -1;


    private Set<CaArrayFile> files = new HashSet<CaArrayFile>();

    /**
     * A simple enum to use instead of string literals.
     */
    enum BioMaterialTypes {
        /**
         * Sources.
         */
        SOURCES("Sources"),
        /**
         * Samples.
         */
        SAMPLES("Samples"),
        /**
         * Extracts.
         */
        EXTRACTS("Extracts"),
        /**
         * Labeled Extracts.
         */
        LABELED_EXTRACTS("LabeledExtracts"),
        /**
         * Hybridizations.
         */
        HYBRIDIZATIONS("Hybridizations");
        private final String type;

        private BioMaterialTypes(String type) {
            this.type = type;
        }

        /**
         * @return pretty type name
         */
        public String getType() {
            return type;
        }
    }

    /**
     * default constructor.
     *
     * @param resourceKey the base resource key.
     * @param pagedItems the paged list to use for this tab's item list
     */
    public AbstractProjectProtocolAnnotationListTabAction(String resourceKey,
            SortablePaginatedList<? extends PersistentObject, ?> pagedItems) {
        super(resourceKey, pagedItems);
    }

    private void initForm() {
        setProtocolTypes(CaArrayActionHelper.getTermsFromCategory(ExperimentOntologyCategory.PROTOCOL_TYPE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String edit() {
        setSelectedProtocols(getCurrentProtocols());
        initForm();
        return super.edit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public String save() {
        ProtocolApplicable protApplicable = (ProtocolApplicable) getItem();
        List<ProtocolApplication> currentProtocolApplications = new ArrayList<ProtocolApplication>(
                getCurrentProtocolApplications());
        List<Protocol> currentProtocols = new ArrayList<Protocol>(getCurrentProtocols());
        protApplicable.clearProtocolApplications();
        for (Protocol selectedProtocol : getSelectedProtocols()) {
            if (currentProtocols.contains(selectedProtocol)) {
                for (ProtocolApplication currProtocolApplication : currentProtocolApplications) {
                    if (currProtocolApplication.getProtocol().equals(selectedProtocol)) {
                        protApplicable.addProtocolApplication(currProtocolApplication);
                        break;
                    }
                }
            } else {
                ProtocolApplication protocolApplication = new ProtocolApplication();
                protocolApplication.setProtocol(selectedProtocol);
                protApplicable.addProtocolApplication(protocolApplication);
            }
        }

        return super.save();
    }

    /**
     * Retrieve the XML protocol list.
     * @return the string
     */
    @SkipValidation
    public String retrieveXmlProtocolList() {
        setProtocols(CaArrayActionHelper.getVocabularyService().getProtocolsByProtocolType(getProtocolType(),
                getProtocolName()));
        return "protocolAutoCompleterValues";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (hasErrors()) {
            initForm();
        }
    }

    /**
     * @return the protocolType
     */
    public Term getProtocolType() {
        return this.protocolType;
    }

    /**
     * @param protocolType the protocolType to set
     */
    public void setProtocolType(Term protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * @return the selectedProtocols
     */
    public List<Protocol> getSelectedProtocols() {
        return this.selectedProtocols;
    }

    /**
     * @param selectedProtocols the selectedProtocols to set
     */
    public void setSelectedProtocols(List<Protocol> selectedProtocols) {
        this.selectedProtocols = selectedProtocols;
    }

    /**
     * @return the protocolTypes
     */
    public Set<Term> getProtocolTypes() {
        return this.protocolTypes;
    }

    /**
     * @param protocolTypes the protocolTypes to set
     */
    public void setProtocolTypes(Set<Term> protocolTypes) {
        this.protocolTypes = protocolTypes;
    }

    /**
     * @return the protocols
     */
    public List<Protocol> getProtocols() {
        return this.protocols;
    }

    /**
     * @param protocols the protocols to set
     */
    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    /**
     * The current protocols on the selected item.
     * @return the protocols
     */
    public List<Protocol> getCurrentProtocols() {
        List<Protocol> currentProtocols = new ArrayList<Protocol>();
        for (ProtocolApplication pa : getCurrentProtocolApplications()) {
            currentProtocols.add(pa.getProtocol());
        }
        return currentProtocols;
    }

    /**
     * does nothing, only here so it can be referenced in the jsp as a bean property.
     * @param p ignored
     */
    public void setCurrentProtocols(List<Protocol> p) {
        // does nothing, only here so it can be referenced in the jsp as a bean property
    }

    /**
     * The current protocol applications on the selected item.
     * @return the protocol applications
     */
    public List<ProtocolApplication> getCurrentProtocolApplications() {
        ProtocolApplicable protApplicable = (ProtocolApplicable) getItem();
        return protApplicable.getProtocolApplications();
    }

    /**
     * does nothing, only here so it can be referenced in the jsp as a bean property.
     * @param p ignored
     */
    public void setCurrentProtocolApplications(List<ProtocolApplication> p) {
        // does nothing, only here so it can be referenced in the jsp as a bean property
    }

    /**
     * @return the protocolName
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * @param protocolName the protocolName to set
     */
    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    /**
     * @return the downloadFileGroups
     */
    public List<DownloadGroup> getDownloadFileGroups() {
        return downloadFileGroups;
    }

    /**
     * @param downloadFileGroups the downloadFileGroups to set
     */
    protected void setDownloadFileGroups(List<DownloadGroup> downloadFileGroups) {
        this.downloadFileGroups = downloadFileGroups;
    }

    /**
     * @return the downloadGroupNumber
     */
    public int getDownloadGroupNumber() {
        return downloadGroupNumber;
    }

    /**
     * @param downloadGroupNumber the downloadGroupNumber to set
     */
    public void setDownloadGroupNumber(int downloadGroupNumber) {
        this.downloadGroupNumber = downloadGroupNumber;
    }


    /**
     * @return files to show for download
     */
    public Set<CaArrayFile> getFiles() {
        return this.files;
    }

    /**
     * @param files to show for download
     */
    public void setFiles(Set<CaArrayFile> files) {
        this.files = files;
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String downloadFiles() {
        for (CaArrayFile f : getAllDataFiles()) {
            if (getFileType() == null
                    || (f.getFileType() != null
                            && f.getFileType().toString().equals(getFileType())
                    || (KNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() != null)
                    || (UNKNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() == null))) {
                getFiles().add(f);
            }
        }
        initFileTypes();
        return "downloadFiles";
    }
    private void initFileTypes() {
        setFileTypeNamesAndStatuses(getAllDataFiles());
    }

    private void setFileTypeNamesAndStatuses(Collection<CaArrayFile> fileSet) {
        Set<String> fileTypeNames = new TreeSet<String>();
        List<String> fileStatusList = new ArrayList<String>();
        for (CaArrayFile file : fileSet) {
            if (file.getFileType() != null) {
                fileTypeNames.add(file.getFileType().getName());
            }
            if (file.getStatus().contains(IMPORTED) && !fileStatusList.contains(IMPORTED)) {
                fileStatusList.add(IMPORTED);
            } else if (file.getStatus().contains(VALIDATED) && !fileStatusList.contains(VALIDATED)) {
                fileStatusList.add(VALIDATED);
            } else if (!fileStatusList.contains(file.getStatus())) {
                fileStatusList.add(file.getStatus());
            }
        }
        fileTypeNames.add(KNOWN_FILE_TYPE);
        fileTypeNames.add(UNKNOWN_FILE_TYPE);
        setFileTypes(fileTypeNames);
        setFileStatuses(fileStatusList);
    }

    /**
     * @return all downloadable data files for the page's primary entity
     */
    protected abstract Collection<CaArrayFile> getAllDataFiles();

    /**
     * Ajax-only call to handle changing the filter extension.
     * @return success.
     */
    @SkipValidation
    public String downloadFilesList() {
        downloadFiles();
        return "downloadFilesList";
    }

    /**
     * Ajax-only call to handle sorting.
     *
     * @return success
     */
    @SkipValidation
    public String downloadFilesListTable() {
        downloadFiles();
        return "downloadFilesListTable";
    }

    /**
     * @return the action to handle filtering downloadable files by file type
     */
    public abstract String getDownloadFileListAction();

    /**
     * @return the action to handle sorting downloadable files
     */
    public abstract String getDownloadFilesTableListSortUrlAction();

    /**
     * @param type [Sources|Samples|Extracts|LabeledExtracts|Hybridization]
     * @return the action to handle filtering downloadable files by file type
     */
    protected String getDownloadFileListActionUrl(BioMaterialTypes type) {
        return "/ajax/project/listTab/" + type.getType() + "/downloadFilesList.action";
    }

    /**
     * @param type [Sources|Samples|Extracts|LabeledExtracts|Hybridization]
     * @return the action to handle sorting downloadable files
     */
    protected String getDownloadFileTableListSortActionUrl(BioMaterialTypes type) {
        return "/ajax/project/listTab/" + type.getType() + "/downloadFilesListTable.action";
    }

    /**
     * Used to render the proper action for the download all files for a Annotation
     * (ref:projectListTabDownloadColumn.tag).
     *
     * @param files all the files for the item in question
     * @return true if the all files to download for the current item (source, sample, etc..) should use the download
     *         groups mechanism
     */
    public static boolean isWillPerformDownloadByGroups(Collection<CaArrayFile> files) {
        List<DownloadGroup> computeDownloadGroups = ProjectFilesAction.computeDownloadGroups(files);
        return computeDownloadGroups.size() > 1 ? true : false;
    }

    /**
     * @return the fileTypes
     */
    public Set<String> getFileTypes() {
        return fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(Set<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the fileStatuses
     */
    public List<String> getFileStatuses() {
        return fileStatuses;
    }

    /**
     * @param fileStatuses the fileStatuses to set
     */
    public void setFileStatuses(List<String> fileStatuses) {
        this.fileStatuses = fileStatuses;
    }
}
