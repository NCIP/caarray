//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import java.util.Collections;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

import gov.nih.nci.caarray.dao.HybridizationDao;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria;

/**
 * @author dkokotov
 *
 */
public class HybridizationDaoStub extends AbstractDaoStub implements HybridizationDao {
    /**
     * {@inheritDoc}
     */
    public List<Hybridization> searchByCriteria(PageSortParams<Hybridization> params,
            HybridizationSearchCriteria criteria) {
        return Collections.emptyList();
    }
}
