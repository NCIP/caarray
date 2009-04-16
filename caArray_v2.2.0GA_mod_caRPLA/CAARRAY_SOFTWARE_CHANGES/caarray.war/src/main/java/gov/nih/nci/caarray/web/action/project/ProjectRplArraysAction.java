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
import gov.nih.nci.caarray.domain.search.RplArraySortCriterion;
import gov.nih.nci.caarray.domain.search.SampleSortCriterion;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.carpla.domain.rplarray.RplArray;
import gov.nih.nci.carpla.domain.rplarray.RplaSample;

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
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.search.FactorSortCriterion;

public class ProjectRplArraysAction extends AbstractProjectListTabAction {
	private static final long	serialVersionUID	= 1L;

	// private RplaSample currentSample = new Sample();
	// private List<Source> itemsToAssociate = new ArrayList<Source>();
	// private List<Source> itemsToRemove = new ArrayList<Source>();

	/**
	 * Default constructor.
	 */
	public ProjectRplArraysAction() {
		super(	"rplarray",
				new SortablePaginatedList<RplArray, RplArraySortCriterion>(	PAGE_SIZE,
																			RplArraySortCriterion.NAME.name(),
																			RplArraySortCriterion.class));
	}

	protected void doCopyItem ()
								throws ProposalWorkflowException,
									InconsistentProjectStateException
	{
	// TODO Auto-generated method stub

	}

	@Override
	protected Collection getCollection () {
		System.out.println("In ProjectRplArraysAction getCollection()");
		System.out.println("There are this many rplarrays:" + getProject()	.getExperiment()
																			.getRplArrays()
																			.size());

		return getProject().getExperiment().getRplArrays();

	}

	@Override
	protected PersistentObject getItem () {
		// TODO Auto-generated method stub
		return null;
	}

}
