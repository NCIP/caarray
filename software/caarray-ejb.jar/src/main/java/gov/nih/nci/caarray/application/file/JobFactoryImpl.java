//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.util.CaArrayFileSetSplitter;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Creates jobs.
 * @author jscott
 *
 */
public class JobFactoryImpl implements JobFactory {
    private final Provider<ArrayDao> arrayDaoProvider;
    private final Provider<ArrayDataImporter> arrayDataImporterProvider;
    private final Provider<MageTabImporter> mageTabImporterProvider;
    private final Provider<ProjectDao> projectDaoProvider;
    private final Provider<SearchDao> searchDaoProvider;
    private final Provider<FileAccessService> fileAccessServiceProvider;
    private final Provider<CaArrayFileSetSplitter> caArrayFileSetSplitterProvider;


    /**
     * @param arrayDaoProvider the Provider&lt;ArrayDao&gt; dependency
     * @param arrayDataImporterProvider the Provider&lt;ArrayDataImporter&gt; dependency
     * @param mageTabImporterProvider the Provider&lt;MageTabImporter&gt; dependency
     * @param projectDaoProvider the Provider&lt;ProjectDao&gt; dependency
     * @param searchDaoProvider the Provider&lt;SearchDao&gt; dependency
     */
    @Inject
    @SuppressWarnings("PMD.ExcessiveParameterList")
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    public JobFactoryImpl(Provider<ArrayDao> arrayDaoProvider,
            Provider<ArrayDataImporter> arrayDataImporterProvider, Provider<MageTabImporter> mageTabImporterProvider,
            Provider<FileAccessService> fileAccessServiceProvider, Provider<ProjectDao> projectDaoProvider,
            Provider<SearchDao> searchDaoProvider, Provider<CaArrayFileSetSplitter> caArrayFileSetSplitter) {
    // CHECKSTYLE:ON
        this.arrayDaoProvider = arrayDaoProvider;
        this.arrayDataImporterProvider = arrayDataImporterProvider;
        this.mageTabImporterProvider = mageTabImporterProvider;
        this.fileAccessServiceProvider = fileAccessServiceProvider;
        this.projectDaoProvider = projectDaoProvider;
        this.searchDaoProvider = searchDaoProvider;
        this.caArrayFileSetSplitterProvider = caArrayFileSetSplitter;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractFileManagementJob createArrayDesignFileImportJob(String user, ArrayDesign arrayDesign) {
        return new ArrayDesignFileImportJob(user, arrayDesign, arrayDaoProvider.get(), fileAccessServiceProvider.get());
    }

    /**
     * {@inheritDoc}
     */
    public ProjectFilesImportJob createProjectFilesImportJob(String user, Project project, CaArrayFileSet fileSet,
            DataImportOptions dataImportOptions) {
        return new ProjectFilesImportJob(user, project, fileSet, dataImportOptions,
                arrayDataImporterProvider.get(), mageTabImporterProvider.get(),
                fileAccessServiceProvider.get(), projectDaoProvider.get(),
                searchDaoProvider.get());
    }

    /**
     * {@inheritDoc}
     */
    public ProjectFilesValidationJob createProjectFilesValidationJob(String user, Project project,
            CaArrayFileSet fileSet) {
        return new ProjectFilesValidationJob(user, project, fileSet,
                arrayDataImporterProvider.get(), mageTabImporterProvider.get(),
                fileAccessServiceProvider.get(), projectDaoProvider.get(), searchDaoProvider.get());
    }

    /**
     * {@inheritDoc}
     */
    public ProjectFilesReparseJob createProjectFilesReparseJob(String user, Project project,
            CaArrayFileSet fileSet) {
        return new ProjectFilesReparseJob(user, project, fileSet,
                arrayDataImporterProvider.get(), mageTabImporterProvider.get(),
                fileAccessServiceProvider.get(), projectDaoProvider.get(), searchDaoProvider.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectFilesSplitJob createProjectFilesSplitJob(String user, Project project, CaArrayFileSet fileSet,
            DataImportOptions dataImportOptions, FileManagementJobSubmitter submitter) {
        return new ProjectFilesSplitJob(user, project, fileSet, arrayDataImporterProvider.get(),
                mageTabImporterProvider.get(), fileAccessServiceProvider.get(), projectDaoProvider.get(),
                searchDaoProvider.get(), dataImportOptions, caArrayFileSetSplitterProvider.get(), submitter);
    }

}
