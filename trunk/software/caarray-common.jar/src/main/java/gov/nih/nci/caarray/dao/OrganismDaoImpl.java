//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.List;

import org.hibernate.Query;

/**
 * DAO for Organism entities .
 *
 * @author Dan Kokotov
 */
class OrganismDaoImpl extends AbstractCaArrayDaoImpl implements OrganismDao {
    private static final String UNCHECKED = "unchecked";

    /**
     * {@inheritDoc}
     */
    public Organism getOrganism(long id) {
        return (Organism) getCurrentSession().get(Organism.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Organism> getAllOrganisms() {
        String query = "from " + Organism.class.getName() + " o order by o.scientificName asc";
        return getCurrentSession().createQuery(query).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Organism> searchForOrganismNames(String keyword) {
        String sb = "SELECT DISTINCT o FROM " + Organism.class.getName()
            + " o"
            + (keyword == null ?"" : " WHERE o.scientificName like :keyword OR o.commonName like :keyword")
            + " ORDER BY o.scientificName";

        Query q = HibernateUtil.getCurrentSession().createQuery(sb);

        if (keyword != null) {
            q.setString("keyword", keyword + "%");
        }

        return q.list();
    }
}
