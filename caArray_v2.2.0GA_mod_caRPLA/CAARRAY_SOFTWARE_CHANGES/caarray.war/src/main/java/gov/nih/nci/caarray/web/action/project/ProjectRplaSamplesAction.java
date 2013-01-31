//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.FactorSortCriterion;
import gov.nih.nci.caarray.domain.search.RplaSampleSortCriterion;
import gov.nih.nci.caarray.domain.search.SampleSortCriterion;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.carpla.domain.rplarray.RplaSample;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getTermsFromCategory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.search.FactorSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

@Validation
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProjectRplaSamplesAction extends AbstractProjectListTabAction {
	private static final long	serialVersionUID	= 1L;

	private RplaSample			currentRplaSample		= new RplaSample();
	

	
	public ProjectRplaSamplesAction() {
		super(	"rplasample",
				new SortablePaginatedList<RplaSample, RplaSampleSortCriterion>(	PAGE_SIZE,
																		RplaSampleSortCriterion.NAME.name(),
																		RplaSampleSortCriterion.class));
	}

	@Override
	protected void doCopyItem ()
								throws ProposalWorkflowException,
									InconsistentProjectStateException
	{
		throw new NotImplementedException("Copying of RplaSamples not supported");
	}

	@Override
	protected Collection<RplaSample> getCollection () {
		System.out.println("In ProjectRplaSamplesAction getCollection()");
		System.out.println("There are this many rplasamples:" + getProject()	.getExperiment()
																			.getRplaSamples()
																			.size());
		return getProject().getExperiment().getRplaSamples();

	}

	@Override
	protected PersistentObject getItem () {
		System.out.println("In ProjectRplaSamplesAction getItem()");
		return currentRplaSample;
	}

	
	@CustomValidator(type = "hibernate")
	public RplaSample getCurrentRplaSample () {
		return currentRplaSample;
	}

	public void setCurrentRplaSample( RplaSample rplaSample) {
		this.currentRplaSample = rplaSample;
	}
	
	
	
	
	
	
	
	
}
