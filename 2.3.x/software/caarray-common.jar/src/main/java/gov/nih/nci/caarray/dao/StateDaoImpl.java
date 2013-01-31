//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.state.State;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
public class StateDaoImpl extends AbstractCaArrayDaoImpl implements StateDao {
    private static final Logger LOG = Logger.getLogger(StateDaoImpl.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<State> getStates() {
        String query = "from " + State.class.getName() + " s order by s.name asc";
        return getCurrentSession().createQuery(query).setCacheable(true).list();
    }

    @Override
    Logger getLog() {
        return LOG;
    }
}
