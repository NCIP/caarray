//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Base class for all caArray domain entities.
 *
 * @author ETavela
 */
@MappedSuperclass
public abstract class AbstractCaArrayEntity extends AbstractCaArrayObject {
    private static final long serialVersionUID = 2732929116326299995L;

    /**
     * LSID Authority for CAARRAY. Used for entities which require an LSID but do not have one assigned
     * from elsewhere
     */
    public static final String CAARRAY_LSID_AUTHORITY = "caarray.nci.nih.gov";

    /**
     * LSID Namespace for CAARRAY. Used for entities which require an LSID but do not have one assigned
     * from elsewhere
     */
    public static final String CAARRAY_LSID_NAMESPACE = "domain";

    private String lsidAuthority;
    private String lsidNamespace;
    private String lsidObjectId;

    /**
     * Default constructor.  All lsid information will be blank.
     */
    public AbstractCaArrayEntity() {
        // blank lsid information / hibernate constructor
    }

    /**
     * Constructor with LSID information.
     *
     * @param lsidAuthority authority
     * @param lsidNamespace namespace
     * @param lsidObjectId object id
     */
    protected AbstractCaArrayEntity(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        setLsidAuthority(lsidAuthority);
        setLsidNamespace(lsidNamespace);
        setLsidObjectId(lsidObjectId);
    }

    /**
     * Returns the LSID authority.
     *
     * @return the LSID authority
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getLsidAuthority() {
        return lsidAuthority;
    }

    /**
     * Sets the LSID authority.
     *
     * @param lsidAuthorityVal the LSID authority to set
     */
    private void setLsidAuthority(String lsidAuthorityVal) {
        this.lsidAuthority = lsidAuthorityVal;
    }

    /**
     * Returns the LSID namespace.
     *
     * @return the LSID namespace
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getLsidNamespace() {
        return lsidNamespace;
    }

    /**
     * Sets the LSID namespace.
     *
     * @param lsidNamespaceVal the LSID namespace to set
     */
    private void setLsidNamespace(String lsidNamespaceVal) {
        this.lsidNamespace = lsidNamespaceVal;
    }

    /**
     * Returns the LSID object ID.
     *
     * @return the LSID object ID
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getLsidObjectId() {
        return lsidObjectId;
    }

    /**
     * Sets the LSID object ID.
     *
     * @param lsidObjectIdVal the LSID object ID to set
     */
    private void setLsidObjectId(String lsidObjectIdVal) {
        this.lsidObjectId = lsidObjectIdVal;
    }

    /**
     * Sets the LSID components for this entity.
     * If the authority and namespace are both absent, the default caArray authority
     * and namespace will be used. The LSID string is of the form authority:namespace:objectId
     * where authority can be absent, or authority and namespace can both be absent.
     *
     * @param lsidString the LSID string
     */
    public void setLsidForEntity(String lsidString) {
        setLsid(new LSID(lsidString));
    }

    /**
     * Set the LSID for this entity.
     * @param lsid the LSID to set.
     */
    public void setLsid(LSID lsid) {
        if (lsid != null) {
            setLsidAuthority(lsid.getAuthority());
            setLsidNamespace(lsid.getNamespace());
            setLsidObjectId(lsid.getObjectId());
        } else {
            setLsidAuthority(null);
            setLsidNamespace(null);
            setLsidObjectId(null);
        }
    }

    /**
     * Set the LSID for this entity.
     * @param lsid the LSID to set.
     */
    public void setLsid(String lsid) {
        setLsid(lsid == null ? null : new LSID(lsid));
    }

    /**
     * Returns the concatenated the LSID for this entity.
     *
     * @return the LSID.
     */
    @Transient
    public String getLsid() {
        return "URN:LSID:" + getLsidAuthority() + ":" + getLsidNamespace() + ":" + getLsidObjectId();
    }
}
