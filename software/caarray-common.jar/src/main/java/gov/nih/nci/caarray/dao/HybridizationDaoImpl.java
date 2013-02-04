//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.Inject;

/**
 * Implementation of the HybridizationDao.
 * 
 * @author dkokotov
 */
public class HybridizationDaoImpl extends AbstractCaArrayDaoImpl implements HybridizationDao {

    /**
     * 
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject
    public HybridizationDaoImpl(CaArrayHibernateHelper hibernateHelper) {
        super(hibernateHelper);
    }
   
   /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Hybridization> searchByCriteria(PageSortParams<Hybridization> params,
            HybridizationSearchCriteria criteria) {
        Criteria c = getCurrentSession().createCriteria(Hybridization.class);

        if (criteria.getExperiment() != null) {
            c.add(Restrictions.eq("experiment", criteria.getExperiment()));
        }
        
        if (!criteria.getBiomaterials().isEmpty()) {
            List<Long> hybIds = new LinkedList<Long>();
            for (AbstractBioMaterial b : criteria.getBiomaterials()) {
                for (Hybridization h : b.getRelatedHybridizations()) {
                    hybIds.add(h.getId());
                }
            }
            if (!hybIds.isEmpty()) {
                c.add(Restrictions.in("id", hybIds));
            } else {
                return Collections.emptyList();
            }
        } 
        
        if (!criteria.getNames().isEmpty()) {
            c.add(Restrictions.in("name", criteria.getNames()));
        }

        c.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            c.setMaxResults(params.getPageSize());
        }
        c.addOrder(toOrder(params));
        return c.list();
    }
}
