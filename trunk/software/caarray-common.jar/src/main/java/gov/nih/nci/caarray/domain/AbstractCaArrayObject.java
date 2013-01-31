//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base class for all persistent caArray domain objects.
 */
@MappedSuperclass
public abstract class AbstractCaArrayObject implements PersistentObject {

    private static final long serialVersionUID = 2732929116326299995L;

    /**
     * The default column size for string columns in the db.
     */
    public static final int DEFAULT_STRING_COLUMN_SIZE = 254;

    /**
     * The column size for large string columns in the db.
     */
    protected static final int LARGE_TEXT_FIELD_LENGTH = 2000;

    private Long id;
    private String caBigId;

    /**
     * Default hibernate batch size.
     */
    public static final int DEFAULT_BATCH_SIZE = 20;

    /**
     * Returns the id.
     *
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     * @deprecated should only be used by castor and hibernate
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Return the set of security policies that should be applied to this object at hibernate load time.
     * These policies will be applied as port of a hibernate post-load event listener, so that by the time
     * a query that results in the load of this object is finished and returns to the program, these policies
     * will have been applied.
     * 
     * Note that because the policies will be applied while hibernate is in the middle of loading the objects
     * from the database, unexpected behavior may occur. For example, asssociated objects and collections may not
     * yet be loaded. The security policies must be designed carefully in awareness of this behavior.
     * 
     * @param currentUser the current user.
     * @return the set of policies to apply to this object, given the current user.
     */
    public Set<SecurityPolicy> getPostLoadSecurityPolicies(User currentUser) {
        return Collections.emptySet();
    }

    /**
     * Return the set of security policies that should be applied to this object prior to it being returned
     * as part of a return value for a remote API call.
     * 
     * @param currentUser the current user.
     * @return the set of policies to apply to this object, given the current user.
     */
    public Set<SecurityPolicy> getRemoteApiSecurityPolicies(User currentUser) {
        return Collections.emptySet();
    }

    /**
     * The default comparison uses the id.
     * @param o other object
     * @return equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof AbstractCaArrayObject)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (id == null) {
            // by default, two transient instances cannot ever be equal
            return false;
        }

        AbstractCaArrayObject e = (AbstractCaArrayObject) o;
        return (id.equals(e.getId()) && getClass().isInstance(e));
    }

    /**
     * Default hashCode goes off of id.
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }

        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        stringBuffer.append(getClass().getSimpleName());
        stringBuffer.append("] id=");
        stringBuffer.append(id);
        return stringBuffer.toString();
    }

    /**
     * @return the gridIdentifier
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE, name = "bigid")
    public String getCaBigId() {
        return caBigId;
    }

    /**
     * @param gridIdentifier the gridIdentifier to set
     */
    public void setCaBigId(String gridIdentifier) {
        this.caBigId = gridIdentifier;
    }

}
