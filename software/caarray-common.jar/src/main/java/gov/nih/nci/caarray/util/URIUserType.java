//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.usertype.UserType;

/**
 * Custom Hibernate Type for URIs. simply stores the URI in a varchar column using its string representation.
 * 
 * @author dkokotov
 */
public class URIUserType implements UserType {
    private static final Logger LOG = Logger.getLogger(URIUserType.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] sqlTypes() {
        return new int[] {Types.VARCHAR };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<?> returnedClass() {
        return URI.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object x, Object y) {
        return (x == y) || (x != null && y != null && (x.equals(y))); // NOPMD - on purpose == comparison
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nullSafeGet(ResultSet inResultSet, String[] names, Object o) throws SQLException {
        final String val = (String) Hibernate.STRING.nullSafeGet(inResultSet, names[0]);
        if (val == null) {
            return null;
        }

        URI uri = null;
        try {
            uri = new URI(val);
        } catch (final URISyntaxException e) {
            LOG.error("problem creating URI from " + val);
        }

        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement inPreparedStatement, Object o, int i) throws SQLException {
        final URI val = (URI) o;
        String uri = null;
        if (val != null) {
            uri = StringUtils.defaultString(val.toString());
        }
        inPreparedStatement.setString(i, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deepCopy(Object o) {
        if (o == null) {
            return null;
        }

        URI deepCopy = null;
        try {
            deepCopy = new URI(o.toString());
        } catch (final URISyntaxException e) {
            LOG.error("Problem creating deepcopy of URI" + o.toString());
        }
        return deepCopy;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object assemble(Serializable cached, Object owner) {
        return deepCopy(cached);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) deepCopy(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object replace(Object original, Object target, Object owner) {
        return deepCopy(original);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }
}
