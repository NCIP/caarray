//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.protocol</code> package.
 *
 * @author Rashmi Srinivasa
 */
class ProtocolDaoImpl extends AbstractCaArrayDaoImpl implements ProtocolDao {

    private static final String NAME_FIELD = "name";
    private static final Logger LOG = Logger.getLogger(ProtocolDaoImpl.class);

    /**
     * Returns the <code>Protocol</code> with the id given or null if none exists.
     *
     * @param id get <code>Protocol</code> matching this id
     * @return the <code>Protocol</code> or null.
     */
    public Protocol getProtocol(long id) {
        return (Protocol) getCurrentSession().get(Protocol.class, id);
    }

    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, TermSource source) {
        if (StringUtils.isEmpty(name) || source == null || source.getId() == null) {
            // all of these fields are required
            return null;
        }
        String hsql = "from " + Protocol.class.getName() + " where name = :name and source = :source";
        Query q = getCurrentSession().createQuery(hsql);
        q.setString(NAME_FIELD, name);
        q.setEntity("source", source);
        return (Protocol) q.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Protocol> getProtocols(Term type, String name) {
        Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(Protocol.class);
        criteria.add(Restrictions.eq("type", type));
        if (StringUtils.isNotBlank(name)) {
            criteria.add(Restrictions.like(NAME_FIELD, name, MatchMode.START).ignoreCase());
        }
        criteria.addOrder(Order.asc(NAME_FIELD));
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    public Parameter getParameter(String name, Protocol protocol) {
        if (protocol == null || protocol.getId() == null) {
            return null;
        }
        String hsql = "from " + Parameter.class.getName() + " where name = :name and protocol = :protocol";
        Query q = getCurrentSession().createQuery(hsql);
        q.setString(NAME_FIELD, name);
        q.setEntity("protocol", protocol);
        return (Parameter) q.uniqueResult();
    }

    @Override
    Logger getLog() {
        return LOG;
    }
}
