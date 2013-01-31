//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
