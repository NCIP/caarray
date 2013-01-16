//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.audit;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.ActionSupport;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.audit.AuditLogService;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import gov.nih.nci.caarray.domain.search.AuditLogSortCriterion;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.displaytag.properties.SortOrderEnum;

/**
 *
 * @author gax
 */
public class AuditAction extends ActionSupport {
    private static final int SEARCH_PAGE_SIZE = 20;

    private final SortablePaginatedList<AuditLogRecord, AuditLogSortCriterion> results = new SortablePaginatedList(
            SEARCH_PAGE_SIZE, AuditLogSortCriterion.DATE.name(), AuditLogSortCriterion.class);

    private final AuditLogSearchCriteria criteria = new AuditLogSearchCriteria();

    /**
     * default ctor.
     */
    public AuditAction() {
        results.setSortDirection(SortOrderEnum.DESCENDING);
    }

    /**
     * @return search results.
     */
    public SortablePaginatedList<AuditLogRecord, AuditLogSortCriterion> getResults() {
        return results;
    }

    /**
     * @return search criteria.
     */
    public AuditLogSearchCriteria getCriteria() {
        return criteria;
    }

    /**
     * @return SUCCESS.
     */
    @SkipValidation
    public String list() {
        AuditLogService service = ServiceLocatorFactory.getAuditLogService();
        results.setFullListSize(service.getRecordsCount(criteria));
        results.setList(service.getRecords(criteria, results.getPageSortParams()));
        return SUCCESS;
    }

    /**
     * @return SUCCESS.
     */
    @SkipValidation
    public String logs() {
        return list();
    }
   
}
