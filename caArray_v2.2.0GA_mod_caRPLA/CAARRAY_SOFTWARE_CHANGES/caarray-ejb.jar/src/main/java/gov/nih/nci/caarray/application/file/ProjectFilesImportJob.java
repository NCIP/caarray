//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import org.apache.log4j.Logger;

//carpla_begin_add
import gov.nih.nci.carpla.rplatab.RplaTabParsingException;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import java.util.Iterator;
import java.util.Set;

//carpla_end_add
/**
 * Encapsulates the functionality necessary for importing a set of files into a
 * project.
 */
final class ProjectFilesImportJob extends AbstractProjectFilesJob {

	private static final long		serialVersionUID	= 1L;
	private static final Logger		LOG					= Logger.getLogger(ProjectFilesImportJob.class);

	private final DataImportOptions	dataImportOptions;

	ProjectFilesImportJob(	String username,
							Project targetProject,
							CaArrayFileSet fileSet,
							DataImportOptions dataImportOptions) {
		super(username, targetProject, fileSet);
		this.dataImportOptions = dataImportOptions;
	}

	// carpla_begin_mod
	@Override
	void execute () {
		CaArrayFileSet fileSet = getFileSet(getProject());
		try {
			doValidate(fileSet);
			if (fileSet.getStatus().equals(FileStatus.VALIDATED) || fileSet	.getStatus()
																			.equals(FileStatus.VALIDATED_NOT_PARSED)) {

				if (containsRplaIDF(fileSet)) {
					importRplaAnnotation(fileSet);

				} else {
					importAnnotation(fileSet);
					importArrayData(fileSet);
				}
			}
		} finally {
			TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
		}
	}

	// carpla_end_mod

	private void importAnnotation ( CaArrayFileSet fileSet) {
		try {
			getMageTabImporter().importFiles(	getProject(),
												fileSet,
												isReimportingMagetab());
		} catch (MageTabParsingException e) {
			LOG.error(e.getMessage(), e);
		}
		getDaoFactory().getProjectDao().flushSession();
		getDaoFactory().getProjectDao().clearSession();
	}

	// carpla_begin_add
	private void importRplaAnnotation ( CaArrayFileSet fileSet) {
		try {
			getRplaTabImporter().importFiles(getProject(), fileSet);
		} catch (RplaTabParsingException e) {
			LOG.error(e.getMessage(), e);
		}
		LOG.info(" flushing in ProjectFilesImportJob: importRplaAnnotation");
		getDaoFactory().getProjectDao().flushSession();
		LOG.info(" done flushing in ProjectFilesImportJob: importRplaAnnotation");
		LOG.info(" clearing in ProjectFilesImportJob: importRplaAnnotation");

		getDaoFactory().getProjectDao().clearSession();
		LOG.info(" done clearing in ProjectFilesImportJob: importRplaAnnotation");
	}

	RplaTabImporter getRplaTabImporter () {
		return new RplaTabImporter(getRplaTabTranslator(), getDaoFactory());
	}

	private boolean containsRplaIDF ( CaArrayFileSet fileSet) {
		Set<CaArrayFile> files = fileSet.getFiles();
		Iterator itie = files.iterator();
		boolean found_rplaidf = false;
		while (itie.hasNext()) {
			CaArrayFile file = (CaArrayFile) itie.next();
			if (file.getFileType() == FileType.RPLA_TAB_RPLAIDF) {
				found_rplaidf = true;
			}

		}
		return found_rplaidf;
	}

	// carpla_end_add

	private void importArrayData ( CaArrayFileSet fileSet) {
		ArrayDataImporter arrayDataImporter = getArrayDataImporter();
		arrayDataImporter.importFiles(fileSet, this.dataImportOptions);
	}

	@Override
	FileStatus getInProgressStatus () {
		return FileStatus.IMPORTING;
	}

}
