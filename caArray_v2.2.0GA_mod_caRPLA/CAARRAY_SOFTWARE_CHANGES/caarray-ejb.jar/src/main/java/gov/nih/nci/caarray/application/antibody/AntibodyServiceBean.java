//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.antibody;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.AntibodyDao;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.carpla.domain.antibody.Antibody;

import java.io.File;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.TransactionTimeout;

@Local(AntibodyService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@TransactionTimeout(AntibodyServiceBean.TIMEOUT_SECONDS)
@SuppressWarnings("PMD.CyclomaticComplexity")
public class AntibodyServiceBean implements AntibodyService {

	private static final Logger	LOG				= Logger.getLogger(AntibodyServiceBean.class);
	static final int			TIMEOUT_SECONDS	= 1800;

	private CaArrayDaoFactory	daoFactory		= CaArrayDaoFactory.INSTANCE;

	public List<Antibody> getAntibodies () {
		LogUtil.logSubsystemEntry(LOG);
		List<Antibody> antibodies = getAntibodyDao().getAntibodies();
		LogUtil.logSubsystemExit(LOG);
		return antibodies;
	}

	CaArrayDaoFactory getDaoFactory () {
		return daoFactory;
	}

	private AntibodyDao getAntibodyDao () {
		return getDaoFactory().getAntibodyDao();
	}

}
