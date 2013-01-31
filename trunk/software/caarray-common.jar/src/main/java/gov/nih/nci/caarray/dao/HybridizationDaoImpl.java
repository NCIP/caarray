//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Implementation of the HybridizationDao.
 * 
 * @author dkokotov
 */
public class HybridizationDaoImpl extends AbstractCaArrayDaoImpl implements HybridizationDao {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Hybridization> searchByCriteria(PageSortParams<Hybridization> params,
            HybridizationSearchCriteria criteria) {
        Criteria c = HibernateUtil.getCurrentSession().createCriteria(Hybridization.class);

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
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Hybridization> getWithNoArrayDesign() {
        return HibernateUtil.getCurrentSession().createQuery(
                "select h from " + Hybridization.class.getName()
                        + " h left join h.array a where a is null or a.design is null order by h.name asc").list();
    }
}
